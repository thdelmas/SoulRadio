package com.soulradio.soulradio

import android.content.Context

// Default APP_ONLY: user library is opt-in (manifesto §"exploration is
// opt-in, never the default surface").
enum class LibrarySource {
    APP_ONLY,
    MIXED,
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
        if (get(context) == source) return
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, source.name)
            .apply()
    }
}
