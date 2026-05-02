package com.soulradio.soulradio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Locks the 24-hour auto-loop schedule against FREQUENCIES.md § Quick map.
 * If the markdown changes, this test must change with it — and vice versa.
 */
class FrequenciesTest {

    // The schedule, transcribed directly from FREQUENCIES.md § Quick map.
    // Format: hour-of-day (0..23) -> expected frequency key.
    private val expectedSchedule = mapOf(
        0  to "174",   1  to "174",   2  to "174",   3  to "174",
        4  to "174",   5  to "174",
        6  to "396",   7  to "396",   8  to "396",
        9  to "741",   10 to "741",   11 to "741",
        12 to "528",   13 to "528",   14 to "528",   15 to "528",
        16 to "639",   17 to "639",
        18 to "417",   19 to "417",
        20 to "285",   21 to "285",
        22 to "174",
        23 to "174",
    )

    @Test
    fun forHour_matchesQuickMap_forAll24Hours() {
        for ((hour, expectedKey) in expectedSchedule) {
            assertEquals(
                "Hour $hour should map to $expectedKey Hz",
                expectedKey,
                Frequencies.forHour(hour).key,
            )
        }
    }

    @Test
    fun forHour_coversEveryHourOfTheDay() {
        for (hour in 0..23) {
            assertNotNull("Hour $hour returned null", Frequencies.forHour(hour))
        }
    }

    @Test
    fun forHour_bandBoundariesAreInclusiveAtStartExclusiveAtEnd() {
        // FREQUENCIES.md uses "06:00–09:00" notation: 06:00 is in the band, 09:00 starts the next.
        assertEquals("396", Frequencies.forHour(6).key)
        assertEquals("741", Frequencies.forHour(9).key)
        assertEquals("528", Frequencies.forHour(12).key)
        assertEquals("639", Frequencies.forHour(16).key)
        assertEquals("417", Frequencies.forHour(18).key)
        assertEquals("285", Frequencies.forHour(20).key)
        assertEquals("174", Frequencies.forHour(22).key)
        assertEquals("174", Frequencies.forHour(23).key)
        // Night band wraps: 22:00–06:00 means 05:59 is still night, 06:00 is morning.
        assertEquals("174", Frequencies.forHour(5).key)
        assertEquals("396", Frequencies.forHour(6).key)
    }

    @Test
    fun forHour_nightBandIsAnchoredBy174() {
        // 22:00–06:00 is held by 174 Hz until a 7.83 underlay is curated.
        for (hour in listOf(22, 23, 0, 1, 2, 3, 4, 5)) {
            assertEquals("Hour $hour is in the night band", "174", Frequencies.forHour(hour).key)
        }
    }

    @Test
    fun forHour_neverReturnsDialOnlyTones() {
        // 852 and 963 are dial-only per FREQUENCIES.md — the loop must never auto-play them.
        for (hour in 0..23) {
            val key = Frequencies.forHour(hour).key
            assert(key != "852" && key != "963") {
                "Hour $hour returned dial-only tone $key — auto loop must not play it"
            }
        }
    }
}
