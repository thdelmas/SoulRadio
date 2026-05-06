package com.soulradio.soulradio

/**
 * Entry data for the Radio mode catalogue. Split out from [Catalogue]
 * (which keeps the data classes, enum, and helpers) to stay under the
 * 500-line cap as the catalogue grows.
 *
 * Insertion order is the display order *within* a group; the
 * [CatalogueGroup] enum's order picks which group renders first. New
 * entries should be added inside the group's section, mirroring the
 * five-field structure documented on [CatalogueEntry].
 */
internal val catalogueEntries: List<CatalogueEntry> = listOf(
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
        hz = "Theta",
        title = "4 – 8 Hz · sub-audible threshold",
        group = CatalogueGroup.BRAINWAVE,
        history = "Berger / Walter — EEG oscillation between Delta and Alpha, dominant in REM sleep, hypnagogic transition, and deep meditation in long-term practitioners. The Schumann fundamental at 7.83 Hz sits at the top edge of the band.",
        uses = "Used in clinical EEG as the marker of REM and hypnagogic states. In the biohacker / wellness community, used as the target of meditation-focused entrainment audio — Holosync, the Hemi-Sync \"Gateway\" series, Brain.fm \"Meditation,\" iAwake meditation tracks. The one band with a real pre-electronic tradition behind it: shamanic drumming traditions (Siberian, Native American, Mongolian) commonly hold a steady 240–270 BPM cadence — a pulse rate of 4 – 4.5 Hz, squarely Theta. Used in trauma-adjacent neurofeedback (Sebern Fisher's protocols, EMDR-adjacent practice) and in the contemporary drumming-circle / shamanic-journey practice descended from Michael Harner's Foundation for Shamanic Studies.",
        studies = "Theta = REM and deep meditation, well-documented. Audio-entrainment effects on theta are mixed — the same pattern as other bands. The shamanic-drumming literature is small but interesting: Harner-tradition studies (Maxfield 1990, Wright 1991) report modest effects on subjective journey-state and altered-state measures; EEG evidence for actual theta-driving from the drum is weak.",
        references = "Holosync (Centerpointe); Hemi-Sync \"Gateway Experience\" (Monroe Institute); Brain.fm \"Meditation\"; iAwake Technologies; Foundation for Shamanic Studies (Michael Harner) drumming tapes; Sandra Ingerman / Sounds True guided-journey recordings; Mickey Hart's Planet Drum and other percussion-tradition crossovers; trauma-adjacent neurofeedback protocols.",
        usage = "If meditation-entrainment audio, shamanic-journey drumming practice, or theta-down trauma neurofeedback is part of your work. The dial's 7.83 sub-audible companion sits at the top of this band — sitting under music rather than as foreground is the radio's honest in-band integration.",
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

    // — Brainwave entrainment delivery. The Hz of a brainwave band
    // is one question; the *mechanism* used to deliver it is another.
    // All three are speaker-or-headphone audio with no music tradition
    // behind them — refused on the Loop and the foreground Dial,
    // documented here as a labeled exhibit of the practice.
    CatalogueEntry(
        hz = "Binaural",
        title = "Two pitches, one per ear",
        group = CatalogueGroup.ENTRAINMENT_DELIVERY,
        history = "Discovered by Heinrich Wilhelm Dove in 1839 — when two close-frequency tones are played one to each ear, the brain perceives a third \"beat\" frequency equal to the difference. Popularised in the 1970s by Robert Monroe (Monroe Institute) as the basis of the Hemi-Sync product line.",
        uses = "Used as the dominant delivery mechanism of brainwave-entrainment audio from the 1970s onward — Hemi-Sync, Holosync (Centerpointe), iAwake Technologies, Brain.fm's binaural mode, and an enormous YouTube ecosystem labelled by target band (Delta sleep, Theta meditation, Alpha relaxation, Gamma focus). Headphones are required by the mechanism — speakers blur the L/R separation. Often used inside subliminal-affirmation tracks and \"manifestation\" audio.",
        studies = "The perceptual phenomenon is real — listeners do hear the beat frequency. Whether the perceived beat reliably drives the listener's EEG into the target band is contested: Garcia-Argibay et al. (2019) found weak, inconsistent evidence in a meta-review; Wahbeh and Lane reported modest cognitive effects in small studies. Subjective relaxation effects are more consistently reported than EEG changes.",
        references = "Robert Monroe, *Journeys Out of the Body* (1971); Hemi-Sync (Monroe Institute); Holosync (Centerpointe); iAwake Technologies; Brain.fm; YouTube binaural-beats playlists at every target band.",
        usage = "If binaural-entrainment audio is part of your routine. The mechanism requires headphones; the radio plays through speakers and so cannot honestly demonstrate it on the dial. Documented here so the catalogue covers the practice without prescribing it.",
    ),
    CatalogueEntry(
        hz = "Isochronic",
        title = "Pulsed single tone",
        group = CatalogueGroup.ENTRAINMENT_DELIVERY,
        history = "Single tone amplitude-modulated at the target band frequency — e.g. a 200 Hz carrier pulsed on/off ten times a second to target Alpha. Came into widespread use in the 1990s entrainment-software wave (BrainWave Generator, NeuroProgrammer) as a speaker-friendly alternative to binaural beats.",
        uses = "Used in entrainment audio products that don't require headphones — Mind-Sync, BrainWave Generator, NeuroProgrammer, iAwake's isochronic tracks, and many YouTube isochronic playlists. Often presented alongside binaural variants of the same band as a \"speakers OK\" option.",
        studies = "Direct comparisons of isochronic vs binaural entrainment are few. The pulse is more salient — a listener notices it more than a binaural beat — which makes blinded studies harder. Effects on subjective state are modest where reported; EEG-driving evidence is similar in strength to the binaural literature, which is to say weak and inconsistent.",
        references = "BrainWave Generator (Noromaa Solutions); NeuroProgrammer; iAwake Technologies isochronic tracks; YouTube isochronic-entrainment playlists.",
        usage = "If isochronic-entrainment audio is part of your routine — particularly when headphones aren't available. The mechanism is what makes the track \"work\" the way it claims to; the radio documents the practice without endorsing the claim.",
    ),
    CatalogueEntry(
        hz = "Monaural",
        title = "Two pitches mixed before output",
        group = CatalogueGroup.ENTRAINMENT_DELIVERY,
        history = "Two close-frequency tones are summed in a single channel before output, producing acoustic (rather than perceptual) beats at the difference frequency. The third major entrainment-delivery mechanism, distinguished from binaural (which exists only in the brain) and isochronic (which is a single pulsed tone).",
        uses = "Used in entrainment audio products positioned between binaural (needs headphones) and isochronic (more distracting on/off pulse). Less common in the consumer market than the other two; appears in some Brain.fm tracks, in audio-engineering test signals, and in academic entrainment-research designs.",
        studies = "Effect literature is sparse — most entrainment research compares binaural to a control or to isochronic, not to monaural. Mechanistically, monaural beats present an acoustic beat at the target frequency, auditorily simpler than a binaural beat and easier on speaker systems.",
        references = "Brain.fm (some tracks); academic entrainment research; audio-engineering reference signals; specialist entrainment-software environments.",
        usage = "If a monaural-beat track is part of your routine, particularly through speakers where binaural beats can't work. As with the other entrainment mechanisms, documented here without the claim of effect.",
    ),

    // — Noise colors. Spectral shapes, not single Hz values. Refused
    // on the dial as audio rather than music; documented here so the
    // listener can place pink-noise sleep apps and brown-noise ADHD
    // focus tracks inside the wider catalogue.
    CatalogueEntry(
        hz = "White",
        title = "Flat-spectrum noise",
        group = CatalogueGroup.NOISE_COLOR,
        history = "Acoustic concept for noise with equal energy across all frequencies — the auditory equivalent of white light. Used in audio engineering and signal processing since the early-20th-c. development of electronic noise generators.",
        uses = "Used in hospital nurseries and NICUs as an in utero womb-sound analogue, in office and open-plan acoustic-masking systems, in sleep machines (Marpac Dohm, LectroFan), and in tinnitus-masking therapy. The dominant \"sleep noise\" of consumer apps before pink and brown noise overtook it in the 2020s. Streaming platforms host enormous white-noise playlists — single Spotify tracks have surpassed a billion plays.",
        studies = "Acoustic masking mechanism is well-understood — broadband noise raises the auditory threshold, so transient sounds (a door, a passing car) are less likely to wake a listener. Direct sleep-onset effect is modest in healthy adults; stronger in NICU contexts. Tinnitus-masking literature (Henry et al. and successors) is established.",
        references = "Marpac Dohm; LectroFan; Calm and Headspace sleep tracks; Spotify and YouTube white-noise playlists; Resound and Widex tinnitus-masker hearing aids; office acoustic-masking systems (Cambridge Sound Management).",
        usage = "If acoustic masking is the goal — sleeping next to traffic, sharing a wall, masking tinnitus, calming an infant. Masking is not music; Radio is the right surface for that distinction.",
    ),
    CatalogueEntry(
        hz = "Pink",
        title = "−3 dB / octave · steady rain",
        group = CatalogueGroup.NOISE_COLOR,
        history = "1/f spectral profile — power decreases by 3 dB per octave, so each octave carries equal power. Pervasive in natural systems: rainfall, ocean surf, electronic component noise, neural firing patterns. The ear's frequency-response tilt makes pink noise sound \"flat\" the way white noise sounds bright.",
        uses = "Used in sleep apps and sleep machines as a softer alternative to white noise. Used in audio engineering as a reference signal for room measurement and equalisation. The Northwestern (Phyllis Zee lab) sleep-research work on slow-wave-sleep memory consolidation pushed pink noise into the wellness-app mainstream.",
        studies = "Papalambros et al. (2017) and Schade et al. (2020) at Northwestern showed that closed-loop pink-noise stimulation, time-locked to slow waves during NREM sleep, modestly improved memory consolidation in older adults. The general consumer claim — that any pink-noise track improves sleep — is not what the studies tested.",
        references = "Calm; Headspace; Endel; Brain.fm pink-noise tracks; YouTube pink-noise playlists; SoundOasis sleep machines; Dreem and Philips SmartSleep headbands target the closed-loop protocol from the Northwestern work.",
        usage = "If sleep-onset noise, slow-wave-sleep memory work, or audio-engineering room calibration is part of your day. The closed-loop research protocol is a different product than a passive pink-noise track.",
    ),
    CatalogueEntry(
        hz = "Brown",
        title = "−6 dB / octave · deep roar",
        group = CatalogueGroup.NOISE_COLOR,
        history = "Also called red noise. 1/f² spectral profile — power decreases by 6 dB per octave, so the lowest frequencies dominate. Sounds like distant thunder or heavy surf. Named for Robert Brown, whose Brownian-motion process generates this spectrum.",
        uses = "Used widely as a focus / concentration aid, especially in the ADHD self-treatment community — a 2023 wave of TikTok and Reddit discussion drove it into the mainstream. Used in sleep tracks for listeners who find white and pink noise too bright. Used in audio engineering as a low-frequency test signal.",
        studies = "Formal literature is thin; most evidence is community-reported. Broadband low-frequency noise as an attention aid in ADHD subjects is consistent with the moderate-stimulation hypothesis (Sikström, Söderlund), which suggests under-aroused brains benefit from sensory load. Replication is early; brown noise specifically is rarely the tested signal.",
        references = "Endel; Brain.fm \"Focus\"; YouTube brown-noise channels (some at hundreds of millions of views); Reddit r/ADHD and ADHD-TikTok communities; sleep apps featuring brown alongside pink and white.",
        usage = "If brown noise is a working focus tool for you — particularly in the ADHD context where the community-reported effect is strongest. The radio is honest about the literature: real for some listeners, not yet well-replicated.",
    ),

    // — Vibroacoustic and sub-audible body-stimulation. The body
    // channel rather than the ear channel — felt low-frequency tones
    // used in vibroacoustic-therapy and vagal-tone practice. The dial's
    // 7.83 lives in this territory under music as a geophysical
    // companion; entries here cover the rest of the band as exhibits.
    CatalogueEntry(
        hz = "20–60",
        title = "Vagal-tone drone range",
        group = CatalogueGroup.VIBROACOUSTIC,
        history = "The lower-audible / sub-audible band where chest, throat, and auricular-branch vagus-nerve resonance is hypothesised to amplify a tone's effect on the parasympathetic nervous system. Vibroacoustic therapy (VAT) — pioneered in 1980s Norway by Olav Skille — gave the band its clinical framing; Stephen Porges' Polyvagal Theory (1990s onward) supplied the current vagal-tone language.",
        uses = "Used in vibroacoustic-therapy mats and chairs (Inner Sound, MultiVib, Sound Oasis VTS) — bone-conducted low-frequency stimulation as a clinical and consumer product. Used in PEMF / vibroacoustic combination devices. Reproduced acoustically by Tibetan overtone chant, Mongolian throat singing, didgeridoo drone, organ pedal, and bowed-string drone — these traditions hit the band as a side-effect of musical work, which is the difference between the radio's Loop / Dial admission of the *singing* and refusal of the *bare drone*.",
        studies = "Vibroacoustic-therapy literature (Skille, Wigram, Punkanen) is medium-sized and reports modest effects on muscle tension, anxiety, and chronic pain — most studies are small and unblinded. Polyvagal-Theory framings (Porges) are widely cited in trauma practice but contested in academic neuroscience.",
        references = "Olav Skille's VibroAcoustic Therapy; Tony Wigram's research catalogue; SOMA Sound Tools; Inner Sound, MultiVib, Sound Oasis VTS vibroacoustic devices; PEMF + vibration combination units (BioBalance, NES Health). Throat-singing and overtone-chant traditions reach the band as living music.",
        usage = "If vibroacoustic-therapy practice or chest / throat resonance work is part of your day. The bare-drone version is what a sine demo here gestures at; the same band carried by Tibetan or Mongolian singing voices already lives on the dial under 7.83.",
    ),
    CatalogueEntry(
        hz = "128",
        title = "\"NO release\" / vasodilation tone",
        group = CatalogueGroup.VIBROACOUSTIC,
        history = "Promoted in vibroacoustic-therapy and biohacker literature as a frequency that releases nitric oxide (NO) in the vascular endothelium, producing vasodilation. The 128 Hz value (C₃ in Western tuning) appears in several vibroacoustic protocols and weighted-fork sets in circulation.",
        uses = "Used in vibroacoustic-therapy and sound-healing tuning-fork practice — a 128 Hz weighted fork applied to the body during a session, often pitched as a circulatory or pain-management tool. Used by John Beaulieu (BioSonics) and the broader sound-healing tuning-fork community as a foundation tone of the standard fork set.",
        studies = "Lohse et al. (1998) and a small successor literature reported NO release from low-frequency vibration of vascular tissue — the mechanism is not specific to 128 Hz. The leap from \"low-frequency vibration releases NO in vitro\" to \"a 128 Hz fork on the body lowers blood pressure clinically\" is not supported by replicated trials.",
        references = "John Beaulieu, BioSonics tuning-fork sets; Acutonics; vibroacoustic-therapy protocols using weighted 128 Hz forks; YouTube channels and apps featuring 128 Hz tone tracks for circulation and pain.",
        usage = "If sound-healing tuning-fork practice is part of your routine. The radio plays a 128 Hz sine as a worked example of what's in circulation; the clinical-effect claims are not what the radio endorses.",
    ),
    CatalogueEntry(
        hz = "Infrasound",
        title = "Below 20 Hz · felt, not heard",
        group = CatalogueGroup.VIBROACOUSTIC,
        history = "The frequency band below the threshold of human hearing. Naturally produced by ocean surf, weather systems, large structures, and the Earth–ionosphere cavity. Studied since the early 20th century; clinically delivered through tactile transducers rather than speakers.",
        uses = "Used in vibroacoustic-therapy chairs and beds where bone-conducted low-frequency vibration is the active channel. Used in cinema and theme-park audio through tactile-transducer products (Buttkicker, Clark Synthesis). Studied in defence and acoustics research for effects on subjective fear, nausea, and disorientation. The dial's 7.83 sub-audible companion sits here, framed as a geophysical resonance rather than a vibroacoustic claim.",
        studies = "Vibroacoustic delivery of infrasound has a small clinical literature (Wigram and successors) on muscle relaxation and pain. The defence / acoustic-disturbance literature on infrasound is real; wellness-product framings usually leave it aside.",
        references = "Tactile-transducer hardware (Buttkicker, Clark Synthesis); vibroacoustic-therapy chairs and mats; cinema / haptic-audio practice; the dial's 7.83 (geophysical, not vibroacoustic claim).",
        usage = "Sub-audible; no foreground listening role. Documented as the body-channel band the dial's 7.83 already integrates honestly under music — the radio is a speaker, not a tactile transducer.",
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

    // — Ancient music traditions. Reconstructed pieces with documented
    // archaeology — older than the Solfeggio set, older than Greek
    // theory. Not Hz values; specific compositions with a real
    // performance lineage. The Hurrian Hymn h.6 is the deepest
    // documented anchor; see tunables.md § Ancient music traditions
    // for the gate.
    CatalogueEntry(
        hz = "h.6",
        title = "Hurrian Hymn to Nikkal — c. 1400 BCE",
        group = CatalogueGroup.ANCIENT,
        history = "The earliest substantially complete notated composition known. Recovered from clay tablets at the Royal Palace of Ugarit (modern Ras Shamra, Syria) excavated in the 1950s; tablet h.6 — a hymn to the Hurrian goddess Nikkal, consort of the moon god — is preserved well enough to permit reconstruction. The cuneiform notation prescribes intervals on a heptatonic diatonic scale played on a nine-string lyre, documenting the diatonic system roughly a thousand years before Pythagoras. The instrumental lineage runs further back still: the Sumerian Royal Cemetery of Ur (c. 2500 BCE) contained the bull-headed Silver Lyre and Golden Lyre — the instruments Hurrian musicians inherited — though no Sumerian compositions survive.",
        uses = "Performed in modern reconstruction by lyre players and academic ensembles — Michael Levy (solo lyre), the Lyre 2.0 Project, Ensemble De Organographia, Peter Pringle. Used in archaeology and music-history teaching as evidence that the diatonic scale predates Greek theory by a millennium. Studied in Mesopotamian and ancient-Near-Eastern musicology since Anne Draffkorn Kilmer's first decoding in the 1970s. Sat with by some contemporary lyre and harp practitioners as a contemplative practice — listening to a piece older than every other piece in the canon.",
        studies = "No biological-effect literature — this is musicology and archaeology, not wellness research. The interest is historical: the cuneiform documents diatonic scale-tuning rules and interval names, which reframes the conventional Western narrative locating the origin of harmonic theory in 6th-c. BCE Greece. Decipherment of the rhythmic and modal layer is contested — Kilmer, Duchesne-Guillemin, Dumbrill, and M. L. West offer different readings, while agreeing on the diatonic skeleton.",
        references = "Anne Draffkorn Kilmer's foundational publications (1974, 1976) on the Ugarit tablets; Richard Dumbrill, *The Archaeomusicology of the Ancient Near East* (2005); M. L. West's reconstructions; Michael Levy's reconstruction recordings (Bandcamp, Spotify); the Lyre 2.0 Project; Peter Pringle's reconstructions; Ensemble De Organographia, *Music of the Ancient Sumerians, Egyptians, & Greeks* (1997); the British Museum's Lyre of Ur reconstruction project.",
        usage = "If the deep lineage of notated music — heptatonic diatonic scale, lyre timbre, ritual hymnody older than Pythagoras — is part of your interest. The radio documents the tradition; specific reconstructions can be filed in the listener's library under whichever band the performance sits in (174 foundation for slow lyre reconstructions, 7.83 for the night-time devotional register, 396 morning gate for a Nikkal hymn at dawn).",
        compositions = listOf(
            Composition("Hurrian Hymn h.6 — lyre reconstruction", "Michael Levy"),
            Composition("Music of the Ancient Sumerians, Egyptians, & Greeks", "Ensemble De Organographia"),
            Composition("Hurrian Hymn — multi-version reconstructions", "Lyre 2.0 Project"),
            Composition("Hurrian Hymn (Kilmer / West reconstruction)", "Peter Pringle"),
        ),
    ),
)
