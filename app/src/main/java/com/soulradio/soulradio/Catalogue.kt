package com.soulradio.soulradio

/**
 * The wider catalogue exposed in Radio mode — frequencies documented in
 * [docs/tunables.md](../../../../../../docs/tunables.md) that did **not** earn a
 * place on the dial. The dial stays at eleven so the room can recede; this
 * catalogue lives in Radio mode, behind a deliberate door, so the wider
 * landscape is honest about itself without bleeding into the wallpaper.
 *
 * Entries here are informational in the MVP — no bundled audio, no sine
 * synthesis. Tapping a row reveals the rationale; that is the product.
 */
data class CatalogueEntry(
    val hz: String,
    val title: String,
    val group: CatalogueGroup,
    val rationale: String,
)

enum class CatalogueGroup(val label: String) {
    REFERENCE_PITCH("Historical reference pitches"),
    COUSTO("Cousto cosmic octave"),
    SCHUMANN_HARMONICS("Schumann harmonics"),
    BRAINWAVE("Brainwave bands"),
    NAMED("Other named tones"),
    NUMEROLOGY("Numerological"),
}

object Catalogue {
    val entries: List<CatalogueEntry> = listOf(
        // — Historical reference pitches. The value of A4. These are
        // properties of *recordings*, not standalone tones — you cannot
        // tune the radio to A=415, you can only choose recordings already
        // performed at A=415. Verdi's A (432) is the one exception, and
        // it lives on the dial as a companion paired with acoustic-era
        // opera. Everything else here is documented for the listener
        // wondering "why isn't A=440 a station?"
        CatalogueEntry(
            hz = "A=440",
            title = "Modern ISO standard (1939, ratified 1955)",
            group = CatalogueGroup.REFERENCE_PITCH,
            rationale = "Already the default tuning of most bundled recordings. Making it a station would mean making \"the modern world\" a station — there is nothing to listen to that isn't already at A=440.",
        ),
        CatalogueEntry(
            hz = "A=415",
            title = "Baroque pitch (Bach, Vivaldi)",
            group = CatalogueGroup.REFERENCE_PITCH,
            rationale = "A property of historically-informed recordings, not a station. A Bach cello prelude at A=415 belongs on 174 (foundation), not on its own band.",
        ),
        CatalogueEntry(
            hz = "A=392",
            title = "French Baroque ton de chapelle",
            group = CatalogueGroup.REFERENCE_PITCH,
            rationale = "Same logic as A=415 — it is the tuning of the recording, not a thing to tune the radio to.",
        ),
        CatalogueEntry(
            hz = "A=466",
            title = "German Baroque Chorton (church organs)",
            group = CatalogueGroup.REFERENCE_PITCH,
            rationale = "Same.",
        ),
        CatalogueEntry(
            hz = "A=435",
            title = "1859 French diapason normal",
            group = CatalogueGroup.REFERENCE_PITCH,
            rationale = "Bridges 432 and 440; nothing on the dial would change if it existed.",
        ),
        CatalogueEntry(
            hz = "A=421.6",
            title = "Mozart-era",
            group = CatalogueGroup.REFERENCE_PITCH,
            rationale = "A property of period recordings, not a dial station. Same as A=415.",
        ),
        CatalogueEntry(
            hz = "A=422.5",
            title = "Handel's tuning fork",
            group = CatalogueGroup.REFERENCE_PITCH,
            rationale = "Same.",
        ),
        CatalogueEntry(
            hz = "A=444 / 449",
            title = "Modern sharper standards (Boston, 19th c. Paris Opera)",
            group = CatalogueGroup.REFERENCE_PITCH,
            rationale = "Brighter than 440; not a contemplative direction. The radio's curatorial axis runs the other way.",
        ),
        CatalogueEntry(
            hz = "C=256",
            title = "Sauveur 1701 — \"scientific pitch\"",
            group = CatalogueGroup.REFERENCE_PITCH,
            rationale = "Already approximated by A=432 (which gives C≈256). Adding it duplicates Verdi's A on the dial.",
        ),

        // — Cousto cosmic octave (1978): orbital periods pitch-shifted up
        // into audible range. Real arithmetic; folkloric meaning.
        CatalogueEntry(
            hz = "136.1",
            title = "Earth year — \"OM tone\"",
            group = CatalogueGroup.COUSTO,
            rationale = "Adopted by Indian classical practice as a tanpura tuning, which is a real and defensible use. The radio already covers that lineage at 396 (raga Bhairavi) and 7.83 (raga Yaman, Subbulakshmi), so 136.1 would duplicate without adding ground.",
        ),
        CatalogueEntry(
            hz = "194.18",
            title = "Earth day",
            group = CatalogueGroup.COUSTO,
            rationale = "Cousto-derived, no musical tradition behind it. Numerology with a wellness gloss.",
        ),
        CatalogueEntry(
            hz = "126.22",
            title = "Sun",
            group = CatalogueGroup.COUSTO,
            rationale = "Cousto pitch-shift of the solar period. Same problem: no tradition to curate, just a number.",
        ),
        CatalogueEntry(
            hz = "210.42",
            title = "Moon",
            group = CatalogueGroup.COUSTO,
            rationale = "Cousto pitch-shift of the lunar period. Adding one Cousto tone means adding the set, and the set is the \"wellness audio that wasn't made as music\" the manifesto rejects.",
        ),
        CatalogueEntry(
            hz = "144.72",
            title = "Mars",
            group = CatalogueGroup.COUSTO,
            rationale = "Pitch-shifted orbital period. No musical lineage; bare sine territory.",
        ),
        CatalogueEntry(
            hz = "183.58",
            title = "Jupiter",
            group = CatalogueGroup.COUSTO,
            rationale = "Pitch-shifted orbital period. Same.",
        ),
        CatalogueEntry(
            hz = "147.85",
            title = "Saturn",
            group = CatalogueGroup.COUSTO,
            rationale = "Pitch-shifted orbital period. Same.",
        ),

        // — Schumann harmonics. The fundamental (7.83) is on the dial as a
        // sub-audible companion; stacking the harmonics would turn the night
        // band into a frequency lab.
        CatalogueEntry(
            hz = "14.3",
            title = "Schumann · 2nd harmonic",
            group = CatalogueGroup.SCHUMANN_HARMONICS,
            rationale = "The fundamental at 7.83 is on the dial. The harmonics are real geophysics, but stacking them would crowd the night band without adding listenable ground.",
        ),
        CatalogueEntry(
            hz = "20.8",
            title = "Schumann · 3rd harmonic",
            group = CatalogueGroup.SCHUMANN_HARMONICS,
            rationale = "Same — geophysically real, musically unfilled. 7.83 already covers the role.",
        ),
        CatalogueEntry(
            hz = "27.3",
            title = "Schumann · 4th harmonic",
            group = CatalogueGroup.SCHUMANN_HARMONICS,
            rationale = "Same.",
        ),
        CatalogueEntry(
            hz = "33.8",
            title = "Schumann · 5th harmonic",
            group = CatalogueGroup.SCHUMANN_HARMONICS,
            rationale = "Same.",
        ),

        // — Brainwave bands. Refused as foreground stations because (a) no
        // band has a musical tradition behind it, and (b) the category arrives
        // pre-loaded with state-induction framing the manifesto avoids. 7.83
        // is the existence proof that a brainwave-band Hz can be integrated
        // honestly — by sitting under music, not as foreground.
        CatalogueEntry(
            hz = "Delta",
            title = "0.5 – 4 Hz · sub-audible",
            group = CatalogueGroup.BRAINWAVE,
            rationale = "Below the threshold of human hearing. Can only function as pulse/modulation — the role 7.83 already fills under the night band. A foreground Delta station would land as bare sine, refused on audio grounds.",
        ),
        CatalogueEntry(
            hz = "Alpha",
            title = "8 – 12 Hz · audible",
            group = CatalogueGroup.BRAINWAVE,
            rationale = "Audible, but no musical tradition behind the band as such. Would land as bare sine, refused on audio grounds. \"Relaxation\" framing dominates the market and would be hard to escape with copy alone.",
        ),
        CatalogueEntry(
            hz = "Beta",
            title = "12 – 30 Hz · audible",
            group = CatalogueGroup.BRAINWAVE,
            rationale = "Same \"no tradition\" problem. \"Focus / productivity\" framing dominates — and productivity is the engagement-loop register the radio rejects.",
        ),
        CatalogueEntry(
            hz = "Gamma",
            title = "30 – 100 Hz (40 typical)",
            group = CatalogueGroup.BRAINWAVE,
            rationale = "Real research at 40 Hz; product claims around it are not. Pre-loaded with cognition / attention framing; no tradition to fill the band as anything but bare sine.",
        ),

        // — Other named tones in circulation.
        CatalogueEntry(
            hz = "8",
            title = "\"Earth pulse\" / \"genius frequency\"",
            group = CatalogueGroup.NAMED,
            rationale = "Folklore stacked on top of folklore. 7.83 already covers the sub-audible pulse role honestly.",
        ),
        CatalogueEntry(
            hz = "40",
            title = "\"Neuroscience gamma\"",
            group = CatalogueGroup.NAMED,
            rationale = "Real research; product claims are not. Medical-claim territory the manifesto refuses absolutely.",
        ),
        CatalogueEntry(
            hz = "1122",
            title = "Solfeggio outlier",
            group = CatalogueGroup.NAMED,
            rationale = "Sometimes appended to the modern Solfeggio set. Marginal even within the tradition. Nine is enough.",
        ),

        // — Numerological / Pythagorean.
        CatalogueEntry(
            hz = "111 … 999",
            title = "\"Angel numbers\"",
            group = CatalogueGroup.NUMEROLOGY,
            rationale = "Numerology with no musical tradition behind it. Bare sines; nothing to curate.",
        ),
    )

    val byGroup: Map<CatalogueGroup, List<CatalogueEntry>> =
        entries.groupBy { it.group }

    /**
     * Extract the first numeric Hz value from an entry's `hz` string and
     * return it only if it is in the audible range. Used by Radio mode
     * to decide whether tapping a row should start a sine demo or only
     * toggle the rationale.
     *
     * Examples:
     * - "528"            → 528.0
     * - "136.1"          → 136.1
     * - "A=440"          → 440.0
     * - "A=444 / 449"    → 444.0
     * - "111 … 999"      → 111.0
     * - "Delta", "Alpha" → null (no numeric)
     * - "0.5", "14.3"    → null (sub-audible)
     */
    fun audibleHzFor(hzString: String): Double? {
        val numeric = NUMERIC_PATTERN.find(hzString)?.value?.toDoubleOrNull()
        return numeric?.takeIf { it >= SineDemo.MIN_AUDIBLE_HZ }
    }

    private val NUMERIC_PATTERN = Regex("""\d+(?:\.\d+)?""")
}
