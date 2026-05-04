package com.soulradio.soulradio

/**
 * The wider catalogue exposed in Radio mode — frequencies documented in
 * [docs/tunables.md](../../../../../../docs/tunables.md) that did **not** earn a
 * place on the dial. The dial stays at eleven so the room can recede; this
 * catalogue lives in Radio mode, behind a deliberate door, so the wider
 * landscape is honest about itself without bleeding into the wallpaper.
 *
 * Each entry carries four fields, surfaced as separate sections when the
 * row expands:
 *
 * - **history** — where the frequency comes from, who named it, when.
 * - **believed** — what proponents/practitioners say it produces. Folklore
 *   reported as folklore. The radio's voice describes the *belief*, never
 *   asserts the effect.
 * - **studies** — what the literature actually shows (or, honestly, that
 *   it doesn't). When there's nothing, the field says so.
 * - **references** — artistic and traditional music known to live in this
 *   band. The point of the radio is curated music; this field is where a
 *   frequency's musical lineage shows up, or admits its absence.
 *
 * Tapping a row reveals these four sections; if the row's Hz is audible,
 * the same tap plays the tone as a sine demo.
 */
data class CatalogueEntry(
    val hz: String,
    val title: String,
    val group: CatalogueGroup,
    val history: String,
    val believed: String,
    val studies: String,
    val references: String,
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
        // properties of *recordings*, not standalone tones.
        CatalogueEntry(
            hz = "A=440",
            title = "Modern ISO standard (1939, ratified 1955)",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "Standardised at the 1939 Stuttgart conference and ratified by ISO in 1955 (ISO 16). Earlier 19th-century French diapason normal sat at A=435; Anglo-American orchestras drifted upward through the 20th century. Now the global default for orchestras, instruments, and recordings.",
            believed = "Critics in the \"432 Hz\" community describe it as too sharp and emotionally agitating compared to lower historical pitches. Defenders treat it simply as a coordinating standard, with no claim about the listener.",
            studies = "No replicated studies showing psychological or physiological effect from the standard itself. Small comparisons of A=440 vs A=432 (Calamassi & Pomponi 2019; Cox listening tests) find either no difference or mild subjective preference, not large effects.",
            references = "Default tuning of essentially all post-1950 commercial recordings. Modern symphony orchestras (Boston, Berlin, Vienna often slightly sharper at 442–445).",
        ),
        CatalogueEntry(
            hz = "A=415",
            title = "Baroque pitch (Bach, Vivaldi)",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "Adopted as the working standard of historically-informed performance for Baroque music since the early-music revival of the 1960s–70s. Roughly a semitone below A=440. Actual Baroque pitch varied by region — German organs as high as 466, French chapel as low as 392.",
            believed = "HIP performers say A=415 brings out the timbral character of gut strings, wooden flutes and Baroque-bow articulation as composers heard them — \"warmer\" and less brilliant than modern pitch.",
            studies = "A musicological standard, not a wellness frequency. No biological-effect literature exists for it.",
            references = "Bach cello suites and Brandenburg concertos as recorded by Jordi Savall, Philippe Herreweghe, John Eliot Gardiner. Vivaldi's Four Seasons in Baroque-pitch performances. Most of the Hänssler / Harmonia Mundi early-music catalogue.",
        ),
        CatalogueEntry(
            hz = "A=392",
            title = "French Baroque ton de chapelle",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "The lowest of the historical French church-organ pitches, used in 17th–18th c. Versailles chapel and other French sacred contexts. A whole tone below modern A=440.",
            believed = "Period-instrument performers say this very low tuning gives French sacred music — Charpentier, Couperin, Lully — its characteristic gravity and softness.",
            studies = "Pure musicological reference; no biological-effect literature.",
            references = "Marc-Antoine Charpentier's Te Deum, François Couperin's Leçons de Ténèbres, Lully's church music in performances by Les Arts Florissants and Le Concert Spirituel.",
        ),
        CatalogueEntry(
            hz = "A=466",
            title = "German Baroque Chorton (church organs)",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "Pitch of 17th–18th c. German church organs, roughly a semitone above modern A=440. Bach worked between Chorton (organ) and Kammerton (chamber, ~A=415) by transposing his cantata parts.",
            believed = "Period performers value Chorton as the actual pitch Bach's cantatas would have sounded at when sung in a Lutheran church with the organ.",
            studies = "None — a musicological reconstruction.",
            references = "Bach cantata recordings using Chorton-tuned reconstructed organs (Gardiner's Bach Cantata Pilgrimage occasionally; some Helmuth Rilling productions).",
        ),
        CatalogueEntry(
            hz = "A=435",
            title = "1859 French diapason normal",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "Set by French government decree in 1859, the first national tuning standard. Used in French opera houses through the late 19th century and ratified at the 1885 Vienna conference, before being eclipsed by A=440 in the mid-20th.",
            believed = "Sometimes proposed as a compromise between Verdi's A=432 and modern A=440.",
            studies = "None.",
            references = "Late 19th-c. French opera (Bizet, Massenet, Saint-Saëns) was composed and premiered at this pitch; some HIP recordings target it.",
        ),
        CatalogueEntry(
            hz = "A=421.6",
            title = "Mozart-era",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "Reconstructed from period tuning forks attributed to Mozart's circle in late-18th-c. Vienna. Other Mozart-era forks sit between 421 and 427 — there was no fixed standard.",
            believed = "HIP Mozart performers say his works gain transparency at this slightly-lower tuning compared to A=440.",
            studies = "None.",
            references = "Mozart symphonies and operas in period-instrument recordings by Christopher Hogwood, Jordi Savall, René Jacobs.",
        ),
        CatalogueEntry(
            hz = "A=422.5",
            title = "Handel's tuning fork",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "A tuning fork preserved at the Handel House Museum in London is calibrated at A=422.5; sometimes cited as Handel's working pitch. London pitch in his lifetime varied widely.",
            believed = "HIP Handel performers cite this fork as one anchor for \"Handel pitch.\"",
            studies = "None.",
            references = "Handel's Messiah and operas in period-pitch performances (William Christie, Trevor Pinnock, John Eliot Gardiner).",
        ),
        CatalogueEntry(
            hz = "A=444 / 449",
            title = "Modern sharper standards (Boston, 19th c. Paris Opera)",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "Some modern orchestras tune sharper than A=440 — Berlin Philharmonic ~443, Vienna Philharmonic ~444, Boston Symphony ~444, late-19th-c. Paris Opera up to 449 — to brighten ensemble sound.",
            believed = "Conductors and players say higher pitch projects more clearly in large halls and gives wind instruments greater brilliance. Audiences and critics often perceive it as more \"exciting.\"",
            studies = "No biological-effect studies; a working orchestral choice.",
            references = "Modern Vienna, Berlin, Boston symphony recordings; many post-1990 concert recordings drift sharper than nominal A=440.",
        ),
        CatalogueEntry(
            hz = "C=256",
            title = "Sauveur 1701 — \"scientific pitch\"",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "Proposed by French acoustician Joseph Sauveur in 1701 as a \"philosophical\" tuning where C is a power of 2 (256 = 2⁸). Revived in the 20th century by tuning advocacy associated with the Schiller Institute. Gives A≈430.5 — close to but not identical to Verdi's A=432.",
            believed = "Proponents say it produces a \"natural\" or \"mathematically pure\" octave structure aligned with whole-number-of-vibrations-per-second.",
            studies = "None of biological effect; the mathematical \"purity\" argument is numerological, not acoustic — every tuning system has whole-number ratios somewhere in it.",
            references = "A small body of \"scientific-pitch\" piano recordings exists, mostly identified with Schiller Institute \"Verdi tuning\" advocacy rather than mainstream practice.",
        ),

        // — Cousto cosmic octave (1978): orbital periods pitch-shifted up
        // into audible range. Real arithmetic; folkloric meaning.
        CatalogueEntry(
            hz = "136.1",
            title = "Earth year — \"OM tone\"",
            group = CatalogueGroup.COUSTO,
            history = "Hans Cousto, The Cosmic Octave (1978), pitch-shifted the Earth's orbital period (one year) up by 32 octaves to arrive at 136.1 Hz. Subsequently adopted by some Indian-classical practitioners as a tanpura tuning.",
            believed = "Cousto and followers say the tone resonates with the Earth's annual cycle. In Indian practice it is sometimes treated as the Sa of the cosmos and used as a drone for raga performance.",
            studies = "No replicated biological-effect studies. The orbital arithmetic is correct; the meaning attributed to it is interpretive.",
            references = "Tanpura recordings tuned to 136.1 (often labelled \"OM\") used as drone underneath Indian classical raga; planetary-tone meditation albums.",
        ),
        CatalogueEntry(
            hz = "194.18",
            title = "Earth day",
            group = CatalogueGroup.COUSTO,
            history = "Cousto's pitch-shift of the 24-hour solar day, ~194.18 Hz after 24 octaves of doubling.",
            believed = "Proponents say it resonates with the daily circadian cycle.",
            studies = "None.",
            references = "Used in some \"planet tone\" sound-bath sets; no traditional musical lineage.",
        ),
        CatalogueEntry(
            hz = "126.22",
            title = "Sun",
            group = CatalogueGroup.COUSTO,
            history = "Cousto's pitch-shift of the synodic rotation period of the Sun (~25 days at the equator).",
            believed = "Proponents say it carries solar energy and radiance.",
            studies = "None.",
            references = "New-age \"planet tone\" sets; no traditional lineage.",
        ),
        CatalogueEntry(
            hz = "210.42",
            title = "Moon",
            group = CatalogueGroup.COUSTO,
            history = "Cousto's pitch-shift of the synodic lunar month (~29.5 days).",
            believed = "Proponents say it resonates with menstrual and tidal cycles. Sometimes used in moon-phase sound work.",
            studies = "None.",
            references = "New-age \"planet tone\" sets; lunar sound-bath repertoire.",
        ),
        CatalogueEntry(
            hz = "144.72",
            title = "Mars",
            group = CatalogueGroup.COUSTO,
            history = "Cousto's pitch-shift of the Martian sidereal year (~687 Earth days).",
            believed = "Cousto's framework associates each planet with classical astrological qualities — Mars with action, courage, assertion.",
            studies = "None.",
            references = "Astrological / planetary sound-work; no musical lineage.",
        ),
        CatalogueEntry(
            hz = "183.58",
            title = "Jupiter",
            group = CatalogueGroup.COUSTO,
            history = "Cousto's pitch-shift of the Jovian sidereal year (~12 Earth years).",
            believed = "Associated by proponents with expansion, generosity, prosperity (classical astrological Jupiter).",
            studies = "None.",
            references = "Astrological / planetary sound-work.",
        ),
        CatalogueEntry(
            hz = "147.85",
            title = "Saturn",
            group = CatalogueGroup.COUSTO,
            history = "Cousto's pitch-shift of the Saturnian sidereal year (~29.5 Earth years).",
            believed = "Associated by proponents with structure, discipline, and time (classical astrological Saturn).",
            studies = "None.",
            references = "Astrological / planetary sound-work.",
        ),

        // — Schumann harmonics. The fundamental (7.83) is on the dial.
        CatalogueEntry(
            hz = "14.3",
            title = "Schumann · 2nd harmonic",
            group = CatalogueGroup.SCHUMANN_HARMONICS,
            history = "The Earth–ionosphere cavity has a fundamental electromagnetic resonance at ~7.83 Hz (Winfried Schumann, 1952) and harmonics at ~14.3, 20.8, 27.3 and 33.8 Hz — real, continuously measurable geophysical phenomena driven by global lightning activity.",
            believed = "The wider \"Schumann resonance\" wellness narrative associates exposure with grounding, sleep quality, and nervous-system regulation. Specific lore around the 2nd harmonic vs. fundamental is folkloric.",
            studies = "The geophysics is well-established and routinely cited in atmospheric-physics literature. Biological-effect studies (Cherry 2002, König 1971 and successors) are small, mostly unreplicated, and frequently conflated with separate ELF-magnetic-field literature.",
            references = "No musical tradition uses 14.3 Hz directly — it sits at the audible threshold. Sub-audible drones in modern ambient (Stars of the Lid, Eliane Radigue) sometimes intersect this range.",
        ),
        CatalogueEntry(
            hz = "20.8",
            title = "Schumann · 3rd harmonic",
            group = CatalogueGroup.SCHUMANN_HARMONICS,
            history = "Third harmonic of the Earth–ionosphere cavity resonance, around 20.8 Hz, at the threshold of human hearing.",
            believed = "Same Schumann-resonance narrative as the fundamental, with no specific lore attached to this harmonic.",
            studies = "Geophysics solid; no biological-effect studies for this harmonic specifically.",
            references = "Below most instruments' fundamental range. Very large pipe-organ pedal stops (32-foot) reach into this neighbourhood; otherwise no tradition.",
        ),
        CatalogueEntry(
            hz = "27.3",
            title = "Schumann · 4th harmonic",
            group = CatalogueGroup.SCHUMANN_HARMONICS,
            history = "Fourth harmonic of the Earth–ionosphere cavity resonance.",
            believed = "Same Schumann-resonance narrative.",
            studies = "Geophysics solid; no biological-effect studies for this harmonic specifically.",
            references = "Roughly the lowest A on a standard piano (27.5 Hz) — the very bottom of conventional instrumental range. No targeted musical tradition.",
        ),
        CatalogueEntry(
            hz = "33.8",
            title = "Schumann · 5th harmonic",
            group = CatalogueGroup.SCHUMANN_HARMONICS,
            history = "Fifth harmonic of the Earth–ionosphere cavity resonance.",
            believed = "Same Schumann-resonance narrative.",
            studies = "Geophysics solid; no biological-effect studies for this harmonic specifically.",
            references = "Roughly C₁ on the piano (32.7 Hz). No tradition specifically targeting this Hz.",
        ),

        // — Brainwave bands.
        CatalogueEntry(
            hz = "Delta",
            title = "0.5 – 4 Hz · sub-audible",
            group = CatalogueGroup.BRAINWAVE,
            history = "Identified by Hans Berger and W. Grey Walter in early-20th-c. EEG work as the slow oscillation dominant in deep (stage 3/4 NREM) sleep.",
            believed = "Audio \"delta\" tracks (typically binaural or isochronic) are sold for sleep onset and deep rest. The premise: external Hz drives brain Hz via \"entrainment.\"",
            studies = "Polysomnography literature confirms delta = deep sleep. Whether external delta-frequency audio reliably drives EEG into delta states is contested — small studies (Wahbeh, Lane) show modest effects; larger reviews (Garcia-Argibay 2019) find weak, inconsistent results.",
            references = "No musical tradition at this register — sub-audible. Sleep-focused binaural / isochronic products dominate the market.",
        ),
        CatalogueEntry(
            hz = "Alpha",
            title = "8 – 12 Hz · audible",
            group = CatalogueGroup.BRAINWAVE,
            history = "Berger's original \"alpha rhythm\" — prominent EEG oscillation when awake with eyes closed, present in relaxed wakefulness.",
            believed = "Alpha-frequency audio is sold for relaxation, meditation onset, and creativity. Premise: external Hz drives EEG into alpha.",
            studies = "Alpha = relaxed wakefulness is well-documented. Audio-entrainment effects on alpha are mixed; some studies show modest effects on subjective relaxation, larger reviews are skeptical of robust, replicable EEG entrainment.",
            references = "No musical tradition behind the band itself. Drone instruments (didgeridoo, tanpura, organ pedal) often pulse in this range as amplitude modulation rather than fundamental pitch.",
        ),
        CatalogueEntry(
            hz = "Beta",
            title = "12 – 30 Hz · audible",
            group = CatalogueGroup.BRAINWAVE,
            history = "Berger / Walter — EEG oscillation associated with active waking cognition, problem-solving, and arousal.",
            believed = "Sold as \"focus\" or \"productivity\" audio.",
            studies = "Beta = active cognition, well-documented. \"Focus audio\" claims are weak; commercial products (Brain.fm, Endel) cite small in-house studies, no large independent replications.",
            references = "No musical tradition. Modern \"focus\" streaming products dominate.",
        ),
        CatalogueEntry(
            hz = "Gamma",
            title = "30 – 100 Hz (40 typical)",
            group = CatalogueGroup.BRAINWAVE,
            history = "Higher-frequency EEG associated with cross-cortical binding, attention, and perceptual integration. The 40 Hz band is studied especially in connection with consciousness and Alzheimer's research.",
            believed = "Marketed for cognition, focus, and \"neural coherence.\"",
            studies = "Real neuroscience at 40 Hz includes the Tsai-lab work at MIT (Iaccarino 2016, Martorell 2019) showing 40 Hz audiovisual stimulation may reduce amyloid plaques in mice, with early-stage human trials ongoing. Generic consumer \"gamma audio\" tracks do not replicate the protocol.",
            references = "No musical tradition at the band itself.",
        ),

        // — Other named tones in circulation.
        CatalogueEntry(
            hz = "8",
            title = "\"Earth pulse\" / \"genius frequency\"",
            group = CatalogueGroup.NAMED,
            history = "A New-Age conflation: rounds 7.83 Hz to 8, sometimes attributed to Tesla (no documented source), sometimes tied to apocryphal \"creative state\" measurements of Einstein's brainwaves. A separate lineage from the actual Schumann fundamental.",
            believed = "Proponents say it correlates with creativity, \"genius states,\" and hemispheric coherence.",
            studies = "None supporting the claims. The actual Schumann fundamental at 7.83 has its own (small, contested) literature.",
            references = "Folklore stacked on folklore; no musical tradition.",
        ),
        CatalogueEntry(
            hz = "40",
            title = "\"Neuroscience gamma\"",
            group = CatalogueGroup.NAMED,
            history = "Real neuroscience interest at 40 Hz, especially the MIT Tsai-lab work on Alzheimer's-relevant audiovisual stimulation. Distinct from consumer \"gamma audio\" products.",
            believed = "Consumer gamma-audio products are marketed for cognition, memory protection, and focus.",
            studies = "Tsai-lab studies (Iaccarino et al. 2016, Martorell et al. 2019) used controlled audiovisual stimulation in mice and small human cohorts; results are early-stage and replication is ongoing. Generic 40 Hz tracks sold as wellness audio do not replicate the protocol or measurement.",
            references = "No traditional musical context for the bare frequency.",
        ),
        CatalogueEntry(
            hz = "1122",
            title = "Solfeggio outlier",
            group = CatalogueGroup.NAMED,
            history = "Sometimes appended to the modern 9-tone Solfeggio set as a 10th tone. Joseph Puleo's 1999 Solfeggio book — the founding modern source — listed only nine; 1122 enters in later New-Age publishing.",
            believed = "Marketed (when marketed) as a higher-octave or \"ascension\" frequency.",
            studies = "None.",
            references = "Marginal even within Solfeggio listings; no curated music at this band.",
        ),

        // — Numerological / Pythagorean.
        CatalogueEntry(
            hz = "111 … 999",
            title = "\"Angel numbers\"",
            group = CatalogueGroup.NUMEROLOGY,
            history = "Repeating-digit number sequences popularised by 21st-c. New-Age numerology (Doreen Virtue's Angel Numbers, 2005, and earlier works on angel-guidance themes). Each number is assigned an \"angelic message.\"",
            believed = "Believers say repeating-digit Hz tones carry the corresponding numerological meaning — manifestation, alignment, the presence of unseen guidance.",
            studies = "None — pure numerology with no acoustic basis.",
            references = "No musical tradition. Generated as bare sines for numerology-themed content.",
        ),
    )

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
     */
    fun audibleHzFor(hzString: String): Double? {
        val numeric = NUMERIC_PATTERN.find(hzString)?.value?.toDoubleOrNull()
        return numeric?.takeIf { it >= SineDemo.MIN_AUDIBLE_HZ }
    }

    private val NUMERIC_PATTERN = Regex("""\d+(?:\.\d+)?""")
}
