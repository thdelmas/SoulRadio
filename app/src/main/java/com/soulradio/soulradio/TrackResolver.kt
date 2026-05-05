package com.soulradio.soulradio

import android.content.Context

// USER_ONLY is strict by design: an empty user list yields silence, the
// auto-loop's next-hour tick rolls on. MIXED is the soft fallback.
internal object TrackResolver {

    fun urisFor(context: Context, freq: Frequency): List<String> =
        urisFor(
            freq = freq,
            source = LibrarySourceStore.get(context),
            userUrisOnBand = UserTracksStore.byBand(context, freq.key)
                .map { it.sourceUri }
                .filter { UriHealth.isReadable(context, it) },
        )

    fun urisFor(
        freq: Frequency,
        source: LibrarySource,
        userUrisOnBand: List<String>,
    ): List<String> {
        val curated = freq.tracks.map { "asset:///${freq.assetPath(it)}" }
        return when (source) {
            LibrarySource.APP_ONLY -> curated
            LibrarySource.MIXED -> curated + userUrisOnBand
            LibrarySource.USER_ONLY -> userUrisOnBand
        }
    }

    // Skips UriHealth deliberately: per-file readability is too expensive
    // for the dial's per-composition path. Library surfaces ghost rows
    // directly; tunedKeys is a hint, not a guarantee.
    fun tunedKeys(context: Context): Set<String> = tunedKeys(
        bands = Frequencies.all,
        source = LibrarySourceStore.get(context),
        userTrackBands = UserTracksStore.all(context).flatMap { it.assignedBands }.toSet(),
    )

    fun tunedKeys(
        bands: List<Frequency>,
        source: LibrarySource,
        userTrackBands: Set<String>,
    ): Set<String> = bands.filter { f ->
        val curated = f.tracks.isNotEmpty()
        val user = f.key in userTrackBands
        when (source) {
            LibrarySource.APP_ONLY -> curated
            LibrarySource.MIXED -> curated || user
            LibrarySource.USER_ONLY -> user
        }
    }.map { it.key }.toSet()
}
