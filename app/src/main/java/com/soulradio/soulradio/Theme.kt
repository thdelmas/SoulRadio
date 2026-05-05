package com.soulradio.soulradio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal val Bg = Color.Black
internal val Gold = Color(0xFFD4AF37)
// GoldDim and Mute lifted from #6E5A1C / #555555 — the originals sat just
// below WCAG AA contrast on AMOLED in daylight.
internal val GoldDim = Color(0xFF8A7228)
internal val Slate = Color(0xFF1A1A1A)
internal val SlateDeep = Color(0xFF0E0E0E)
internal val Mute = Color(0xFF707070)
internal val MuteSoft = Color(0xFF888888)
internal val Hairline = Color(0xFF222222)

@Composable
internal fun HairlineDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Hairline),
    )
}
