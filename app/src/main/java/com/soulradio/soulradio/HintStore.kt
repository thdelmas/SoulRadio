package com.soulradio.soulradio

import android.content.Context

object HintStore {
    private const val PREFS = "soulradio.state"
    private const val KEY_SHOWN = "first_launch_hint_shown"

    fun shouldShow(context: Context): Boolean =
        !context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_SHOWN, false)

    fun markShown(context: Context) {
        if (!shouldShow(context)) return
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_SHOWN, true)
            .apply()
    }
}
