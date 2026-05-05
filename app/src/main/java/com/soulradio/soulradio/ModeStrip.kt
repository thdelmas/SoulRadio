package com.soulradio.soulradio

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal enum class AppSurface { Main, Radio, Body, Chakra, Library, Notes, Settings }

// Surfaces that hide the ModeStrip when active.
internal val UTILITY_SURFACES = setOf(AppSurface.Notes, AppSurface.Settings)

// Chakra is intentionally absent from the strip: it lives one click in
// from BodyScreen because a sixth pill would overflow a 360 dp phone.
@Composable
internal fun ModeStrip(
    currentSurface: AppSurface,
    autoOn: Boolean,
    currentHour: Int,
    onDjMode: () -> Unit,
    onDialMode: () -> Unit,
    onRadio: () -> Unit,
    onBody: () -> Unit,
    onLibrary: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNotes: () -> Unit,
) {
    val activePill: String? = when {
        currentSurface == AppSurface.Main && autoOn -> "dj"
        currentSurface == AppSurface.Main -> "dial"
        currentSurface == AppSurface.Radio -> "radio"
        currentSurface == AppSurface.Body -> "body"
        currentSurface == AppSurface.Library -> "lib"
        else -> null
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ModePill(
            label = if (autoOn) "dj · ${currentHour.toString().padStart(2, '0')}h" else "dj",
            active = activePill == "dj",
            // Reserve space for the wider on-state ("dj · HHh") so toggling
            // auto doesn't push the other pills sideways.
            minWidth = 64.dp,
            onClick = onDjMode,
        )
        ModePill(label = "dial", active = activePill == "dial", onClick = onDialMode)
        ModePill(label = "radio", active = activePill == "radio", onClick = onRadio)
        ModePill(label = "body", active = activePill == "body", onClick = onBody)
        // Abbreviated to "lib" so the strip still fits five mode pills + two
        // utility icons on a 360 dp phone width.
        ModePill(label = "lib", active = activePill == "lib", onClick = onLibrary)
        Spacer(Modifier.weight(1f))
        IconDoor(onClick = onOpenSettings) { GearGlyph() }
        IconDoor(onClick = onOpenNotes) { BookGlyph() }
    }
}

@Composable
private fun ModePill(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    minWidth: Dp = Dp.Unspecified,
) {
    val color by animateColorAsState(
        targetValue = if (active) Gold else GoldDim,
        animationSpec = tween(500),
        label = "pill-color",
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .widthIn(min = minWidth)
            .padding(horizontal = 8.dp, vertical = 12.dp),
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            fontWeight = if (active) FontWeight.Medium else FontWeight.Light,
        )
        Spacer(Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(if (active) Gold else Color.Transparent),
        )
    }
}
