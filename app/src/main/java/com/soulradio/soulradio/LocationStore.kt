package com.soulradio.soulradio

import android.content.Context

/**
 * Persists an optional manual location for the solar-aware schedule.
 * Returns null when the user has not entered one — [Frequencies.forNow]
 * then falls back to the clock-based schedule.
 *
 * Floats are sufficient: the algorithm is accurate to ~1 minute and the
 * loop's band edges already span tens of minutes, so sub-degree precision
 * is meaningless here.
 */
object LocationStore {
    private const val PREFS = "soulradio.state"
    private const val KEY_LAT = "loc_lat"
    private const val KEY_LON = "loc_lon"

    fun get(context: Context): LatLon? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_LAT) || !prefs.contains(KEY_LON)) return null
        val lat = prefs.getFloat(KEY_LAT, Float.NaN).toDouble()
        val lon = prefs.getFloat(KEY_LON, Float.NaN).toDouble()
        return runCatching { LatLon(lat, lon) }.getOrNull()
    }

    fun set(context: Context, loc: LatLon) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putFloat(KEY_LAT, loc.lat.toFloat())
            .putFloat(KEY_LON, loc.lon.toFloat())
            .apply()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_LAT)
            .remove(KEY_LON)
            .apply()
    }
}
