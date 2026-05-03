package com.soulradio.soulradio

/**
 * The 24-hour auto loop expressed as solar phases, not clock hours. Each
 * band starts at an offset from a solar anchor (sunrise, solar noon, sunset)
 * and runs until the next band starts. The night band wraps from the last
 * boundary of one solar day until the first sunrise of the next.
 *
 * Bands are listed in solar-day order. Keep aligned with FREQUENCIES.md
 * § Quick map — the clock-hour version in [Frequencies.forHour] is the
 * pre-solar approximation, this is the real schedule.
 */
object SolarSchedule {

    enum class SolarAnchor { SUNRISE, SOLAR_NOON, SUNSET }

    private data class Band(
        val anchor: SolarAnchor,
        val offsetMinutes: Int,
        val key: String,
    )

    // The same intentions as the clock schedule, restated against the sun.
    // Offsets in minutes; negative is "before the anchor".
    private val bands: List<Band> = listOf(
        Band(SolarAnchor.SUNRISE,    0,    "396"),   // morning gate — first light
        Band(SolarAnchor.SUNRISE,    180,  "741"),   // clearing — mid-morning
        Band(SolarAnchor.SOLAR_NOON, -90,  "528"),   // centre — straddles noon
        Band(SolarAnchor.SOLAR_NOON, 180,  "639"),   // table — afternoon into evening
        Band(SolarAnchor.SUNSET,     -45,  "417"),   // dissolver — golden hour through dusk
        Band(SolarAnchor.SUNSET,     60,   "285"),   // slow turn — full dark settling
        Band(SolarAnchor.SUNSET,     180,  "174"),   // foundation — late evening
        Band(SolarAnchor.SUNSET,     240,  "7.83"),  // night — until next sunrise
    )

    /** Frequency playing at [nowMillis] given today's [sun] times. */
    fun bandAt(nowMillis: Long, sun: SolarTimes): Frequency {
        val nightKey = bands.last().key
        // Before today's first band start: still in last night's wrap.
        if (nowMillis < bands.first().startMillis(sun)) {
            return Frequencies.byKey(nightKey)!!
        }
        // Walk in order. The current band is the last one whose start is <= now.
        var currentKey = bands.first().key
        for (b in bands) {
            if (b.startMillis(sun) <= nowMillis) currentKey = b.key else break
        }
        return Frequencies.byKey(currentKey)!!
    }

    /**
     * UTC millis at which the current band ends. If [nowMillis] is past
     * today's last band start (i.e. in the evening night band), the next
     * boundary is tomorrow's sunrise — supplied via [tomorrowSunriseUtcMillis].
     * Falls back to a one-hour poll when tomorrow is unavailable, so the
     * tick scheduler keeps re-checking instead of stalling forever.
     */
    fun nextBoundaryMillis(
        nowMillis: Long,
        sun: SolarTimes,
        tomorrowSunriseUtcMillis: Long? = null,
    ): Long {
        val next = bands.map { it.startMillis(sun) }.firstOrNull { it > nowMillis }
        if (next != null) return next
        return tomorrowSunriseUtcMillis ?: (nowMillis + 60L * 60_000L)
    }

    private fun Band.startMillis(sun: SolarTimes): Long {
        val anchorMillis = when (anchor) {
            SolarAnchor.SUNRISE -> sun.sunriseUtcMillis
            SolarAnchor.SOLAR_NOON -> sun.solarNoonUtcMillis
            SolarAnchor.SUNSET -> sun.sunsetUtcMillis
        }
        return anchorMillis + offsetMinutes * 60_000L
    }
}
