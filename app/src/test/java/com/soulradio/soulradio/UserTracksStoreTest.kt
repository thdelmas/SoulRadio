package com.soulradio.soulradio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Round-trips [UserTrack] lists through the JSON encoder/decoder. The
 * persistence layer is the only thing that can corrupt user data silently
 * — a missed field would mean the listener loses what they imported. Each
 * test exercises a shape the rest of the system actually produces:
 * multi-band assignments, empty matches (the ancestral case), and
 * null-able signals.
 */
class UserTracksStoreTest {

    @Test
    fun roundTrip_emptyList() {
        val raw = UserTracksStore.encode(emptyList())
        val decoded = UserTracksStore.decode(raw)
        assertEquals(emptyList<UserTrack>(), decoded)
    }

    @Test
    fun roundTrip_trackWithMultipleMatches_andFullSignals() {
        val track = UserTrack(
            id = "abc-123",
            sourceUri = "content://com.android.providers.documents/foo",
            displayName = "Some recording.flac",
            addedAtMs = 1_700_000_000_000L,
            profile = BandAssignment(
                matches = listOf(
                    BandMatch("528", 0.92f, "dominant pitch 527.4 Hz"),
                    BandMatch("174", 0.31f, "dominant pitch 527.4 Hz, octave of 174 Hz"),
                ),
                signals = ProfileSignals(
                    bpm = 72.5f,
                    spectralTiltDbPerOctave = -3.1f,
                    sub60HzEnergyFraction = 0.18f,
                    dominantHz = 527.4f,
                ),
            ),
            assignedBands = setOf("528", "174"),
            manualOverride = true,
        )

        val decoded = UserTracksStore.decode(UserTracksStore.encode(listOf(track)))
        assertEquals(1, decoded.size)
        val out = decoded[0]
        assertEquals(track.id, out.id)
        assertEquals(track.sourceUri, out.sourceUri)
        assertEquals(track.displayName, out.displayName)
        assertEquals(track.addedAtMs, out.addedAtMs)
        assertEquals(track.assignedBands, out.assignedBands)
        assertEquals(track.manualOverride, out.manualOverride)

        assertEquals(track.profile.matches.size, out.profile.matches.size)
        for (i in track.profile.matches.indices) {
            assertEquals(track.profile.matches[i].bandKey, out.profile.matches[i].bandKey)
            assertEquals(
                track.profile.matches[i].confidence,
                out.profile.matches[i].confidence,
                1e-6f,
            )
            assertEquals(track.profile.matches[i].reason, out.profile.matches[i].reason)
        }

        assertEquals(track.profile.signals.bpm!!, out.profile.signals.bpm!!, 1e-6f)
        assertEquals(
            track.profile.signals.spectralTiltDbPerOctave,
            out.profile.signals.spectralTiltDbPerOctave,
            1e-6f,
        )
        assertEquals(
            track.profile.signals.sub60HzEnergyFraction,
            out.profile.signals.sub60HzEnergyFraction,
            1e-6f,
        )
        assertEquals(track.profile.signals.dominantHz!!, out.profile.signals.dominantHz!!, 1e-6f)
    }

    @Test
    fun roundTrip_ancestralCase_emptyMatches_nullDominantHz() {
        // The canonical "no Solfeggio fit" case: ancestral drum has BPM
        // and sub-60 energy but no foreground tonic and no band match.
        // Persistence must preserve the empty matches list and the null
        // dominantHz so the listener can later file the track manually.
        val track = UserTrack(
            id = "drum-1",
            sourceUri = "content://x/y",
            displayName = "tribal_drum.mp3",
            addedAtMs = 1L,
            profile = BandAssignment(
                matches = emptyList(),
                signals = ProfileSignals(
                    bpm = 240f,
                    spectralTiltDbPerOctave = -5.5f,
                    sub60HzEnergyFraction = 0.78f,
                    dominantHz = null,
                ),
            ),
            assignedBands = emptySet(),
            manualOverride = false,
        )

        val out = UserTracksStore.decode(UserTracksStore.encode(listOf(track)))[0]
        assertTrue("matches must round-trip empty: ${out.profile.matches}", out.profile.matches.isEmpty())
        assertTrue("assignedBands must round-trip empty: ${out.assignedBands}", out.assignedBands.isEmpty())
        assertNull("dominantHz must round-trip null", out.profile.signals.dominantHz)
        assertEquals(240f, out.profile.signals.bpm!!, 1e-6f)
        assertEquals(0.78f, out.profile.signals.sub60HzEnergyFraction, 1e-6f)
    }

    @Test
    fun roundTrip_droneCase_nullBpm() {
        // A pure 528 Hz tone has dominantHz but no BPM (no pulse). Mirror
        // case to the ancestral one, exercising the other null axis.
        val track = UserTrack(
            id = "drone-1",
            sourceUri = "content://x/z",
            displayName = "528_pure.wav",
            addedAtMs = 2L,
            profile = BandAssignment(
                matches = listOf(BandMatch("528", 1.0f, "dominant pitch 528.0 Hz")),
                signals = ProfileSignals(
                    bpm = null,
                    spectralTiltDbPerOctave = 0f,
                    sub60HzEnergyFraction = 0f,
                    dominantHz = 528.0f,
                ),
            ),
            assignedBands = setOf("528"),
            manualOverride = false,
        )

        val out = UserTracksStore.decode(UserTracksStore.encode(listOf(track)))[0]
        assertNull("bpm must round-trip null", out.profile.signals.bpm)
        assertEquals(528f, out.profile.signals.dominantHz!!, 1e-6f)
        assertEquals(setOf("528"), out.assignedBands)
    }

    @Test
    fun roundTrip_multipleTracks_preservesOrder() {
        val tracks = (1..5).map { i ->
            UserTrack(
                id = "track-$i",
                sourceUri = "content://x/$i",
                displayName = "file_$i.mp3",
                addedAtMs = i.toLong() * 1_000,
                profile = BandAssignment(
                    matches = emptyList(),
                    signals = ProfileSignals(null, 0f, 0f, null),
                ),
                assignedBands = setOf(),
                manualOverride = false,
            )
        }
        val out = UserTracksStore.decode(UserTracksStore.encode(tracks))
        assertEquals(tracks.size, out.size)
        for (i in tracks.indices) {
            assertEquals(tracks[i].id, out[i].id)
            assertEquals(tracks[i].addedAtMs, out[i].addedAtMs)
        }
    }
}
