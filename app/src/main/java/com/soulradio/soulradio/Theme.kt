package com.soulradio.soulradio

import androidx.compose.ui.graphics.Color

// Twelve named colours total. The eye does not have to choose.
// Shared across the screen's component files; kept `internal` so the
// palette stays inside the module without leaking to library consumers.
internal val Bg = Color.Black
internal val Gold = Color(0xFFD4AF37)
// GoldDim and Mute lifted from #6E5A1C / #555555 — the originals sat just
// below WCAG AA contrast on AMOLED in daylight, the kitchen-counter
// ambient context the radio is for. The hierarchy is unchanged; the text
// becomes findable in sun.
internal val GoldDim = Color(0xFF8A7228)
internal val Slate = Color(0xFF1A1A1A)
internal val SlateDeep = Color(0xFF0E0E0E)
internal val Mute = Color(0xFF707070)
internal val MuteSoft = Color(0xFF888888)
internal val Hairline = Color(0xFF222222)
