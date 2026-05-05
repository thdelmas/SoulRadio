package com.soulradio.soulradio

// Entry data lives in CatalogueEntries.kt to stay under the 500-line cap.
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
