package com.soulradio.soulradio

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests for the solar-anchored band schedule. Uses synthetic [SolarTimes]
 * (sunrise = 06:00, solar noon = 12:00, sunset = 18:00, all in UTC) so the
 * tests are independent of the underlying solar calculator.
 */
class SolarScheduleTest {

    private val day = 86_400_000L
    private val hour = 3_600_000L
    private val min = 60_000L

    // Synthetic baseline: 2026-05-03 with sunrise/noon/sunset at 06/12/18 UTC.
    private val baseDayMidnight = 1_777_852_800_000L  // 2026-05-03 00:00:00 UTC
    private val sun = SolarTimes(
        sunriseUtcMillis = baseDayMidnight + 6 * hour,
        solarNoonUtcMillis = baseDayMidnight + 12 * hour,
        sunsetUtcMillis = baseDayMidnight + 18 * hour,
    )

    @Test
    fun beforeSunrise_isNightBand() {
        val nowAt = baseDayMidnight + 3 * hour  // 03:00, well before sunrise
        assertEquals("7.83", SolarSchedule.bandAt(nowAt, sun).key)
    }

    @Test
    fun atSunrise_isMorningGate() {
        assertEquals("396", SolarSchedule.bandAt(sun.sunriseUtcMillis, sun).key)
    }

    @Test
    fun midMorning_isClearing() {
        // sunrise + 4h → past the 396 → 741 boundary at sunrise+3h
        val nowAt = sun.sunriseUtcMillis + 4 * hour
        assertEquals("741", SolarSchedule.bandAt(nowAt, sun).key)
    }

    @Test
    fun straddleNoon_isCentre() {
        // solar noon → 528 (the band starts at solar_noon - 90min)
        assertEquals("528", SolarSchedule.bandAt(sun.solarNoonUtcMillis, sun).key)
        // 90 min before noon: just-entered 528
        assertEquals("528", SolarSchedule.bandAt(sun.solarNoonUtcMillis - 90 * min, sun).key)
        // 91 min before noon: still 741
        assertEquals("741", SolarSchedule.bandAt(sun.solarNoonUtcMillis - 91 * min, sun).key)
    }

    @Test
    fun afternoon_isTable() {
        val nowAt = sun.solarNoonUtcMillis + 4 * hour  // past noon+180min boundary
        assertEquals("639", SolarSchedule.bandAt(nowAt, sun).key)
    }

    @Test
    fun goldenHour_isDissolver() {
        val nowAt = sun.sunsetUtcMillis - 30 * min  // sunset - 30 → past sunset-45 boundary
        assertEquals("417", SolarSchedule.bandAt(nowAt, sun).key)
    }

    @Test
    fun afterSunset_progressesThroughEveningBands() {
        assertEquals("285", SolarSchedule.bandAt(sun.sunsetUtcMillis + 90 * min, sun).key)
        assertEquals("174", SolarSchedule.bandAt(sun.sunsetUtcMillis + 200 * min, sun).key)
        assertEquals("7.83", SolarSchedule.bandAt(sun.sunsetUtcMillis + 250 * min, sun).key)
    }

    @Test
    fun lateEvening_isNightBand() {
        // Well after sunset+240 boundary
        val nowAt = sun.sunsetUtcMillis + 6 * hour
        assertEquals("7.83", SolarSchedule.bandAt(nowAt, sun).key)
    }

    @Test
    fun nextBoundaryMillis_advancesThroughTheDay() {
        val firstBoundary = SolarSchedule.nextBoundaryMillis(sun.sunriseUtcMillis - hour, sun)
        assertEquals("first boundary after pre-dawn is sunrise", sun.sunriseUtcMillis, firstBoundary)

        val secondBoundary = SolarSchedule.nextBoundaryMillis(sun.sunriseUtcMillis, sun)
        assertEquals(
            "next boundary after sunrise is sunrise+180m (741)",
            sun.sunriseUtcMillis + 180 * min,
            secondBoundary,
        )

        val centreBoundary = SolarSchedule.nextBoundaryMillis(sun.sunriseUtcMillis + 4 * hour, sun)
        assertEquals(
            "next boundary in late morning is solar_noon - 90m (528)",
            sun.solarNoonUtcMillis - 90 * min,
            centreBoundary,
        )
    }

    @Test
    fun nextBoundaryMillis_pastLastBand_usesTomorrowSunrise() {
        val tomorrowSunrise = sun.sunriseUtcMillis + day
        val nowAt = sun.sunsetUtcMillis + 5 * hour  // past 7.83 entry
        val boundary = SolarSchedule.nextBoundaryMillis(nowAt, sun, tomorrowSunrise)
        assertEquals(tomorrowSunrise, boundary)
    }

    @Test
    fun nextBoundaryMillis_pastLastBand_fallsBackToOneHourPoll() {
        val nowAt = sun.sunsetUtcMillis + 5 * hour
        val boundary = SolarSchedule.nextBoundaryMillis(nowAt, sun, tomorrowSunriseUtcMillis = null)
        assertEquals("falls back to now + 1h when tomorrow unknown", nowAt + hour, boundary)
    }

    @Test
    fun bandAt_neverReturnsDialOnlyTones() {
        // Walk a synthetic day in 15-minute steps. The auto schedule must never
        // serve 852 or 963 — those are dial-only per FREQUENCIES.md.
        val start = sun.sunriseUtcMillis - 12 * hour  // start a half-day before
        for (offset in 0..(48 * hour / (15 * min)).toInt()) {
            val now = start + offset * 15 * min
            val key = SolarSchedule.bandAt(now, sun).key
            assert(key != "852" && key != "963") {
                "bandAt at offset ${offset * 15}min returned dial-only $key"
            }
        }
    }
}
