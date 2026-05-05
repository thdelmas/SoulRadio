package com.soulradio.soulradio

import android.content.Context

/**
 * Resolves the URI playlist a band plays, given the active [LibrarySource]
 * filter and any user-imported tracks the listener has filed under the
 * band. The four playback surfaces (loop, dial, Radio, Library) all consult
 * this — the band is the data axis; the surface is just the UI selecting
 * a band to play.
 *
 * - APP_ONLY: curated catalogue only. The radio as it ships.
 * - MIXED: curated tracks first, then user tracks filed under the band.
 *   Order is intentional — curator-chosen recordings open the band, the
 *   listener's imports follow.
 * - USER_ONLY: user tracks only. May return an empty list when the
 *   listener has filed nothing on this band; the player interprets that
 *   as silence for the band, and the auto-loop's next-hour tick rolls on.
 *   Strict by design — the listener opted in to USER_ONLY; falling back
 *   to APP would defeat the choice. Listeners who want a soft fallback
 *   can use MIXED.
 */
internal object TrackResolver {

    /**
     * Production entry point. Wires up [LibrarySourceStore],
     * [UserTracksStore], and [UriHealth] — missing user files (deleted,
     * moved, or with revoked permission) are skipped so the player never
     * queues a dead URI. The Library screen exposes the same missing
     * state to the listener so they can remove the ghost row.
     */
    fun urisFor(context: Context, freq: Frequency): List<String> =
        urisFor(
            freq = freq,
            source = LibrarySourceStore.get(context),
            userUrisOnBand = UserTracksStore.byBand(context, freq.key)
                .map { it.sourceUri }
                .filter { UriHealth.isReadable(context, it) },
        )

    /** Pure function for tests. Takes the inputs the production overload reads. */
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

    /**
     * The set of band keys that would yield a non-empty playlist under
     * the active [LibrarySource]. The dial uses this to gate its "tuned"
     * indicator so a tap doesn't promise audio that won't play — e.g.
     * USER_ONLY with no user files filed under a band reads as untuned,
     * and a user-filed band reads as tuned even when the curated catalogue
     * is empty for it.
     *
     * Does not consult [UriHealth] — a band with only ghost (missing)
     * user files reads as tuned here even though the playlist would be
     * empty after readability filtering. The Library screen surfaces the
     * "missing" state directly per-track; the dial is a hint, not a
     * guarantee, and the per-file readability check is too expensive to
     * run on every dial composition.
     */
    fun tunedKeys(context: Context): Set<String> = tunedKeys(
        bands = Frequencies.all,
        source = LibrarySourceStore.get(context),
        userTrackBands = UserTracksStore.all(context).flatMap { it.assignedBands }.toSet(),
    )

    /** Pure overload for tests. */
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
