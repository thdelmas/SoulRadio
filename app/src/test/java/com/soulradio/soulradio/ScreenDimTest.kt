package com.soulradio.soulradio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pins the screen-dim curve. The three anchors named in issue #24 — night
 * 0.6, dawn 0.85, midday 1.0 — are exact; the in-between bands must form
 * a monotonic descent from midday into deep night.
 */
class ScreenDimTest {

    @Test
    fun anchors_matchIssue24() {
        assertEquals(1.00f, screenDimFor("528"), 0.001f)   // midday
        assertEquals(0.85f, screenDimFor("396"), 0.001f)   // dawn
        assertEquals(0.85f, screenDimFor("417"), 0.001f)   // golden-hour dusk
        assertEquals(0.60f, screenDimFor("7.83"), 0.001f)  // deep night
    }

    @Test
    fun curve_descendsMonotonicallyFromMiddayIntoNight() {
        // The diurnal arc, in solar-day order from midday outward toward night.
        val descent = listOf("528", "639", "417", "285", "174", "7.83")
        val values = descent.map { screenDimFor(it) }
        for (i in 1 until values.size) {
            assertTrue(
                "Dim must not rise from ${descent[i - 1]} (${values[i - 1]}) " +
                    "to ${descent[i]} (${values[i]})",
                values[i] <= values[i - 1],
            )
        }
    }

    @Test
    fun unknownKey_fallsBackToFullBrightness() {
        assertEquals(1.00f, screenDimFor("nonexistent"), 0.001f)
    }
}
