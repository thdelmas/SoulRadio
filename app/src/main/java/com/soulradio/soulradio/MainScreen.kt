package com.soulradio.soulradio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import kotlinx.coroutines.delay

// Sentinel key for the first-launch hint inside the caption Crossfade —
// can't collide with any real Frequency.key (which are all numeric).
private const val CAPTION_HINT_KEY = "__hint__"

/**
 * The dial home — shared by `dj` (auto on) and `dial` (auto off) modes
 * in the [ModeStrip] vocabulary. The strip lives above this screen in
 * [MainActivity], so this composable starts directly with the dial layout
 * and consumes [autoOn] from the parent rather than owning that state.
 *
 * The tap-pauses-auto interaction stays local: when the listener taps a
 * tone while DJ is on, that tap silently flips `autoOn` to false (via
 * [onSetAuto]) and bumps the [autoPausedAt] notice slot below the dial.
 *
 * [dialPillTrigger] is a counter the activity bumps when the listener
 * presses the strip's `dial` pill — the explicit "silence + dial mode"
 * intent. Each increment clears the current selection. Initial value of
 * 0 is a no-op on first composition; the side-effect `setAuto(false)`
 * from a dial tap does NOT touch this counter, so a tapped tone keeps
 * playing through the auto-pause.
 */
@Composable
internal fun MainScreen(
    autoOn: Boolean,
    onSetAuto: (Boolean) -> Unit,
    dialPillTrigger: Int,
) {
    val context = LocalContext.current
    val engine = remember { TrackEngine(context) }
    var selected by remember {
        mutableStateOf(if (autoOn) Frequencies.forNow(context) else null)
    }
    // Bumped only when DJ flips off as a *side effect* of a dial tap —
    // not when the user explicitly presses the dial pill in the strip.
    // The notice exists because that side-effect is silent and easy to
    // miss.
    var autoPausedAt by remember { mutableStateOf<Long?>(null) }

    DisposableEffect(Unit) { onDispose { engine.release() } }

    // The strip's `dial` pill press signals "silence + dial mode" — clear
    // any current selection. Initial composition runs with trigger = 0,
    // which we ignore so a fresh launch never silences a service-side
    // playing track. Subsequent increments fire the clear.
    LaunchedEffect(dialPillTrigger) {
        if (dialPillTrigger > 0) {
            selected = null
            engine.selectFrequency(null)
        }
    }

    // The engine reports what is *actually* playing in the service — the
    // source of truth for the dial highlight after the activity has been
    // recreated, and the signal that lets us drop the 60 s DJ poll.
    val playing by engine.currentFrequency

    // After the controller binds, if the activity has no selection but a
    // track is playing in the service (the user reopened the app while
    // audio kept going), adopt that tone so the dial reflects the room.
    LaunchedEffect(Unit) {
        snapshotFlow { engine.currentFrequency.value }
            .collect { freq ->
                if (freq != null && selected == null && !autoOn) {
                    selected = freq
                }
            }
    }

    // While DJ is on, mirror what the service is playing onto the dial.
    // Falls back to the schedule until the controller binds, so the dial
    // is correct on the very first frame.
    LaunchedEffect(autoOn, playing) {
        if (!autoOn) return@LaunchedEffect
        val target = playing ?: Frequencies.forNow(context)
        if (selected?.key != target.key) selected = target
    }

    // One shared breath drives every alive element (the playing node's
    // ring), so the whole dial pulses in unison. ~4 s cycle, faint — a
    // pilot light, not a pulse animation. Passed as State<Float> and read
    // inside drawBehind so frame-rate updates invalidate only the draw
    // layer.
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

    // First-launch hint. DJ starts on the very first open, so the
    // empty-state caption ("tap a tone to listen…") never renders for a
    // new user — they see a glowing dial of unexplained numbers. Borrow
    // the caption slot for ~6 s on launch #1 to point at the book glyph,
    // then dissolve back to the normal now-playing line.
    var firstLaunchHint by remember { mutableStateOf(HintStore.shouldShow(context)) }
    LaunchedEffect(Unit) {
        if (!firstLaunchHint) return@LaunchedEffect
        HintStore.markShown(context)
        delay(6000)
        firstLaunchHint = false
    }

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
        if (autoOn) {
            onSetAuto(false)
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
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))

        Dial(
            selectedKey = selected?.key,
            tunedKeys = engine.tunedKeys,
            pulse = pulse,
            onTap = onTap,
        )

        Spacer(Modifier.height(32.dp))
        DialDivider()
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
            firstLaunchHint = firstLaunchHint,
        )

        Spacer(Modifier.height(28.dp))
    }

    if (showContribution) {
        ContributionPopup(onDismiss = { showContribution = false })
    }
}

@Composable
private fun DialDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.4f)
            .height(1.dp)
            .background(Hairline),
    )
}

@Composable
private fun AutoPausedNotice(triggerKey: Long?) {
    // A 22 dp slot reserved above the caption — the notice fades in for
    // ~2 s when DJ flips off as a side-effect of a dial tap, then
    // dissolves. The slot is always present so the caption block doesn't
    // jiggle when the notice appears or leaves; the visual cost is a
    // small fixed gap.
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
                text = "dj · paused",
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
    firstLaunchHint: Boolean,
) {
    // Title is now always under each dial node, so the caption drops the
    // redundant title line and shows only what the node can't: now-playing
    // details, the "untuned" note, or the empty-state prompt.
    // 56dp min reserves room for the tallest caption (work + performer
    // with their spacer), keeping the dial from jumping when a tone is
    // selected.
    val target: String? = when {
        firstLaunchHint -> CAPTION_HINT_KEY
        else -> selected?.key
    }
    Crossfade(
        targetState = target,
        animationSpec = tween(700),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        label = "caption",
    ) { key ->
        val current = key?.takeIf { it != CAPTION_HINT_KEY }?.let { Frequencies.byKey(it) }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (key == CAPTION_HINT_KEY) {
                // First-launch hint: points at the book glyph so the dial
                // numbers don't read as gibberish. Same letter-spacing as
                // the empty-state line so it sits at the same visual
                // weight.
                Text(
                    text = "tap the book to learn these tones",
                    color = Mute,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                )
                return@Column
            }
            if (current == null) {
                // Empty-state caption is instructional rather than
                // decorative (telling a first-time user what DJ and a
                // tap actually do), so it gets a tighter letter-spacing
                // than the now-playing line and is allowed to wrap — the
                // 56 dp slot already reserves room for two lines.
                Text(
                    text = "tap a tone to listen · or leave it on DJ",
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
                // Show the actually-playing track when its band matches
                // the selected band; otherwise fall back to the band's
                // first listed recording so the card never goes blank
                // during the brief window before the player reports its
                // current item.
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
