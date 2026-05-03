package com.soulradio.soulradio

import java.util.Calendar
import java.util.TimeZone
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/**
 * Geographic location for solar calculations. Latitude in [-90, 90],
 * longitude in [-180, 180] with east positive.
 */
data class LatLon(val lat: Double, val lon: Double) {
    init {
        require(lat in -90.0..90.0) { "lat out of range: $lat" }
        require(lon in -180.0..180.0) { "lon out of range: $lon" }
    }
}

/**
 * Sunrise, solar-noon (upper meridian transit), and sunset for a given date,
 * expressed as UTC epoch milliseconds. Callers convert to a local zone via
 * [Calendar] or [java.util.Date].
 */
data class SolarTimes(
    val sunriseUtcMillis: Long,
    val solarNoonUtcMillis: Long,
    val sunsetUtcMillis: Long,
)

/**
 * Offline sunrise / sunset / solar-noon calculator. Implements the NOAA-style
 * "approximate" sunrise equation (see Wikipedia: Sunrise equation). Accuracy
 * is within a few minutes at temperate latitudes — well past what an ambient
 * loop needs to shift its band edges.
 *
 * Pure math on java.util.Calendar (API 24 compatible). No network, no
 * permissions, no Android APIs.
 *
 * Returns null for polar day / polar night, where the sun does not cross
 * the standard sunrise altitude on the given date. Callers should treat
 * null as a signal to fall back to the clock-based schedule.
 */
object SolarCalculator {

    private const val J2000_UTC_MILLIS = 946_728_000_000L  // 2000-01-01 12:00:00 UTC
    private const val MILLIS_PER_DAY = 86_400_000.0

    // Standard sunrise/sunset altitude: -0.833° = atmospheric refraction (~34')
    // plus the apparent solar disc radius (~16').
    private const val SUN_ALTITUDE_DEG = -0.833

    // Earth's mean obliquity of the ecliptic.
    private const val OBLIQUITY_DEG = 23.4397

    /**
     * Compute solar events for the civil date [year]/[month]/[dayOfMonth] in [zone].
     * Returns UTC epoch millis; convert with a [Calendar] in the desired zone.
     *
     * @param month is 1..12 (not 0-indexed Calendar style).
     */
    fun compute(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        loc: LatLon,
        zone: TimeZone,
    ): SolarTimes? {
        val n = daysFromJ2000(year, month, dayOfMonth, zone) + 0.0008
        val jStar = n - loc.lon / 360.0

        val mDeg = (357.5291 + 0.98560028 * jStar).mod360()
        val m = Math.toRadians(mDeg)
        val cDeg = 1.9148 * sin(m) + 0.0200 * sin(2 * m) + 0.0003 * sin(3 * m)
        val lambdaDeg = (mDeg + cDeg + 180.0 + 102.9372).mod360()
        val lambda = Math.toRadians(lambdaDeg)

        val transitDays = jStar + 0.0053 * sin(m) - 0.0069 * sin(2 * lambda)

        val delta = asin(sin(lambda) * sin(Math.toRadians(OBLIQUITY_DEG)))
        val latRad = Math.toRadians(loc.lat)
        val cosH = (sin(Math.toRadians(SUN_ALTITUDE_DEG)) - sin(latRad) * sin(delta)) /
            (cos(latRad) * cos(delta))

        if (cosH !in -1.0..1.0) return null

        val hFrac = Math.toDegrees(acos(cosH)) / 360.0

        val transitMillis = J2000_UTC_MILLIS + (transitDays * MILLIS_PER_DAY).toLong()
        val halfDayMillis = (hFrac * MILLIS_PER_DAY).toLong()

        return SolarTimes(
            sunriseUtcMillis = transitMillis - halfDayMillis,
            solarNoonUtcMillis = transitMillis,
            sunsetUtcMillis = transitMillis + halfDayMillis,
        )
    }

    /**
     * Days since J2000.0 (2000-01-01 12:00 UTC) at the start of the given local
     * date. Fractional, accounting for the zone offset so the algorithm operates
     * on the correct civil day.
     */
    private fun daysFromJ2000(year: Int, month: Int, dayOfMonth: Int, zone: TimeZone): Double {
        val cal = Calendar.getInstance(zone)
        cal.clear()
        cal.set(year, month - 1, dayOfMonth, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return floor((cal.timeInMillis - J2000_UTC_MILLIS) / MILLIS_PER_DAY)
    }

    private fun Double.mod360(): Double = ((this % 360.0) + 360.0) % 360.0
}
