package com.soulradio.soulradio

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Locks the catalogue ([Frequencies.all]) against the filesystem layout under
 * `app/src/main/assets/audio/<key>/`. The catalogue is the source of truth for
 * which recording belongs to which band — i.e. the band's profile. A
 * [NowPlaying] entry whose file isn't on disk, or an audio file that no
 * catalogue entry references, both mean the listener gets a band whose profile
 * has drifted from what the curator chose.
 *
 * Spectral correctness (does the 528 station actually peak at 528 Hz?) is a
 * curator-review question and out of scope here — the catalogue is the
 * authority and these tests guard the contract between catalogue and disk.
 */
class AudioMappingTest {

    private val assetsRoot: File by lazy {
        // Gradle's Android unit-test task runs with cwd = the module dir, but
        // run-from-repo-root is common enough (e.g. `./gradlew :app:test` from
        // a parent shell) that we tolerate both.
        val candidates = listOf(
            File("src/main/assets/audio"),
            File("app/src/main/assets/audio"),
        )
        candidates.firstOrNull { it.isDirectory }
            ?: error(
                "Audio assets root not found. cwd=${File(".").absolutePath}. " +
                    "Tried: ${candidates.joinToString { it.path }}",
            )
    }

    @Test
    fun every_frequency_hasAnAssetFolder() {
        for (freq in Frequencies.all) {
            val folder = File(assetsRoot, freq.key)
            assertTrue(
                "Frequency ${freq.key} (${freq.title}) has no asset folder at ${folder.path}",
                folder.isDirectory,
            )
        }
    }

    @Test
    fun every_frequency_hasAtLeastOneTrack() {
        for (freq in Frequencies.all) {
            assertTrue(
                "Frequency ${freq.key} (${freq.title}) has zero tracks — the band has nothing to play",
                freq.tracks.isNotEmpty(),
            )
        }
    }

    @Test
    fun every_catalogue_asset_existsOnDisk() {
        // Forward: every NowPlaying.asset declared in Frequencies.kt must be
        // a real file at audio/<key>/<asset>. A missing file means the band
        // would silently fall back to whatever else the player picks up.
        val missing = mutableListOf<String>()
        for (freq in Frequencies.all) {
            for (track in freq.tracks) {
                val file = File(assetsRoot, "${freq.key}/${track.asset}")
                if (!file.isFile) {
                    missing += "${freq.key} → \"${track.asset}\" (${track.work}) at ${file.path}"
                }
            }
        }
        assertTrue(
            "Catalogue declares assets that don't exist on disk:\n  " +
                missing.joinToString("\n  "),
            missing.isEmpty(),
        )
    }

    @Test
    fun every_disk_audio_file_isReferencedByItsFolder() {
        // Reverse: every audio file in audio/<key>/ must be claimed by a
        // NowPlaying on that frequency. An orphan file means a recording
        // sits in a band the catalogue doesn't list — the wrong profile, by
        // definition. .gitkeep is the documented exception (see CLAUDE.md
        // § "Adding a recording").
        val orphans = mutableListOf<String>()
        for (freq in Frequencies.all) {
            val folder = File(assetsRoot, freq.key)
            if (!folder.isDirectory) continue
            val claimed = freq.tracks.map { it.asset }.toSet()
            val onDisk = folder.listFiles { f -> f.isFile && f.name != ".gitkeep" }
                ?.map { it.name }
                ?: emptyList()
            for (filename in onDisk) {
                if (filename !in claimed) {
                    orphans += "audio/${freq.key}/$filename"
                }
            }
        }
        assertTrue(
            "Audio files exist on disk that no NowPlaying entry references:\n  " +
                orphans.joinToString("\n  ") +
                "\nEither add a NowPlaying in Frequencies.kt, or move the file out of this band's folder.",
            orphans.isEmpty(),
        )
    }

    @Test
    fun no_duplicate_asset_within_a_frequency() {
        for (freq in Frequencies.all) {
            val seen = mutableSetOf<String>()
            for (track in freq.tracks) {
                assertTrue(
                    "Frequency ${freq.key} lists asset \"${track.asset}\" more than once — " +
                        "the same recording would be counted twice in the band's profile",
                    seen.add(track.asset),
                )
            }
        }
    }

    @Test
    fun no_asset_filename_appearsOnTwoBands() {
        // The same filename living in two frequencies' folders is legal at
        // the OS level (two distinct files) but a curatorial smell: the
        // same recording on two bands means at least one band has the wrong
        // profile. If a future change genuinely needs cross-band sharing,
        // revisit this test — it is a guardrail, not a manifesto rule.
        val byFilename = mutableMapOf<String, MutableList<String>>()
        for (freq in Frequencies.all) {
            for (track in freq.tracks) {
                byFilename.getOrPut(track.asset) { mutableListOf() }.add(freq.key)
            }
        }
        val collisions = byFilename.filterValues { it.size > 1 }
        assertTrue(
            "Asset filenames declared on more than one band: $collisions",
            collisions.isEmpty(),
        )
    }

    @Test
    fun trackByAsset_resolvesEveryDeclaredTrack() {
        // The MediaSession path looks up the now-playing card via
        // Frequencies.trackByAsset(freq, asset). If that lookup ever
        // disagreed with what the catalogue declares, the lockscreen would
        // show the wrong work/performer for a band whose audio is correct.
        for (freq in Frequencies.all) {
            for (track in freq.tracks) {
                val resolved = Frequencies.trackByAsset(freq, track.asset)
                assertTrue(
                    "trackByAsset(${freq.key}, ${track.asset}) returned null — the lookup " +
                        "disagrees with Frequencies.all",
                    resolved != null,
                )
                assertTrue(
                    "trackByAsset(${freq.key}, ${track.asset}) returned a different entry: " +
                        "expected work=\"${track.work}\", got \"${resolved?.work}\"",
                    resolved == track,
                )
            }
        }
    }
}
