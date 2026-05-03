package com.soulradio.soulradio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Bg = Color.Black
private val Gold = Color(0xFFD4AF37)
private val GoldDim = Color(0xFF6E5A1C)
private val Slate = Color(0xFF1A1A1A)
private val SlateDeep = Color(0xFF0E0E0E)
private val Mute = Color(0xFF555555)
private val MuteSoft = Color(0xFF888888)
private val Hairline = Color(0xFF222222)

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

        Caption(
            selected = selected,
            isTuned = selected?.let { engine.isTuned(it) } ?: false,
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
private fun Dial(
    selectedKey: String?,
    tunedKeys: Set<String>,
    pulse: State<Float>,
    onTap: (Frequency) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Frequencies.dial.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                row.forEach { freq ->
                    DialNode(
                        freq = freq,
                        selected = selectedKey == freq.key,
                        tuned = freq.key in tunedKeys,
                        pulse = pulse,
                        onTap = onTap,
                    )
                }
            }
        }
    }
}

@Composable
private fun DialNode(
    freq: Frequency,
    selected: Boolean,
    tuned: Boolean,
    pulse: State<Float>,
    onTap: (Frequency) -> Unit,
) {
    val colors = nodeColors(selected, tuned)
    Box(
        modifier = Modifier
            .size(98.dp)
            .drawBehind {
                if (selected) {
                    drawCircle(
                        color = Gold.copy(alpha = pulse.value),
                        radius = size.minDimension / 2 - 0.5.dp.toPx(),
                        style = Stroke(width = 1.dp.toPx()),
                    )
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
                .background(colors.bg)
                .border(colors.borderWidth, colors.border, CircleShape)
                .clickable { onTap(freq) },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = freq.label,
                color = colors.fg,
                fontSize = 19.sp,
                fontWeight = FontWeight.Light,
            )
        }
    }
}

private data class NodeColors(
    val bg: Color,
    val fg: Color,
    val border: Color,
    val borderWidth: Dp,
)

@Composable
private fun nodeColors(selected: Boolean, tuned: Boolean): NodeColors {
    // Four states: selected+tuned (gold disc), selected+untuned (dark with
    // thick gold outline — distinct so an audio-less tap doesn't visually
    // lie), tuned-idle (slate + thin gold), untuned-idle (deepest slate +
    // dim gold). Colour transitions ease over ~700ms — half the audio
    // crossfade — so the eye leads the ear by a hair instead of slamming.
    val targetBg = when {
        selected && tuned -> Gold
        tuned             -> Slate
        else              -> SlateDeep
    }
    val targetFg = when {
        selected && tuned -> Color.Black
        tuned || selected -> Gold
        else              -> GoldDim
    }
    val targetBorder = when {
        selected && tuned -> Color.Transparent
        selected          -> Gold
        tuned             -> Gold
        else              -> GoldDim
    }
    val bg by animateColorAsState(targetBg, tween(700), label = "node-bg")
    val fg by animateColorAsState(targetFg, tween(700), label = "node-fg")
    val border by animateColorAsState(targetBorder, tween(700), label = "node-border")
    val borderWidth = if (selected && !tuned) 2.dp else 1.dp
    return NodeColors(bg, fg, border, borderWidth)
}

@Composable
private fun CompanionsRow(
    selectedKey: String?,
    tunedKeys: Set<String>,
    pulse: State<Float>,
    onTap: (Frequency) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Frequencies.companions.forEach { freq ->
            CompanionNode(
                freq = freq,
                selected = selectedKey == freq.key,
                tuned = freq.key in tunedKeys,
                pulse = pulse,
                onTap = onTap,
            )
        }
    }
}

@Composable
private fun CompanionNode(
    freq: Frequency,
    selected: Boolean,
    tuned: Boolean,
    pulse: State<Float>,
    onTap: (Frequency) -> Unit,
) {
    val colors = nodeColors(selected, tuned)
    val titleColor by animateColorAsState(
        targetValue = if (selected) Gold else MuteSoft,
        animationSpec = tween(700),
        label = "companion-title",
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .drawBehind {
                    if (selected) {
                        drawCircle(
                            color = Gold.copy(alpha = pulse.value),
                            radius = size.minDimension / 2 - 0.5.dp.toPx(),
                            style = Stroke(width = 1.dp.toPx()),
                        )
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(colors.bg)
                    .border(colors.borderWidth, colors.border, CircleShape)
                    .clickable { onTap(freq) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = freq.label,
                    color = colors.fg,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = freq.title,
            color = titleColor,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
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
private fun Caption(selected: Frequency?, isTuned: Boolean) {
    // Crossfade matches the audio crossfade in spirit: text doesn't snap
    // when the tone underneath fades. Keying on the frequency key (or
    // null) means re-selecting the same tone is a no-op.
    Crossfade(
        targetState = selected?.key,
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
