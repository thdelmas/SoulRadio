package com.soulradio.soulradio

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                    RadioScreen()
                }
            }
        }
    }
}

@Composable
private fun RadioScreen() {
    val context = LocalContext.current
    val engine = remember { TrackEngine(context) }
    var auto by remember { mutableStateOf(PlaybackService.isAutoEnabled(context)) }
    var selected by remember {
        mutableStateOf(if (auto) Frequencies.forNow() else null)
    }
    // Bumped only when AUTO flips off as a *side effect* of a dial tap —
    // not when the user explicitly toggles the pill. The notice exists
    // because that side-effect is silent and easy to miss.
    var autoPausedAt by remember { mutableStateOf<Long?>(null) }
    // Long-press reveals a tone's title in the caption without selecting
    // or playing it. The previewAt timestamp keys the auto-clear coroutine
    // so a fresh long-press on another node resets the 2.5s window.
    var previewed by remember { mutableStateOf<Frequency?>(null) }
    var previewAt by remember { mutableStateOf<Long?>(null) }

    DisposableEffect(Unit) { onDispose { engine.release() } }

    // While AUTO is on, mirror the schedule into UI state so the dial
    // highlights the current band. PlaybackService drives the actual
    // playback — see PlaybackService.enableAuto.
    LaunchedEffect(auto) {
        if (!auto) return@LaunchedEffect
        while (true) {
            val target = Frequencies.forNow()
            if (selected?.key != target.key) {
                selected = target
            }
            delay(60_000)
        }
    }

    LaunchedEffect(previewAt) {
        if (previewAt == null) return@LaunchedEffect
        delay(2500)
        previewed = null
        previewAt = null
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

    val onTap: (Frequency) -> Unit = { freq ->
        if (auto) {
            auto = false
            PlaybackService.setAuto(context, false)
            autoPausedAt = System.currentTimeMillis()
        }
        previewed = null
        previewAt = null
        if (selected?.key == freq.key) {
            selected = null
            engine.selectFrequency(null)
        } else {
            selected = freq
            engine.selectFrequency(freq)
        }
    }
    val onLongPress: (Frequency) -> Unit = { freq ->
        previewed = freq
        previewAt = System.currentTimeMillis()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(20.dp))
        AutoPill(
            auto = auto,
            pulse = pulse,
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

        Spacer(Modifier.weight(1f))

        Dial(
            selectedKey = selected?.key,
            tunedKeys = engine.tunedKeys,
            pulse = pulse,
            onTap = onTap,
            onLongPress = onLongPress,
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
        Caption(
            selected = selected,
            isTuned = selected?.let { engine.isTuned(it) } ?: false,
            previewed = previewed,
        )

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun AutoPill(auto: Boolean, pulse: State<Float>, onToggle: () -> Unit) {
    val textColor by animateColorAsState(
        targetValue = if (auto) Gold else GoldDim,
        animationSpec = tween(700),
        label = "auto-text",
    )
    Row(
        modifier = Modifier
            .clip(CircleShape)
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
private fun Caption(selected: Frequency?, isTuned: Boolean, previewed: Frequency?) {
    // Preview takes precedence over selection: a long-press peek shouldn't
    // be hidden by whatever is currently playing. Details (work/performer
    // or "untuned") only render for an actual selection — a previewed tone
    // is a peek, not a play.
    val displayed = previewed ?: selected
    val showDetails = previewed == null && selected != null
    Crossfade(
        targetState = displayed?.key,
        animationSpec = tween(700),
        modifier = Modifier.fillMaxWidth(),
        label = "caption",
    ) { key ->
        val current = key?.let { Frequencies.byKey(it) }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (current == null) {
                Text(
                    text = "tap a tone · or leave it",
                    color = Mute,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                )
                return@Column
            }
            Text(
                text = current.title,
                color = Gold,
                fontSize = 13.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Light,
            )
            if (!showDetails) return@Column
            Spacer(Modifier.height(10.dp))
            if (!isTuned) {
                Text(
                    text = "untuned · recording forthcoming",
                    color = Mute,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                )
            } else {
                current.nowPlaying?.let { np ->
                    Text(
                        text = np.work,
                        color = MuteSoft,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = np.performer,
                        color = Mute,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
