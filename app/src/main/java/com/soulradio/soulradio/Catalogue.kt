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
            uses = "Used since 1939 as the global tuning reference for orchestras, instrument manufacture, and recording. Default tuning of essentially all post-1950 commercial music. Critiqued in the \"432 Hz\" sound-healing community as too sharp and over-energising; defended in the music establishment as a coordinating standard.",
            studies = "No replicated studies showing psychological or physiological effect from the standard itself. Small comparisons of A=440 vs A=432 (Calamassi & Pomponi 2019; Cox listening tests) find either no difference or mild subjective preference, not large effects.",
            references = "Default tuning of essentially all post-1950 commercial recordings. Modern symphony orchestras (Boston, Berlin, Vienna often slightly sharper at 442–445).",
            usage = "Default — every modern recording is already at this pitch. There is no separate listening occasion; this is the world's tuning.",
        ),
        CatalogueEntry(
            hz = "A=415",
            title = "Baroque pitch (Bach, Vivaldi)",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "Adopted as the working standard of historically-informed performance for Baroque music since the early-music revival of the 1960s–70s. Roughly a semitone below A=440. Actual Baroque pitch varied by region — German organs as high as 466, French chapel as low as 392.",
            uses = "Used in historically-informed Baroque performance since the early-music revival of the 1960s — Bach Brandenburg concertos, Vivaldi Four Seasons, Handel oratorios on period instruments. The standard pitch of the Harmonia Mundi, Hänssler, and Archiv early-music catalogues.",
            studies = "A musicological standard, not a wellness frequency. No biological-effect literature exists for it.",
            references = "Bach cello suites and Brandenburg concertos as recorded by Jordi Savall, Philippe Herreweghe, John Eliot Gardiner. Vivaldi's Four Seasons in Baroque-pitch performances. Most of the Hänssler / Harmonia Mundi early-music catalogue.",
            usage = "When sitting with historically-informed Baroque performance — Bach cello suites, Vivaldi concerti, Handel operas on period instruments.",
            compositions = listOf(
                Composition("Bach · Cello Suites, BWV 1007–1012", "Jordi Savall"),
                Composition("Bach · Brandenburg Concertos", "Philippe Herreweghe · Collegium Vocale Gent"),
                Composition("Vivaldi · The Four Seasons", "Il Giardino Armonico · Giovanni Antonini"),
                Composition("Handel · Messiah", "John Eliot Gardiner · English Baroque Soloists"),
            ),
        ),
        CatalogueEntry(
            hz = "A=392",
            title = "French Baroque ton de chapelle",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "The lowest of the historical French church-organ pitches, used in 17th–18th c. Versailles chapel and other French sacred contexts. A whole tone below modern A=440.",
            uses = "Used in 17th–18th c. French sacred music: the Versailles royal chapel under Louis XIV, the Concert Spirituel, daily liturgy at Notre-Dame. Charpentier, Couperin, Lully composed inside this register. Modern HIP ensembles target it for their French-Baroque catalogue.",
            studies = "Pure musicological reference; no biological-effect literature.",
            references = "Marc-Antoine Charpentier's Te Deum, François Couperin's Leçons de Ténèbres, Lully's church music in performances by Les Arts Florissants and Le Concert Spirituel.",
            usage = "When listening to French-Baroque sacred repertoire in low-pitch performance — Charpentier, Couperin, Lully chapel music.",
            compositions = listOf(
                Composition("Charpentier · Te Deum, H. 146", "Les Arts Florissants · William Christie"),
                Composition("Couperin · Leçons de Ténèbres", "Les Arts Florissants · William Christie"),
                Composition("Lully · Grands Motets", "Le Concert Spirituel · Hervé Niquet"),
            ),
        ),
        CatalogueEntry(
            hz = "A=466",
            title = "German Baroque Chorton (church organs)",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "Pitch of 17th–18th c. German church organs, roughly a semitone above modern A=440. Bach worked between Chorton (organ) and Kammerton (chamber, ~A=415) by transposing his cantata parts.",
            uses = "Used by 17th–18th c. German Lutheran church organs. Bach's cantatas were sung at Chorton; he transposed orchestral parts so that Kammerton instruments could play with the higher-tuned organ. A small number of modern organ-rebuild projects have restored the higher pitch.",
            studies = "None — a musicological reconstruction.",
            references = "Bach cantata recordings using Chorton-tuned reconstructed organs (Gardiner's Bach Cantata Pilgrimage occasionally; some Helmuth Rilling productions).",
            usage = "When listening to Bach cantatas performed on Chorton-tuned reconstructed organs — a small slice of the period-instrument catalogue.",
            compositions = listOf(
                Composition("Bach · Cantata Pilgrimage (the cycle)", "Monteverdi Choir · John Eliot Gardiner"),
                Composition("Bach · Cantatas (Stuttgart cycle)", "Helmuth Rilling · Gächinger Kantorei"),
            ),
        ),
        CatalogueEntry(
            hz = "A=435",
            title = "1859 French diapason normal",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "Set by French government decree in 1859, the first national tuning standard. Used in French opera houses through the late 19th century and ratified at the 1885 Vienna conference, before being eclipsed by A=440 in the mid-20th.",
            uses = "Used in French opera houses, the Paris Conservatoire, and many European orchestras from 1859 until A=440 displaced it in the mid-20th c. Bizet, Massenet, Saint-Saëns composed and premiered inside this pitch. The Met and most major theatres held A in this neighbourhood until WWII.",
            studies = "None.",
            references = "Late 19th-c. French opera (Bizet, Massenet, Saint-Saëns) was composed and premiered at this pitch; some HIP recordings target it.",
            usage = "When listening to late-19th-c. French opera in period-pitch performance — Bizet, Massenet, Saint-Saëns at the pitch they were premiered in.",
            compositions = listOf(
                Composition("Bizet · Carmen (premiere-pitch performance)", "Late-19th-c. Opéra-Comique repertoire"),
                Composition("Saint-Saëns · Symphony No. 3 \"Organ\"", "Period-pitch HIP performance"),
            ),
        ),
        CatalogueEntry(
            hz = "A=421.6",
            title = "Mozart-era",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "Reconstructed from period tuning forks attributed to Mozart's circle in late-18th-c. Vienna. Other Mozart-era forks sit between 421 and 427 — there was no fixed standard.",
            uses = "Used in late-18th-c. Vienna by Mozart's circle and contemporaries. Modern Mozart performance on period instruments targets this neighbourhood — René Jacobs, Christopher Hogwood, the Academy of Ancient Music.",
            studies = "None.",
            references = "Mozart symphonies and operas in period-instrument recordings by Christopher Hogwood, Jordi Savall, René Jacobs.",
            usage = "When listening to Mozart on period instruments tuned to a Viennese fork — much of René Jacobs, Christopher Hogwood, the Academy of Ancient Music.",
            compositions = listOf(
                Composition("Mozart · Symphonies 38–41", "Academy of Ancient Music · Christopher Hogwood"),
                Composition("Mozart · Le Nozze di Figaro", "Concerto Köln · René Jacobs"),
                Composition("Mozart · Requiem, K. 626", "Le Concert des Nations · Jordi Savall"),
            ),
        ),
        CatalogueEntry(
            hz = "A=422.5",
            title = "Handel's tuning fork",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "A tuning fork preserved at the Handel House Museum in London is calibrated at A=422.5; sometimes cited as Handel's working pitch. London pitch in his lifetime varied widely.",
            uses = "Used in early-18th-c. London — Handel's Royal Academy operas, the Foundling Hospital Messiah performances. Modern period-correct Messiah recordings target this neighbourhood.",
            studies = "None.",
            references = "Handel's Messiah and operas in period-pitch performances (William Christie, Trevor Pinnock, John Eliot Gardiner).",
            usage = "When listening to Handel oratorios and operas in London-fork pitch performance.",
            compositions = listOf(
                Composition("Handel · Messiah", "Les Arts Florissants · William Christie"),
                Composition("Handel · Giulio Cesare", "The English Concert · Trevor Pinnock"),
                Composition("Handel · Israel in Egypt", "Monteverdi Choir · John Eliot Gardiner"),
            ),
        ),
        CatalogueEntry(
            hz = "A=444 / 449",
            title = "Modern sharper standards (Boston, 19th c. Paris Opera)",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "Some modern orchestras tune sharper than A=440 — Berlin Philharmonic ~443, Vienna Philharmonic ~444, Boston Symphony ~444, late-19th-c. Paris Opera up to 449 — to brighten ensemble sound.",
            uses = "Used by the Vienna Philharmonic (~444), Berlin Philharmonic (~443), and Boston Symphony (~444) today. The late-19th-c. Paris Opera reached 449. The choice is structural — these orchestras tune sharper to brighten ensemble sound and project further in large halls.",
            studies = "No biological-effect studies; a working orchestral choice.",
            references = "Modern Vienna, Berlin, Boston symphony recordings; many post-1990 concert recordings drift sharper than nominal A=440.",
            usage = "When listening to modern symphony recordings from Vienna, Berlin, or Boston — these orchestras tune sharper than the nominal A=440 by design.",
            compositions = listOf(
                Composition("Mahler · Symphony No. 9", "Vienna Philharmonic"),
                Composition("Beethoven · Symphony No. 9", "Berlin Philharmonic"),
                Composition("Brahms · Symphony No. 4", "Boston Symphony Orchestra"),
            ),
        ),
        CatalogueEntry(
            hz = "C=256",
            title = "Sauveur 1701 — \"scientific pitch\"",
            group = CatalogueGroup.REFERENCE_PITCH,
            history = "Proposed by French acoustician Joseph Sauveur in 1701 as a \"philosophical\" tuning where C is a power of 2 (256 = 2⁸). Revived in the 20th century by tuning advocacy associated with the Schiller Institute. Gives A≈430.5 — close to but not identical to Verdi's A=432.",
            uses = "Proposed by Sauveur in 1701 but never adopted as a working orchestral standard. Revived in the 20th c. by Schiller Institute tuning advocacy and adjacent New-Age circles; treated by proponents as a \"natural\" or \"mathematically pure\" tuning. A small body of \"scientific-pitch\" piano recordings exists.",
            studies = "None of biological effect; the mathematical \"purity\" argument is numerological, not acoustic — every tuning system has whole-number ratios somewhere in it.",
            references = "Schiller Institute \"Verdi tuning\" piano recordings; tuning-advocacy literature. Marginal in mainstream music practice.",
            usage = "If \"scientific pitch\" tuning advocacy is part of your interest. Otherwise A=432 (on the dial) covers the same neighbourhood with a real performance lineage attached.",
        ),

        // — Cousto cosmic octave (1978): orbital periods pitch-shifted up
        // into audible range. Real arithmetic; folkloric meaning.
        CatalogueEntry(
            hz = "136.1",
            title = "Earth year — \"OM tone\"",
            group = CatalogueGroup.COUSTO,
            history = "Hans Cousto, The Cosmic Octave (1978), pitch-shifted the Earth's orbital period (one year) up by 32 octaves to arrive at 136.1 Hz. Subsequently adopted by some Indian-classical practitioners as a tanpura tuning.",
            uses = "Used widely in the sound-healing and biohacker community as the \"OM tone\" — sound baths, vibroacoustic therapy mats, tuning forks, planetary-tone meditation. Tuning-fork sets calibrated to 136.1 are sold by Biosonics, Sound Universe, and other sound-healing supply houses. Also adopted by Indian-classical practitioners as a tanpura drone — the older and more grounded path.",
            studies = "No replicated biological-effect studies. The orbital arithmetic is correct; the meaning attributed to it is interpretive.",
            references = "Steven Halpern, Aeoliah, and other planetary-tone meditation albums; Biosonics and Sound Universe planetary tuning-fork sets; Solfeggio / OM-tone YouTube channels (Meditative Mind, Healing Vibrations); tanpura recordings tuned to 136.1 used as drone underneath Indian-classical raga.",
            usage = "If sound-healing, planetary-tone meditation, or vibroacoustic therapy is part of your practice — or if you sit with Indian-classical raga and want the cosmic-Sa register.",
            compositions = listOf(
                Composition("Tanpura drone (cosmic-Sa, 136.1 Hz)", "Indian-classical raga performance"),
                Composition("Raga Yaman (evening raga)", "Sitar · tanpura · tabla"),
                Composition("Raga Bhairavi (morning raga)", "Sitar · tanpura"),
            ),
        ),
        CatalogueEntry(
            hz = "194.18",
            title = "Earth day",
            group = CatalogueGroup.COUSTO,
            history = "Cousto's pitch-shift of the 24-hour solar day, ~194.18 Hz after 24 octaves of doubling.",
            uses = "Used in Cousto-derived planetary-tone meditation, sound-bath sets, and circadian-attunement practice. Tuning forks calibrated to 194.18 are sold in planetary-set kits by sound-healing supply houses. No traditional musical lineage.",
            studies = "None.",
            references = "Cousto's own catalogue and successor planetary-tone albums; Biosonics / Sound Universe planetary tuning-fork sets; circadian-rhythm sound-healing tracks.",
            usage = "If circadian-cycle planetary-tone work is part of your practice.",
        ),
        CatalogueEntry(
            hz = "126.22",
            title = "Sun",
            group = CatalogueGroup.COUSTO,
            history = "Cousto's pitch-shift of the synodic rotation period of the Sun (~25 days at the equator).",
            uses = "Used in Cousto-derived solar-archetype sound work — sound baths timed to dawn or noon, vibroacoustic therapy sessions, planetary-tone meditation. Tuning forks in planetary-set kits from sound-healing suppliers. No traditional lineage.",
            studies = "None.",
            references = "Cousto's catalogue; planetary tuning-fork sets (Biosonics, Sound Universe); solar-archetype sound-healing tracks on YouTube.",
            usage = "If solar-archetype planetary-tone work is part of your practice.",
        ),
        CatalogueEntry(
            hz = "210.42",
            title = "Moon",
            group = CatalogueGroup.COUSTO,
            history = "Cousto's pitch-shift of the synodic lunar month (~29.5 days).",
            uses = "Used in Cousto-derived lunar sound work — moon-phase sound baths, menstrual-cycle attunement practice, dream-work meditation. Tuning forks in planetary-set kits. Lunar-tone tracks dominate the moon-phase-meditation corner of the sound-healing community.",
            studies = "None.",
            references = "Cousto's catalogue; lunar-tone meditation albums; planetary tuning-fork sets; moon-phase sound-healing tracks on YouTube.",
            usage = "If lunar-cycle work is part of your practice — moon-phase rituals, menstrual-cycle attunement, dream work.",
        ),
        CatalogueEntry(
            hz = "144.72",
            title = "Mars",
            group = CatalogueGroup.COUSTO,
            history = "Cousto's pitch-shift of the Martian sidereal year (~687 Earth days).",
            uses = "Used in Cousto-derived astrological sound work, paired in the practice with Mars themes — courage, will, deliberate action. Tuning forks sold in planetary-set kits by sound-healing supply houses. No traditional lineage outside the Cousto framework.",
            studies = "None.",
            references = "Cousto's catalogue; planetary tuning-fork sets; astrological sound-healing practitioners; Mars-tone tracks on YouTube planetary-frequency channels.",
            usage = "If astrological / planetary tuning is part of your practice — Mars themes of assertion, will, deliberate action.",
        ),
        CatalogueEntry(
            hz = "183.58",
            title = "Jupiter",
            group = CatalogueGroup.COUSTO,
            history = "Cousto's pitch-shift of the Jovian sidereal year (~12 Earth years).",
            uses = "Used in Cousto-derived astrological sound work, paired with Jupiter themes — expansion, generosity, abundance. Tuning forks in planetary-set kits. No traditional lineage outside the Cousto framework.",
            studies = "None.",
            references = "Cousto's catalogue; planetary tuning-fork sets; Jupiter-tone tracks on planetary-frequency YouTube channels.",
            usage = "If astrological / planetary tuning is part of your practice — Jupiter themes of expansion, generosity.",
        ),
        CatalogueEntry(
            hz = "147.85",
            title = "Saturn",
            group = CatalogueGroup.COUSTO,
            history = "Cousto's pitch-shift of the Saturnian sidereal year (~29.5 Earth years).",
            uses = "Used in Cousto-derived astrological sound work, paired with Saturn themes — discipline, structure, slowed time, the long view. Tuning forks in planetary-set kits. No traditional lineage outside the Cousto framework.",
            studies = "None.",
            references = "Cousto's catalogue; planetary tuning-fork sets; Saturn-tone tracks on planetary-frequency YouTube channels.",
            usage = "If astrological / planetary tuning is part of your practice — Saturn themes of discipline, structure, the long view.",
        ),

        // — Schumann harmonics. The fundamental (7.83) is on the dial.
        CatalogueEntry(
            hz = "14.3",
            title = "Schumann · 2nd harmonic",
            group = CatalogueGroup.SCHUMANN_HARMONICS,
            history = "The Earth–ionosphere cavity has a fundamental electromagnetic resonance at ~7.83 Hz (Winfried Schumann, 1952) and harmonics at ~14.3, 20.8, 27.3 and 33.8 Hz — real, continuously measurable geophysical phenomena driven by global lightning activity.",
            uses = "Used as a reference in atmospheric-physics measurement of the Earth–ionosphere cavity. In the wellness / biohacker community the Schumann series is referenced collectively under the \"Earth resonance\" frame — PEMF (pulsed electromagnetic field) therapy devices, \"Schumann generators\" sold for the bedroom, grounding / earthing apps, EEG-feedback sleep tools. Most products target the 7.83 fundamental rather than this harmonic specifically.",
            studies = "The geophysics is well-established and routinely cited in atmospheric-physics literature. Biological-effect studies (Cherry 2002, König 1971 and successors) are small, mostly unreplicated, and frequently conflated with separate ELF-magnetic-field literature.",
            references = "Atmospheric-physics literature; Schumann-generator and PEMF device manufacturers (EarthPulse, EarthCalm, Sachs Schumann); grounding / earthing community resources. No specific musical tradition uses this harmonic.",
            usage = "Sub-audible; no foreground listening role. Documented here for the geophysical series — the dial's 7.83 fundamental is what plays under the night band.",
        ),
        CatalogueEntry(
            hz = "20.8",
            title = "Schumann · 3rd harmonic",
            group = CatalogueGroup.SCHUMANN_HARMONICS,
            history = "Third harmonic of the Earth–ionosphere cavity resonance, around 20.8 Hz, at the threshold of human hearing.",
            uses = "Used in atmospheric-physics measurement as part of the Schumann series. No specific wellness-product or musical practice targets this harmonic alone — the biohacker community treats the fundamental at 7.83 as the load-bearing band.",
            studies = "Geophysics solid; no biological-effect studies for this harmonic specifically.",
            references = "Atmospheric-physics literature; Schumann-series wellness content (referencing the band collectively, not this harmonic). No targeted musical or product use.",
            usage = "Sub-audible threshold; no foreground listening role. Documented for completeness, not for listening.",
        ),
        CatalogueEntry(
            hz = "27.3",
            title = "Schumann · 4th harmonic",
            group = CatalogueGroup.SCHUMANN_HARMONICS,
            history = "Fourth harmonic of the Earth–ionosphere cavity resonance.",
            uses = "Used in atmospheric-physics measurement as part of the Schumann series. No targeted biohacker, wellness-product, or musical practice at this harmonic — the wellness-side narrative travels with the fundamental.",
            studies = "Geophysics solid; no biological-effect studies for this harmonic specifically.",
            references = "Atmospheric-physics literature; Schumann-series wellness content (collective reference). No targeted use.",
            usage = "At the very bottom of conventional instrumental range — felt more than heard. No listening role the dial's 7.83 doesn't already cover.",
        ),
        CatalogueEntry(
            hz = "33.8",
            title = "Schumann · 5th harmonic",
            group = CatalogueGroup.SCHUMANN_HARMONICS,
            history = "Fifth harmonic of the Earth–ionosphere cavity resonance.",
            uses = "Used in atmospheric-physics measurement as part of the Schumann series. No targeted use in wellness / biohacker practice or musical performance.",
            studies = "Geophysics solid; no biological-effect studies for this harmonic specifically.",
            references = "Atmospheric-physics literature; Schumann-series collective wellness content.",
            usage = "At the bass-piano floor — felt more than heard. Documented, not curated.",
        ),

        // — Brainwave bands.
        CatalogueEntry(
            hz = "Delta",
            title = "0.5 – 4 Hz · sub-audible",
            group = CatalogueGroup.BRAINWAVE,
            history = "Identified by Hans Berger and W. Grey Walter in early-20th-c. EEG work as the slow oscillation dominant in deep (stage 3/4 NREM) sleep.",
            uses = "Used in clinical sleep medicine as the EEG marker of deep sleep. In the biohacker / wellness community, used as the target band of binaural-beat and isochronic-tone audio products sold for sleep onset and deep rest — Centerpointe Holosync, Hemi-Sync (Monroe Institute), iAwake Technologies, Brain.fm \"Sleep,\" generic YouTube sleep-frequency playlists. Used in neurofeedback training as a sleep-quality marker. Distinct uses: clinicians measure delta, audio products try to drive it from speakers.",
            studies = "Polysomnography literature confirms delta = deep sleep. Whether external delta-frequency audio reliably drives a listener's EEG into delta states is contested — small studies (Wahbeh, Lane) show modest effects; larger reviews (Garcia-Argibay 2019) find weak, inconsistent results.",
            references = "Holosync (Centerpointe); Hemi-Sync (Monroe Institute); iAwake Technologies; Brain.fm \"Sleep\"; Insight Timer / Calm sleep tracks; YouTube sleep-frequency channels; neurofeedback training protocols. No traditional musical lineage.",
            usage = "Sub-audible; no foreground role. If sleep-onset binaural / isochronic tracks or neurofeedback are part of your routine, this is the band — but the dial's 7.83 sub-audible companion is the radio's honest analogue.",
        ),
        CatalogueEntry(
            hz = "Alpha",
            title = "8 – 12 Hz · audible",
            group = CatalogueGroup.BRAINWAVE,
            history = "Berger's original \"alpha rhythm\" — prominent EEG oscillation when awake with eyes closed, present in relaxed wakefulness.",
            uses = "Used in EEG measurement as the marker of relaxed wakefulness. In the biohacker community, used as the target of relaxation- and meditation-focused entrainment audio (Holosync, Hemi-Sync, Brain.fm \"Relax,\" generic Solfeggio playlists labelled \"alpha state\"), and as the target of neurofeedback / biofeedback training to teach voluntary alpha access — the original 1960s alpha-training protocols. Drone instruments (didgeridoo, tanpura, organ pedal) sometimes amplitude-modulate in this range incidentally.",
            studies = "Alpha = relaxed wakefulness is well-documented. Audio-entrainment effects on alpha are mixed; some studies show modest effects on subjective relaxation, larger reviews are skeptical of robust, replicable EEG entrainment.",
            references = "Holosync, Hemi-Sync, iAwake Technologies, Brain.fm; alpha-training neurofeedback protocols (Muse headband, Mendi, Neurosky); didgeridoo, tanpura, and organ-pedal drone music.",
            usage = "If a relaxation-entrainment track, alpha-training neurofeedback, or didgeridoo / tanpura listening practice is part of your day.",
        ),
        CatalogueEntry(
            hz = "Beta",
            title = "12 – 30 Hz · audible",
            group = CatalogueGroup.BRAINWAVE,
            history = "Berger / Walter — EEG oscillation associated with active waking cognition, problem-solving, and arousal.",
            uses = "Used in EEG research as the marker of active waking cognition. In the biohacker / productivity community, used as the target of \"focus audio\" and \"productivity audio\" products — Brain.fm \"Focus,\" Endel, focus@will, generic study-music streaming. Used in beta-up-training neurofeedback for ADHD attention work. No traditional or pre-electronic use of the band as such.",
            studies = "Beta = active cognition, well-documented. \"Focus audio\" claims are weak; commercial products (Brain.fm, Endel) cite small in-house studies, no large independent replications.",
            references = "Brain.fm; Endel; focus@will; commercial \"focus music\" streaming apps; beta-up-training neurofeedback protocols. No traditional musical lineage.",
            usage = "If focus-entrainment audio or beta-up-training neurofeedback is part of your work setup. No dial station — \"productivity\" is the engagement-loop register the manifesto refuses.",
        ),
        CatalogueEntry(
            hz = "Gamma",
            title = "30 – 100 Hz (40 typical)",
            group = CatalogueGroup.BRAINWAVE,
            history = "Higher-frequency EEG associated with cross-cortical binding, attention, and perceptual integration. The 40 Hz band is studied especially in connection with consciousness and Alzheimer's research.",
            uses = "Used in research neuroscience to study cross-cortical binding and attention. Used in the MIT Tsai-lab Alzheimer's-relevant audiovisual stimulation protocol (40 Hz, Iaccarino 2016 onward). Used in the biohacker / cognitive-enhancement community as the target of consumer \"gamma audio\" tracks — distinct from the controlled research protocol. Recorded during compassion meditation in advanced Tibetan-Buddhist practitioners (Davidson lab, Lutz et al. 2004).",
            studies = "Real neuroscience at 40 Hz includes the Tsai-lab work at MIT (Iaccarino 2016, Martorell 2019) showing 40 Hz audiovisual stimulation may reduce amyloid plaques in mice, with early-stage human trials ongoing. Generic consumer \"gamma audio\" tracks do not replicate the protocol.",
            references = "Tsai-lab research papers (Iaccarino 2016, Martorell 2019); Cognito Therapeutics' GammaSense audiovisual device (clinical-trial track); Brain.fm gamma-band variants; Davidson-lab meditation research (Lutz et al. 2004).",
            usage = "If you're following 40 Hz audiovisual stimulation from the Alzheimer's research literature, or doing advanced meditation practice connected to gamma-state research. Consumer audio products do not replicate the controlled protocol.",
        ),

        // — Other named tones in circulation.
        CatalogueEntry(
            hz = "8",
            title = "\"Earth pulse\" / \"genius frequency\"",
            group = CatalogueGroup.NAMED,
            history = "A New-Age conflation: rounds 7.83 Hz to 8, sometimes attributed to Tesla (no documented source), sometimes tied to apocryphal \"creative state\" measurements of Einstein's brainwaves. A separate lineage from the actual Schumann fundamental.",
            uses = "Used in New-Age and biohacker folklore as a \"creative-state\" or \"genius-state\" tone, often invoked alongside attributions to Tesla and Einstein. Appears in subliminal-audio products, brainwave-entrainment \"creativity\" tracks, and Tesla-coil-themed wellness lore. No documented use distinct from the actual Schumann fundamental at 7.83.",
            studies = "None supporting the claims. The actual Schumann fundamental at 7.83 has its own (small, contested) literature.",
            references = "Subliminal-audio products labelled \"genius frequency\"; Tesla-folklore-adjacent wellness content; New-Age creativity-frequency YouTube playlists. No musical tradition.",
            usage = "Sub-audible; no foreground role. The dial's 7.83 is the load-bearing version — this entry is folklore documentation.",
        ),
        CatalogueEntry(
            hz = "40",
            title = "\"Neuroscience gamma\"",
            group = CatalogueGroup.NAMED,
            history = "Real neuroscience interest at 40 Hz, especially the MIT Tsai-lab work on Alzheimer's-relevant audiovisual stimulation. Distinct from consumer \"gamma audio\" products.",
            uses = "Used in MIT Tsai-lab Alzheimer's-relevant audiovisual stimulation research (Iaccarino 2016 onward) — controlled clinical protocol of light + sound. Used in the biohacker / nootropic-stack community as 40 Hz audio for cognition, memory, and focus — Brain.fm gamma tracks, generic 40 Hz YouTube playlists, audiovisual stim devices like Cognito's GammaSense. Used in some neurofeedback protocols.",
            studies = "Tsai-lab studies (Iaccarino et al. 2016, Martorell et al. 2019) used controlled audiovisual stimulation in mice and small human cohorts; results are early-stage and replication is ongoing. Generic 40 Hz tracks sold as wellness audio do not replicate the protocol or measurement.",
            references = "Tsai-lab papers; Cognito GammaSense (clinical-trial device); Brain.fm gamma; generic 40 Hz biohacker YouTube channels.",
            usage = "If the Tsai-lab Alzheimer's research is something you're following from the literature side. Consumer audio alone is not a clinical intervention.",
        ),
        CatalogueEntry(
            hz = "1122",
            title = "Solfeggio outlier",
            group = CatalogueGroup.NAMED,
            history = "Sometimes appended to the modern 9-tone Solfeggio set as a 10th tone. Joseph Puleo's 1999 Solfeggio book — the founding modern source — listed only nine; 1122 enters in later New-Age publishing.",
            uses = "Used (rarely) in extended Solfeggio listings within the sound-healing community, framed as an \"ascension\" or higher-octave frequency above the canonical nine. Marginal even within Solfeggio sound-healing practice; the canonical nine carry the band.",
            studies = "None.",
            references = "Extended Solfeggio listings in New-Age publishing; rarely on Solfeggio sound-healing tracks (Meditative Mind, JustaTeeMusic, Healing Vibrations); ascension-frequency YouTube channels.",
            usage = "If completing the modern Solfeggio set as a curiosity. Marginal even within that tradition; nothing curated lives at this band.",
        ),

        // — Numerological / Pythagorean.
        CatalogueEntry(
            hz = "111 … 999",
            title = "\"Angel numbers\"",
            group = CatalogueGroup.NUMEROLOGY,
            history = "Repeating-digit number sequences popularised by 21st-c. New-Age numerology (Doreen Virtue's Angel Numbers, 2005, and earlier works on angel-guidance themes). Each number is assigned an \"angelic message.\"",
            uses = "Used in 21st-c. manifestation / law-of-attraction culture and synchronicity-watching numerology practice. A practitioner selects the tone matching the repeating digit they are working with — 111 for new beginnings, 222 for alignment, 333 for guidance, on through the set. Often paired with crystal grids, manifestation journaling, affirmation work, or scripting practice.",
            studies = "None — pure numerology with no acoustic basis.",
            references = "Doreen Virtue, Angel Numbers (2005); Mike Dooley, Notes from the Universe; manifestation-culture YouTube channels; numerology / synchronicity playlists. No musical tradition.",
            usage = "If numerology, manifestation, or synchronicity-watching is part of your day — a tone selected by whichever repeating digit the moment seems to call for. The radio plays the lowest of the set (111 Hz) as a worked example.",
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
