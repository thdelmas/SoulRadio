package com.soulradio.soulradio

import android.content.Context

// One-shot first-launch hint flag. The hint points new listeners at the
// book glyph so the dial numbers don't read as gibberish; after the first
// session the radio is supposed to recede, so it never returns.
object HintStore {
    private const val PREFS = "soulradio.state"
    private const val KEY_SHOWN = "first_launch_hint_shown"

    fun shouldShow(context: Context): Boolean =
        !context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_SHOWN, false)

    fun markShown(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_SHOWN, true)
            .apply()
    }
}
