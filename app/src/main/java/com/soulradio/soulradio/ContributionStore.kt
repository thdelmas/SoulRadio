package com.soulradio.soulradio

import android.content.Context

/**
 * Single-fact store: the timestamp (epoch ms) of the last contribution
 * popup show. The cadence-only state model is mandated by the portfolio
 * popup guide — never persist which button the user picked, never persist
 * whether the user donated, never let the app behave differently for
 * users who have. The donation flow is a one-way hand-off to the system
 * browser; the app stays ignorant of the outcome on purpose.
 */
object ContributionStore {
    private const val PREFS = "soulradio.state"
    private const val KEY_LAST_SHOWN = "contrib_last_shown_at"

    /**
     * 90 days. Quieter than the portfolio default (~30 d) — SoulRadio's
     * manifesto says "the point is the room, not the app," so the ask is
     * spaced wider and only fires when the radio is paused (see
     * [shouldOffer]). Trade-off documented in CLAUDE.md.
     */
    const val INTERVAL_MS: Long = 90L * 24 * 60 * 60 * 1000

    fun lastShownAt(context: Context): Long? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return if (prefs.contains(KEY_LAST_SHOWN))
            prefs.getLong(KEY_LAST_SHOWN, 0L) else null
    }

    fun markShown(context: Context, now: Long = System.currentTimeMillis()) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_LAST_SHOWN, now)
            .apply()
    }

    /**
     * True when the popup should be offered: the cadence has elapsed AND
     * the radio is currently paused. Never interrupts audio (manifesto).
     *
     * Side-effect: on the very first call (no stored timestamp), baselines
     * the timestamp to [now] so a fresh install gets a full 90-day grace
     * before the first ask, instead of seeing the popup the moment the
     * user opens the app for the first time.
     */
    fun shouldOffer(
        context: Context,
        isPaused: Boolean,
        now: Long = System.currentTimeMillis(),
    ): Boolean {
        if (!isPaused) return false
        val last = lastShownAt(context)
        if (last == null) {
            markShown(context, now)
            return false
        }
        return now - last >= INTERVAL_MS
    }
}
