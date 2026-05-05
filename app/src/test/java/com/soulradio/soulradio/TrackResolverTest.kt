package com.soulradio.soulradio

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests the band-to-playlist resolution that all four modes (loop, dial,
 * Radio, Library) consult. The pure-function overload of [TrackResolver]
 * makes this exercisable without an Android context.
 */
class TrackResolverTest {

    private val band528 = Frequency(
        key = "528",
        label = "528",
        title = "the centre",
        tracks = listOf(
            NowPlaying("a.ogg", "Work A", "Performer A"),
            NowPlaying("b.ogg", "Work B", "Performer B"),
        ),
    )

    private val emptyBand = Frequency(
        key = "999",
        label = "999",
        title = "untuned",
        tracks = emptyList(),
    )

    private val userOnBand = listOf(
        "content://com.android.providers.documents/u1",
        "content://com.android.providers.documents/u2",
    )

    @Test
    fun appOnly_returns_curated_only_evenWhenUserHasFiles() {
        val out = TrackResolver.urisFor(band528, LibrarySource.APP_ONLY, userOnBand)
        assertEquals(
            listOf("asset:///audio/528/a.ogg", "asset:///audio/528/b.ogg"),
            out,
        )
    }

    @Test
    fun mixed_returns_curated_then_user() {
        val out = TrackResolver.urisFor(band528, LibrarySource.MIXED, userOnBand)
        // Curator-chosen recordings open the band, the listener's imports follow.
        assertEquals(
            listOf(
                "asset:///audio/528/a.ogg",
                "asset:///audio/528/b.ogg",
                "content://com.android.providers.documents/u1",
                "content://com.android.providers.documents/u2",
            ),
            out,
        )
    }

    @Test
    fun userOnly_returns_userFiles_only() {
        val out = TrackResolver.urisFor(band528, LibrarySource.USER_ONLY, userOnBand)
        assertEquals(userOnBand, out)
    }

    @Test
    fun userOnly_withNoUserFiles_returns_emptyList() {
        // Strict by design — a listener who picked USER_ONLY gets silence
        // on bands they haven't filed anything under, not a stealth APP
        // fallback. The auto-loop's next-hour tick rolls on regardless.
        val out = TrackResolver.urisFor(band528, LibrarySource.USER_ONLY, emptyList())
        assertEquals(emptyList<String>(), out)
    }

    @Test
    fun appOnly_withCurator_emptyBand_returns_empty() {
        val out = TrackResolver.urisFor(emptyBand, LibrarySource.APP_ONLY, emptyList())
        assertEquals(emptyList<String>(), out)
    }

    @Test
    fun mixed_withEmptyUser_returns_curated_only() {
        val out = TrackResolver.urisFor(band528, LibrarySource.MIXED, emptyList())
        assertEquals(
            listOf("asset:///audio/528/a.ogg", "asset:///audio/528/b.ogg"),
            out,
        )
    }

    @Test
    fun mixed_withEmptyCurated_returns_user_only() {
        // A curator-empty band could exist in principle; user files should
        // play on it under MIXED.
        val out = TrackResolver.urisFor(emptyBand, LibrarySource.MIXED, userOnBand)
        assertEquals(userOnBand, out)
    }

    @Test
    fun missingUserFiles_areNotInPlaylist_whenCallerHasFilteredThem() {
        // Documents the contract between layers: the production overload
        // of [TrackResolver.urisFor] filters via [UriHealth.isReadable]
        // before delegating to this pure overload. So a missing user
        // file's Uri never reaches the pure function — whatever the
        // caller hands in is what the player gets. The Library screen
        // applies the same check to surface a "missing" chip; the
        // listener can then remove the ghost row.
        val healthyOnly = listOf("content://x/healthy") // simulating post-filter
        val out = TrackResolver.urisFor(band528, LibrarySource.MIXED, healthyOnly)
        assertEquals(
            listOf(
                "asset:///audio/528/a.ogg",
                "asset:///audio/528/b.ogg",
                "content://x/healthy",
            ),
            out,
        )
    }

    @Test
    fun curated_uris_useAssetScheme_andEncodeBandKey() {
        val out = TrackResolver.urisFor(band528, LibrarySource.APP_ONLY, emptyList())
        for (uri in out) {
            assert(uri.startsWith("asset:///audio/528/")) {
                "Curated URI must encode the band's asset folder, got: $uri"
            }
        }
    }

    // -- tunedKeys: dial-tuned indicator under each LibrarySource --

    @Test
    fun tunedKeys_appOnly_returns_bandsWith_curatedTracks() {
        val out = TrackResolver.tunedKeys(
            bands = listOf(band528, emptyBand),
            source = LibrarySource.APP_ONLY,
            userTrackBands = setOf("528", "999"), // user files irrelevant in APP_ONLY
        )
        assertEquals(setOf("528"), out)
    }

    @Test
    fun tunedKeys_userOnly_returns_bandsWith_userFiles_only() {
        // The listener has filed under 999 (curator-empty) but not 528;
        // under USER_ONLY only 999 should read as tuned.
        val out = TrackResolver.tunedKeys(
            bands = listOf(band528, emptyBand),
            source = LibrarySource.USER_ONLY,
            userTrackBands = setOf("999"),
        )
        assertEquals(setOf("999"), out)
    }

    @Test
    fun tunedKeys_userOnly_withNoUserFiles_returns_empty() {
        // Strict by design — same as urisFor: USER_ONLY with no library
        // means no bands are tuned. The dial dims everything, the
        // listener can see the source-filter is the cause and switch.
        val out = TrackResolver.tunedKeys(
            bands = listOf(band528, emptyBand),
            source = LibrarySource.USER_ONLY,
            userTrackBands = emptySet(),
        )
        assertEquals(emptySet<String>(), out)
    }

    @Test
    fun tunedKeys_mixed_returns_union_of_curated_and_user() {
        val out = TrackResolver.tunedKeys(
            bands = listOf(band528, emptyBand),
            source = LibrarySource.MIXED,
            userTrackBands = setOf("999"),
        )
        // 528 is curated-tuned; 999 is user-tuned; both light up under MIXED.
        assertEquals(setOf("528", "999"), out)
    }

    @Test
    fun tunedKeys_mixed_withNoUserFiles_falls_back_to_curated_only() {
        val out = TrackResolver.tunedKeys(
            bands = listOf(band528, emptyBand),
            source = LibrarySource.MIXED,
            userTrackBands = emptySet(),
        )
        assertEquals(setOf("528"), out)
    }

    @Test
    fun tunedKeys_appOnly_userFilesUnderEmptyBand_doNotLightItUp() {
        // The listener has filed under a curator-empty band, but APP_ONLY
        // means the dial reflects the curator's catalogue only — the
        // empty band stays dim until the listener flips to MIXED or USER_ONLY.
        val out = TrackResolver.tunedKeys(
            bands = listOf(band528, emptyBand),
            source = LibrarySource.APP_ONLY,
            userTrackBands = setOf("999"),
        )
        assertEquals(setOf("528"), out)
    }
}
