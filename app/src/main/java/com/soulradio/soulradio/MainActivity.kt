package com.soulradio.soulradio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        PlaybackService.startIfFirstLaunch(this)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize(), color = Bg) {
                    var showNotes by remember { mutableStateOf(false) }
                    // The device back gesture should return to the radio when
                    // notes are open; otherwise it falls through to default
                    // (finish the activity), which is what one expects on the
                    // root screen.
                    BackHandler(enabled = showNotes) { showNotes = false }
                    Crossfade(
                        targetState = showNotes,
                        animationSpec = tween(500),
                        label = "screen",
                    ) { notes ->
                        if (notes) {
                            AboutScreen(onClose = { showNotes = false })
                        } else {
                            RadioScreen(onOpenNotes = { showNotes = true })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RadioScreen(onOpenNotes: () -> Unit) {
    val context = LocalContext.current
    val engine = remember { TrackEngine(context) }
    var auto by remember { mutableStateOf(PlaybackService.isAutoEnabled(context)) }
    var selected by remember {
        mutableStateOf(if (auto) Frequencies.forNow(context) else null)
    }
    // Bumped only when AUTO flips off as a *side effect* of a dial tap —
    // not when the user explicitly toggles the pill. The notice exists
    // because that side-effect is silent and easy to miss.
    var autoPausedAt by remember { mutableStateOf<Long?>(null) }

    DisposableEffect(Unit) { onDispose { engine.release() } }

    // The engine reports what is *actually* playing in the service — the
    // source of truth for the dial highlight after the activity has been
    // recreated, and the signal that lets us drop the 60 s AUTO poll.
    val playing by engine.currentFrequency

    // After the controller binds, if the activity has no selection but a
    // track is playing in the service (the user reopened the app while
    // audio kept going), adopt that tone so the dial reflects the room.
    LaunchedEffect(Unit) {
        snapshotFlow { engine.currentFrequency.value }
            .collect { freq ->
                if (freq != null && selected == null && !auto) {
                    selected = freq
                }
            }
    }

    // While AUTO is on, mirror what the service is playing onto the dial.
    // Falls back to the schedule until the controller binds, so the dial
    // is correct on the very first frame.
    LaunchedEffect(auto, playing) {
        if (!auto) return@LaunchedEffect
        val target = playing ?: Frequencies.forNow(context)
        if (selected?.key != target.key) selected = target
    }

    // One shared breath drives every alive element (the playing node's ring
    // and the AUTO dot), so the whole dial pulses in unison. ~4s cycle, faint
    // — a pilot light, not a pulse animation. Passed as State<Float> and read
    // inside drawBehind so frame-rate updates invalidate only the draw layer.
    val transition = rememberInfiniteTransition(label = "breath")
    val pulse = transition.animateFloat(
        initialValue = 0.30f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alpha",
    )

    // Contribution popup: a 90-day, paused-only ask. Settle 5 s after the
    // screen appears (so the controller has bound and reported state, and
    // the user has had a beat in the room) before deciding. Marking the
    // popup shown happens up front so the cadence resets whether the user
    // actions or dismisses — matches the portfolio guide's spec.
    var showContribution by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(5000)
        val isPaused = engine.currentFrequency.value == null
        if (ContributionStore.shouldOffer(context, isPaused)) {
            ContributionStore.markShown(context)
            showContribution = true
        }
    }

    val onTap: (Frequency) -> Unit = { freq ->
        if (auto) {
            auto = false
            PlaybackService.setAuto(context, false)
            autoPausedAt = System.currentTimeMillis()
        }
        if (selected?.key == freq.key) {
            selected = null
            engine.selectFrequency(null)
        } else {
            selected = freq
            engine.selectFrequency(freq)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            // AUTO pill stays centred (the eye expects it there); the notes
            // button sits at the trailing edge — the only door to the docs
            // that explain what these numbers mean, so it stays in the gold
            // family rather than reading as decoration.
            Box(modifier = Modifier.align(Alignment.Center)) {
                AutoPill(
                    auto = auto,
                    pulse = pulse,
                    pausedAt = autoPausedAt,
                    onToggle = {
                        val next = !auto
                        auto = next
                        PlaybackService.setAuto(context, next)
                        if (!next) {
                            selected = null
                            engine.selectFrequency(null)
                        }
                    },
                )
            }
            // Bordered pill — same idiom as the AUTO pill, smaller, so it
            // reads as an obvious tap target rather than decorative copy.
            // Hairline GoldDim border keeps it visually paired with AUTO
            // without competing for the eye.
            Text(
                text = "notes",
                color = Gold,
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(CircleShape)
                    .border(1.dp, GoldDim, CircleShape)
                    .clickable { onOpenNotes() }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            )
        }

        Spacer(Modifier.weight(1f))

        Dial(
            selectedKey = selected?.key,
            tunedKeys = engine.tunedKeys,
            pulse = pulse,
            onTap = onTap,
        )

        Spacer(Modifier.height(32.dp))
        Divider()
        Spacer(Modifier.height(24.dp))

        CompanionsRow(
            selectedKey = selected?.key,
            tunedKeys = engine.tunedKeys,
            pulse = pulse,
            onTap = onTap,
        )

        Spacer(Modifier.weight(1f))

        AutoPausedNotice(triggerKey = autoPausedAt)
        val playingTrack by engine.currentTrack
        Caption(
            selected = selected,
            isTuned = selected?.let { engine.isTuned(it) } ?: false,
            playingTrack = playingTrack,
            playingFrequency = playing,
        )

        Spacer(Modifier.height(28.dp))
    }

    if (showContribution) {
        ContributionPopup(onDismiss = { showContribution = false })
    }
}

@Composable
private fun AutoPill(
    auto: Boolean,
    pulse: State<Float>,
    pausedAt: Long?,
    onToggle: () -> Unit,
) {
    val textColor by animateColorAsState(
        targetValue = if (auto) Gold else GoldDim,
        animationSpec = tween(700),
        label = "auto-text",
    )
    // When AUTO is silently paused by a dial tap, the AutoPausedNotice
    // appears far from the pill. A brief outline ring on the pill itself
    // links the notice to its recovery affordance — one ~700ms fade,
    // alpha only, no copy or layout change.
    val highlight = remember { Animatable(0f) }
    LaunchedEffect(pausedAt) {
        if (pausedAt == null) return@LaunchedEffect
        highlight.snapTo(0.6f)
        highlight.animateTo(0f, animationSpec = tween(700))
    }
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .border(1.dp, Gold.copy(alpha = highlight.value), CircleShape)
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .drawBehind {
                    val color =
                        if (auto) Gold.copy(alpha = pulse.value)
                        else GoldDim.copy(alpha = 0.6f)
                    drawCircle(color = color, radius = size.minDimension / 2)
                },
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = if (auto) {
                "AUTO · ${Frequencies.currentHour().toString().padStart(2, '0')}h"
            } else "auto · off",
            color = textColor,
            fontSize = 11.sp,
            fontWeight = if (auto) FontWeight.Medium else FontWeight.Light,
            letterSpacing = 3.sp,
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.4f)
            .height(1.dp)
            .background(Hairline),
    )
}

@Composable
private fun AutoPausedNotice(triggerKey: Long?) {
    // A 22dp slot reserved above the caption — the notice fades in for ~2s
    // when AUTO flips off as a side-effect of a dial tap, then dissolves.
    // The slot is always present so the caption block doesn't jiggle when
    // the notice appears or leaves; the visual cost is a small fixed gap.
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(triggerKey) {
        if (triggerKey == null) return@LaunchedEffect
        visible = true
        delay(2000)
        visible = false
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(22.dp),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(700)),
        ) {
            Text(
                text = "auto · paused",
                color = MuteSoft,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
            )
        }
    }
}

@Composable
private fun Caption(
    selected: Frequency?,
    isTuned: Boolean,
    playingTrack: NowPlaying?,
    playingFrequency: Frequency?,
) {
    // Title is now always under each dial node, so the caption drops the
    // redundant title line and shows only what the node can't: now-playing
    // details, the "untuned" note, or the empty-state prompt.
    // 56dp min reserves room for the tallest caption (work + performer with
    // their spacer), keeping the dial from jumping when a tone is selected.
    Crossfade(
        targetState = selected?.key,
        animationSpec = tween(700),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        label = "caption",
    ) { key ->
        val current = key?.let { Frequencies.byKey(it) }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (current == null) {
                // Empty-state caption is instructional rather than decorative
                // (telling a first-time user what AUTO and a tap actually do),
                // so it gets a tighter letter-spacing than the now-playing
                // line and is allowed to wrap — the 56 dp slot already
                // reserves room for two lines.
                Text(
                    text = "tap a tone to listen · or leave it on AUTO",
                    color = Mute,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                )
                return@Column
            }
            if (!isTuned) {
                Text(
                    text = "silent · recording not yet bundled",
                    color = Mute,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                )
            } else {
                // Show the actually-playing track when its band matches the
                // selected band; otherwise fall back to the band's first
                // listed recording so the card never goes blank during the
                // brief window before the player reports its current item.
                val np = playingTrack
                    ?.takeIf { playingFrequency?.key == current.key }
                    ?: current.tracks.firstOrNull()
                np?.let {
                    Text(
                        text = it.work,
                        color = MuteSoft,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = it.performer,
                        color = Mute,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
