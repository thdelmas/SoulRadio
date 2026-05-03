package com.soulradio.soulradio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.abs

/**
 * Sanity tests for [SolarCalculator]. The underlying algorithm is approximate
 * (within a few minutes for temperate latitudes), so tolerances are generous —
 * the goal is to catch regressions, not certify a navigational ephemeris.
 */
class SolarCalculatorTest {

    @Test
    fun equator_atMarchEquinox_dayIsRoughlyTwelveHours() {
        val times = SolarCalculator.compute(
            year = 2026, month = 3, dayOfMonth = 20,
            loc = LatLon(0.0, 0.0),
            zone = TimeZone.getTimeZone("UTC"),
        )
        assertNotNull("equator should have sunrise/sunset", times)
        times!!
        val dayLengthMin = (times.sunsetUtcMillis - times.sunriseUtcMillis) / 60_000L
        // At the equator on the equinox, day length is ~12h ± a few minutes
        // (the standard sunrise altitude includes refraction, which slightly
        // lengthens the apparent day).
        assertTrue("day length $dayLengthMin should be near 720 min", dayLengthMin in 700L..740L)
    }

    @Test
    fun paris_summerSolstice_longDay() {
        val zone = TimeZone.getTimeZone("Europe/Paris")
        val times = SolarCalculator.compute(2026, 6, 21, LatLon(48.8566, 2.3522), zone)!!
        // Reference: Paris summer solstice ~05:47 sunrise, ~21:57 sunset CEST.
        assertNearLocalTime("paris summer sunrise", times.sunriseUtcMillis, zone, 5, 47, tolMin = 15)
        assertNearLocalTime("paris summer sunset", times.sunsetUtcMillis, zone, 21, 57, tolMin = 15)
        val dayLengthMin = (times.sunsetUtcMillis - times.sunriseUtcMillis) / 60_000L
        assertTrue("day length $dayLengthMin near 16h10m", dayLengthMin in 940L..990L)
    }

    @Test
    fun paris_winterSolstice_shortDay() {
        val zone = TimeZone.getTimeZone("Europe/Paris")
        val times = SolarCalculator.compute(2026, 12, 21, LatLon(48.8566, 2.3522), zone)!!
        // Reference: Paris winter solstice ~08:42 sunrise, ~16:55 sunset CET.
        assertNearLocalTime("paris winter sunrise", times.sunriseUtcMillis, zone, 8, 42, tolMin = 15)
        assertNearLocalTime("paris winter sunset", times.sunsetUtcMillis, zone, 16, 55, tolMin = 15)
        val dayLengthMin = (times.sunsetUtcMillis - times.sunriseUtcMillis) / 60_000L
        assertTrue("day length $dayLengthMin near 8h12m", dayLengthMin in 470L..520L)
    }

    @Test
    fun tromso_polarNight_returnsNull() {
        // Tromsø is above the Arctic Circle. On winter solstice the sun does
        // not rise — the algorithm should return null and let callers fall
        // back to the clock schedule.
        val times = SolarCalculator.compute(
            2026, 12, 21, LatLon(69.6492, 18.9553), TimeZone.getTimeZone("Europe/Oslo"),
        )
        assertNull("polar night should return null", times)
    }

    @Test
    fun tromso_polarDay_returnsNull() {
        val times = SolarCalculator.compute(
            2026, 6, 21, LatLon(69.6492, 18.9553), TimeZone.getTimeZone("Europe/Oslo"),
        )
        assertNull("polar day should return null", times)
    }

    @Test
    fun southernHemisphere_seasonsAreInverted() {
        val sydney = LatLon(-33.8688, 151.2093)
        val zone = TimeZone.getTimeZone("Australia/Sydney")
        val december = SolarCalculator.compute(2026, 12, 21, sydney, zone)!!
        val june = SolarCalculator.compute(2026, 6, 21, sydney, zone)!!
        val decemberLen = (december.sunsetUtcMillis - december.sunriseUtcMillis) / 60_000L
        val juneLen = (june.sunsetUtcMillis - june.sunriseUtcMillis) / 60_000L
        assertTrue(
            "Sydney December day ($decemberLen min) should exceed June day ($juneLen min)",
            decemberLen > juneLen,
        )
    }

    @Test
    fun solarNoon_fallsBetweenSunriseAndSunset() {
        val times = SolarCalculator.compute(
            2026, 5, 3, LatLon(48.8566, 2.3522), TimeZone.getTimeZone("Europe/Paris"),
        )!!
        assertTrue("solar noon after sunrise", times.solarNoonUtcMillis > times.sunriseUtcMillis)
        assertTrue("solar noon before sunset", times.solarNoonUtcMillis < times.sunsetUtcMillis)
    }

    @Test
    fun losAngeles_solarNoonNearLocalNoon() {
        // LA is at longitude -118.24°, so solar noon arrives ~7m52s late vs the
        // 120°W zone meridian → solar noon ~12:08 PST (or ~13:08 PDT) ± equation
        // of time. Just check we land in the noon hour, not 6am or midnight.
        val zone = TimeZone.getTimeZone("America/Los_Angeles")
        val times = SolarCalculator.compute(2026, 5, 3, LatLon(34.0522, -118.2437), zone)!!
        val cal = Calendar.getInstance(zone).apply { timeInMillis = times.solarNoonUtcMillis }
        assertTrue(
            "LA solar noon hour ${cal.get(Calendar.HOUR_OF_DAY)} should be 12 or 13 (DST)",
            cal.get(Calendar.HOUR_OF_DAY) in 12..13,
        )
    }

    @Test
    fun latLon_rejectsOutOfRangeValues() {
        assertThrows(IllegalArgumentException::class.java) { LatLon(91.0, 0.0) }
        assertThrows(IllegalArgumentException::class.java) { LatLon(-91.0, 0.0) }
        assertThrows(IllegalArgumentException::class.java) { LatLon(0.0, 181.0) }
        assertThrows(IllegalArgumentException::class.java) { LatLon(0.0, -181.0) }
    }

    private fun assertNearLocalTime(
        label: String,
        actualUtcMillis: Long,
        zone: TimeZone,
        expectedHour: Int,
        expectedMinute: Int,
        tolMin: Long,
    ) {
        val cal = Calendar.getInstance(zone).apply { timeInMillis = actualUtcMillis }
        val actualMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        val expectedMinutes = expectedHour * 60 + expectedMinute
        val diff = abs(actualMinutes - expectedMinutes)
        assertTrue(
            "$label: expected ~%02d:%02d, got %02d:%02d (off by %d min, tolerance %d)".format(
                expectedHour, expectedMinute,
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
                diff, tolMin,
            ),
            diff <= tolMin,
        )
    }
}
