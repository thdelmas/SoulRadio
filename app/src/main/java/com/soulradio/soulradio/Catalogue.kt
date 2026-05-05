package com.soulradio.soulradio

/**
 * The wider catalogue exposed in Radio mode — frequencies documented in
 * [docs/tunables.md](../../../../../../docs/tunables.md) that did **not** earn a
 * place on the dial. The dial stays at eleven so the room can recede; this
 * catalogue lives in Radio mode, behind a deliberate door, so the wider
 * landscape is honest about itself without bleeding into the wallpaper.
 *
 * Each entry carries five fields, surfaced as separate sections when the
 * row expands:
 *
 * - **history** — where the frequency comes from, who named it, when.
 * - **uses** — how the frequency has actually been used, in whichever
 *   community uses it: sound-healing, biohacker / wellness, neurofeedback,
 *   research neuroscience, manifestation / numerology practice, or
 *   musical performance. Descriptive of the practice, not a claim by the
 *   radio about what the tone does to a body. Exempt from the
 *   medical-claim linter — describing that a delta-band track is *sold
 *   for sleep onset* is not the radio prescribing the track.
 * - **studies** — what the literature actually shows (or, honestly, that
 *   it doesn't). When there's nothing, the field says so.
 * - **references** — concrete examples a listener can chase down. For
 *   biohacker frequencies that means specific products and channels
 *   (Holosync, Brain.fm, Meditative Mind); for musical frequencies that
 *   means specific recordings and performers. The radio is honest about
 *   which world a frequency lives in.
 * - **usage** — when a listener might actually reach for this band, or
 *   honestly that there is no foreground role. Phrased as context, never
 *   as prescription.
 *
 * Tapping a row reveals these five sections; if the row's Hz is audible,
 * the same tap plays the tone as a sine demo.
 *
 * Some entries also carry [compositions] — a curated shortlist of real
 * artistic recordings tied to the band, surfaced visually with the same
 * work / performer aesthetic the dial uses for [NowPlaying]. The list is
 * deliberately sparse: only entries with a genuine pre-electronic
 * artistic lineage (most historical reference pitches; the OM-tone
 * tanpura tradition) earn one. Wellness-product albums stay in the
 * **references** prose, where the radio is honest about their world
 * without dressing them as curated music.
 *
 * The actual entry data lives in [CatalogueEntries.kt][catalogueEntries] so
 * this file stays under the 500-line cap.
 */
data class CatalogueEntry(
    val hz: String,
    val title: String,
    val group: CatalogueGroup,
    val history: String,
    val uses: String,
    val studies: String,
    val references: String,
    val usage: String,
    val compositions: List<Composition> = emptyList(),
)

/**
 * A single artistic recording surfaced under a Radio entry — name of the
 * work and the named performer / ensemble. Mirrors [NowPlaying] but
 * carries no asset; Radio mode does not bundle these recordings, it
 * points the listener at them. The pairing is the same one the dial uses
 * in its caption, so the visual register reads as continuous between
 * the two modes.
 */
data class Composition(val work: String, val performer: String)

enum class CatalogueGroup(val label: String) {
    REFERENCE_PITCH("Historical reference pitches"),
    COUSTO("Cousto cosmic octave"),
    SCHUMANN_HARMONICS("Schumann harmonics"),
    BRAINWAVE("Brainwave bands"),
    ENTRAINMENT_DELIVERY("Brainwave entrainment delivery"),
    NOISE_COLOR("Noise colors"),
    VIBROACOUSTIC("Vibroacoustic / sub-audible"),
    NAMED("Other named tones"),
    NUMEROLOGY("Numerological"),
}

object Catalogue {
    val entries: List<CatalogueEntry> = catalogueEntries

    val byGroup: Map<CatalogueGroup, List<CatalogueEntry>> =
        entries.groupBy { it.group }

    /**
     * Extract the first numeric Hz value from an entry's `hz` string and
     * return it only if it is in the audible range. Used by Radio mode
     * to decide whether tapping a row should start a sine demo or only
     * expand the entry sections.
     *
     * Examples:
     * - "528"            → 528.0
     * - "136.1"          → 136.1
     * - "A=440"          → 440.0
     * - "A=444 / 449"    → 444.0
     * - "111 … 999"      → 111.0
     * - "Delta", "Alpha" → null (no numeric)
     * - "0.5", "14.3"    → null (sub-audible)
     * - "White", "Pink"  → null (spectral shapes, not single Hz)
     */
    fun audibleHzFor(hzString: String): Double? {
        val numeric = NUMERIC_PATTERN.find(hzString)?.value?.toDoubleOrNull()
        return numeric?.takeIf { it >= SineDemo.MIN_AUDIBLE_HZ }
    }

    private val NUMERIC_PATTERN = Regex("""\d+(?:\.\d+)?""")
}
