package com.soulradio.soulradio

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun Dial(
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

@Composable
internal fun CompanionsRow(
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
