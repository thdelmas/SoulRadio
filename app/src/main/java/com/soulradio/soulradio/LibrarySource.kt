package com.soulradio.soulradio

import android.content.Context

/**
 * Which sources contribute to a band's playlist when the loop, dial, or
 * Radio plays it.
 *
 * Default is [APP_ONLY] so existing behaviour is unchanged until the
 * listener opts in — the manifesto's "exploration is opt-in, never the
 * default surface" applies to user-imported audio too. The user's library
 * does not bleed into the room without consent.
 */
enum class LibrarySource {
    /** Curated catalogue only. The radio as it ships. */
    APP_ONLY,

    /** Curated catalogue + user-imported, drawn together by the band's profile. */
    MIXED,

    /** User-imported only. The listener becomes the sole curator. */
    USER_ONLY;

    companion object {
        val DEFAULT: LibrarySource = APP_ONLY
    }
}

object LibrarySourceStore {
    private const val PREFS = "soulradio.state"
    private const val KEY = "library_source"

    fun get(context: Context): LibrarySource {
        val name = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, null) ?: return LibrarySource.DEFAULT
        return runCatching { LibrarySource.valueOf(name) }.getOrDefault(LibrarySource.DEFAULT)
    }

    fun set(context: Context, source: LibrarySource) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, source.name)
            .apply()
    }
}
