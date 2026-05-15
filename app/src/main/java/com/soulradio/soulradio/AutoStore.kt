package com.soulradio.soulradio

import android.content.Context
import android.content.Intent

// Single source of truth for the AUTO-enabled preference. Writes also
// fire the corresponding ACTION_AUTO_* intent at PlaybackService so the
// service's runtime state and the persisted pref never diverge.
object AutoStore {
    private const val PREFS = "soulradio.state"
    private const val KEY = "auto_enabled"

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY, false)

    // Manifesto §"the promise": "Just leave it on." First launch defaults to AUTO.
    fun startIfFirstLaunch(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY)) set(context, true)
    }

    fun set(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.contains(KEY) && prefs.getBoolean(KEY, false) == enabled) return
        prefs.edit().putBoolean(KEY, enabled).apply()
        val action = if (enabled) PlaybackService.ACTION_AUTO_ON else PlaybackService.ACTION_AUTO_OFF
        context.startService(
            Intent(context, PlaybackService::class.java).apply { this.action = action },
        )
    }
}
