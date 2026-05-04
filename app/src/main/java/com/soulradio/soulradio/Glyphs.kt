package com.soulradio.soulradio

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 48 dp tap-target wrapping a 20 dp glyph. Borderless on purpose — the
 * gold outline of the glyph carries weight enough; a circle around each
 * one would crowd the corner where these are paired.
 */
@Composable
internal fun IconDoor(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/**
 * Eight-tooth cog drawn as short radial spokes plus a stroked ring and a
 * smaller centre hole. Hairline stroke matches the AUTO pill border so
 * the glyph reads in the same key as the rest of the row.
 */
@Composable
internal fun GearGlyph() {
    Canvas(modifier = Modifier.size(20.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val outer = size.minDimension * 0.34f
        val inner = size.minDimension * 0.13f
        val tooth = size.minDimension * 0.10f
        val stroke = 1.dp.toPx()
        for (i in 0 until 8) {
            val a = (i * (PI / 4)).toFloat()
            drawLine(
                color = Gold,
                start = Offset(center.x + outer * cos(a), center.y + outer * sin(a)),
                end = Offset(center.x + (outer + tooth) * cos(a), center.y + (outer + tooth) * sin(a)),
                strokeWidth = stroke,
            )
        }
        drawCircle(color = Gold, radius = outer, center = center, style = Stroke(width = stroke))
        drawCircle(color = Gold, radius = inner, center = center, style = Stroke(width = stroke))
    }
}

/**
 * Open book — two page rectangles meeting at a centre spine. Stroked,
 * not filled, to match the gear's weight.
 */
@Composable
internal fun BookGlyph() {
    Canvas(modifier = Modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val left = w * 0.10f
        val right = w * 0.90f
        val top = h * 0.24f
        val bottom = h * 0.76f
        val mid = w / 2f
        val stroke = 1.dp.toPx()
        // Left page
        drawLine(Gold, Offset(left, top), Offset(mid, top), stroke)
        drawLine(Gold, Offset(left, top), Offset(left, bottom), stroke)
        drawLine(Gold, Offset(left, bottom), Offset(mid, bottom), stroke)
        // Right page
        drawLine(Gold, Offset(right, top), Offset(mid, top), stroke)
        drawLine(Gold, Offset(right, top), Offset(right, bottom), stroke)
        drawLine(Gold, Offset(right, bottom), Offset(mid, bottom), stroke)
        // Spine
        drawLine(Gold, Offset(mid, top), Offset(mid, bottom), stroke)
    }
}
