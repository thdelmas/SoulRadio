# SoulRadio — A Pedagogical Map of Frequencies

A field guide to the nine dial tones, the two ambient companions, and the music that has carried each intention for centuries.

> **House rule.** We do not claim these frequencies cure, heal, or repair anything. We describe what people have *used* them for — folkloric intention, contemplative tradition, the shape of attention they invite. The body decides whether it agrees. (See [MANIFESTO.md](MANIFESTO.md) §5.)

---

## How to read this map

Each entry has four fields:

- **Hz** — the tone, as a single sine value.
- **Folk name** — the label the modern Solfeggio tradition gives it.
- **Intention** — the state the tradition asks you to bring to the listening. Not a prescription. A door.
- **Historical music** — pre-electronic recordings whose *modal character, tempo, and acoustic geometry* match the spirit of the tone. These pieces are not "tuned to" the Hz — they are siblings in feeling. (See [§ A note on tuning](#a-note-on-tuning) at the end.)

The 24-hour auto loop and the 9-tap dial are **two different products** with different curation rules — see [§ Two modes](#two-modes) below.

Several bands now bundle more than one historical recording. When a band is tuned (manually or by the auto loop) the radio picks one of its recordings for that session and loops it — see [CREDITS.md](CREDITS.md) for the full list and licenses.

---

## Two modes

**Auto loop** — the 24-hour clock-driven schedule (`Frequencies.forHour()`). The listener hasn't asked for anything; the radio is wallpaper. The schedule has to follow the day's arc, never jolt, and never ask for energy the listener didn't volunteer. Uses **eight** of the eleven stations.

**Proactive (dial tap)** — the 9-tap dial. The listener reached for a station; they have invited the register. Looser rules. Uses **all nine dial stations** plus the two companions if surfaced.

Three stations are **tap-only** — they exist on the dial but never play in the auto loop:

- **852 Hz** (the high window) — too vertical to surprise someone with at 4 p.m.
- **963 Hz** (the crown) — arrival is something the listener has to ask for.
- **432 Hz** (Verdi's A, companion) — the acoustic-era operatic voice is narrative; it asks to be sat with, and the auto loop cannot sit you down.

This is not an arbitrary cut-off. It is the rule that lets the auto loop stay wallpaper. See [docs/tunables.md § Two modes, two curation rules](docs/tunables.md#two-modes-two-curation-rules) for the longer rationale.

---

## Quick map — the auto-loop schedule

| Hour band     | Tone     | Intention                      |
|---------------|----------|--------------------------------|
| 06:00–09:00   | 396 Hz   | Releasing the night            |
| 09:00–12:00   | 741 Hz   | Clearing the channel           |
| 12:00–16:00   | 528 Hz   | Warmth, gathering, midday      |
| 16:00–18:00   | 639 Hz   | Connection, table, conversation|
| 18:00–20:00   | 417 Hz   | Setting down the day           |
| 20:00–22:00   | 285 Hz   | Slowing the body               |
| 22:00–23:00   | 174 Hz   | Anchoring, low and warm        |
| 23:00–06:00   | 7.83 Hz  | Held by the stone of the abbey |

This is the source of truth for `Frequencies.forHour()` in [Frequency.kt](app/src/main/java/com/soulradio/soulradio/Frequency.kt). Any change here must also change the code and the `expectedSchedule` map in [FrequenciesTest.kt](app/src/test/java/com/soulradio/soulradio/FrequenciesTest.kt).

---

## The nine dial tones

### 174 Hz — *the foundation*

- **Folk name:** "Pain relief / sense of security."
- **Intention:** Settling. The lowest tone in the Solfeggio set. Used as a ground note — the floor a room is built on.
- **Historical music:**
  - **Gregorian chant in low Dorian or Mixolydian mode** — the *bourdon* (drone) of monastic plainchant. Recordings from the *Solesmes* abbey tradition (Dom Joseph Pothier, late 19th c.) preserve this register.
  - **Hildegard von Bingen — *O virtus Sapientiae* (12th c.)** when sung in lower transposition.
  - **Russian Orthodox liturgy — the *oktavist* basses** of the All-Night Vigil tradition (e.g. Rachmaninoff's *Vespers*, 1915). The lowest male voice in any choral tradition on earth.
  - **Bach — *Cello Suite No. 1 in G major*, Prelude (BWV 1007)** — the open low G the cello speaks from. The piece returns to that note like a tide.
  - **Chopin — *Berceuse*, Op. 57 (1843–44)** — a lullaby built over an unbroken left-hand ostinato in D♭ major; the right hand decorates above without ever leaving the floor. Late-Romantic piano grounded the way the band asks. (Currently bundled — see [CREDITS.md §174](CREDITS.md).)
  - **Chris Zabriskie — *Cylinder Eight* (from *Cylinders*, 2014)** — the lowest, most drone-anchored entry in Zabriskie's nine-piece set; slowly evolving processed piano with no melodic arc. The contemporary CC-released voice on the foundation band, sitting beside the Bach cello prelude. (Currently bundled — see [CREDITS.md §174](CREDITS.md).)

### 285 Hz — *the slow turn*

- **Folk name:** "Tissue and field."
- **Intention:** A frequency for letting the body do what it does without supervision. The hour after a long walk.
- **Historical music:**
  - **Thomas Tallis — *Spem in Alium* (c. 1570)** — forty independent voices weaving. The piece breathes at the speed of a sleeping body.
  - **William Byrd — *Mass for Four Voices*, Agnus Dei (c. 1592)** — a slow internal pulse.
  - **Bach — *Air on the G String* (BWV 1068, mvt. II)** — the most-cited "calm" piece in the Western repertoire, and earned.
  - **Pérotin — *Viderunt omnes* (c. 1198)** — the earliest four-voice polyphony in Europe. Long-held tones over a chant *cantus firmus*.
  - **Satie — *Gymnopédie No. 1* (1888)** — *Lent et douloureux*. The textbook "slow turn" of late-19th-century French piano writing — three bars of left-hand pulse, a melody that refuses to climb. (Currently bundled — see [CREDITS.md §285](CREDITS.md).)
  - **Brahms — Intermezzo in E♭, Op. 117 No. 1 (1892)** — the first of Brahms's late piano *intermezzi*, headed in the score with a Scottish lullaby epigraph; Brahms called the Op. 117 set "the lullabies of my sorrows." Late-Romantic contemplation with no climactic gesture. (Currently bundled — see [CREDITS.md §285](CREDITS.md).)
  - **Clara Schumann — *Liebst du um Schönheit*, Op. 12 No. 4 (1841)** — Clara Wieck-Schumann's setting of Rückert's *"If you love for beauty"*; a two-minute lyric arc, a small song that doesn't climb. Brings the most-revered female composer of the 19th-century Romantic canon onto the radio, sitting beside the Brahms intermezzo on the same band. (Currently bundled — see [CREDITS.md §285](CREDITS.md).)

### 396 Hz — *the morning gate*

- **Folk name:** "Liberating guilt and fear."
- **Intention:** A clearing tone. Traditionally used at the start of practice to set down what was carried in. Our 7 a.m. tone.
- **Historical music:**
  - **Gregorian *Lauds*** — the dawn office of the Divine Office, sung at first light in monasteries since the 6th century. *Aeterne rerum conditor* (Ambrose, 4th c.) is the classical Lauds hymn.
  - **Bach — *Wachet auf, ruft uns die Stimme* (BWV 140)** — literally *"Awake, the voice calls us"*. A morning cantata.
  - **Mozart — *Ave verum corpus* (K. 618, 1791)** — 46 bars, one of the most economical pieces of consolation ever written.
  - **Mozart — *Laudate Dominum* from *Vesperae de Dominica* (K. 321, 1779)** — the psalm of universal praise; we use the soft, soprano-led canticle as a clearing tone for the morning gate. (Currently bundled — see [CREDITS.md §396](CREDITS.md).)
  - **Josquin des Prez — *Missa Ave maris stella*** — Renaissance Marian devotion, used at the start of the canonical day.
  - **Raga Bhairavi — Hindustani classical morning raga (sitar)** — sung at dawn in temples to close the morning cycle, *bhairav thaat*. The non-Western voice for the morning gate, sitting beside the Bach cantata and Mozart settings. (Currently bundled — see [CREDITS.md §396](CREDITS.md).)

### 417 Hz — *the dissolver*

- **Folk name:** "Undoing situations / change."
- **Intention:** A tone for transition — leaving work, leaving an argument, leaving a room you were stuck in. Used in our early-evening band.
- **Historical music:**
  - **Gregory Allegri — *Miserere mei, Deus* (c. 1638)** — sung in the Sistine Chapel during Holy Week. A piece literally written for the act of letting go.
  - **Bach — *Erbarme dich* from *St. Matthew Passion* (BWV 244, 1727)** — the alto aria. A piece about being unable to undo what is done, sung anyway.
  - **Pérotin — *Sederunt principes* (c. 1199)** — extended drone polyphony; the listener's sense of clock dissolves.
  - **Tomás Luis de Victoria — *O magnum mysterium* (1572)** — Spanish Renaissance, written to make the air in a stone room feel different.
  - **Tomás Luis de Victoria — *O vos omnes* (Tenebrae responsory, 1585)** — *"O all you who pass by, behold and see if there be any sorrow like unto my sorrow."* The Spanish Renaissance text written for the act of standing still inside an unfinishable feeling. (Currently bundled — see [CREDITS.md §417](CREDITS.md).)
  - **Cipriano de Rore — *Missa Praeter Rerum Seriem*, Agnus Dei (1557)** — eight minutes of Renaissance Agnus Dei from Willaert's successor at San Marco; the dissolving-into-mercy text written in a different compositional voice from the Allegri above. (Currently bundled — see [CREDITS.md §417](CREDITS.md).)
  - **Fauré — *Cantique de Jean Racine*, Op. 11 (1864–65)** — Fauré at nineteen, setting Racine's translation of the Ambrosian hymn *Consors paterni luminis*. Late-Romantic French choral writing in the same dissolving-into-mercy register as the Allegri and Rore above. (Currently bundled — see [CREDITS.md §417](CREDITS.md).)
  - **Scott Buckley — *Penumbra*** — contemporary "tender, glacial meditation" by the Australian composer; string orchestra + atmospheric synthesizer + field recording, no climactic gesture. The same letting-go intention in a present-day cinematic voice. (Currently bundled — see [CREDITS.md §417](CREDITS.md).)

### 528 Hz — *the centre*

- **Folk name:** "The Love frequency / Miracle tone."
- **Intention:** Warmth at the centre of the day. Where the loop spends its longest stretch. The frequency the popular tradition treats as the heart of the Solfeggio set.
- **Historical music:**
  - **Bach — *Jesu, Joy of Man's Desiring* (from BWV 147)** — the canonical "warm" Bach, and not by accident: the chorale tune sits in C major at a tempo the heart already knows.
  - **Bach — Well-Tempered Clavier Book I, Prelude No. 1 in C major (BWV 846, 1722)** — the most-recognised opening of the 48 in the WTC; Gounod overlaid the *Ave Maria* on top of its arpeggios in 1853. The textbook centre-of-the-day Bach in solo-keyboard form, from the same Kimiko Ishizaka / Open Bach lineage as the Goldberg Aria on 741. (Currently bundled — see [CREDITS.md §528](CREDITS.md).)
  - **Pachelbel — *Canon in D* (c. 1680)** — the most-played classical piece of the 20th century for a reason. Eight bars repeating.
  - **Vivaldi — *The Four Seasons*, "Spring", mvt. I (1725)** — bright E major; the piece every nervous system already recognises.
  - **Mozart — *Clarinet Concerto in A major*, K. 622, Adagio (1791)** — a single woodwind, no edges.
  - **Hildegard von Bingen — *Caritas abundat in omnia* (12th c.)** — the literal "love" antiphon of the medieval canon.
  - **Schumann — *Träumerei* (Kinderszenen, Op. 15 No. 7, 1838)** — *"Dreaming."* The most-played miniature in the Romantic piano canon; the chord progression is a quiet ground and the band asks for nothing more. (Currently bundled — see [CREDITS.md §528](CREDITS.md).)
  - **Chopin — Nocturne in E♭, Op. 9 No. 2 (1830–32)** — the most-recognised nocturne in the Western piano repertoire. Sits at the centre of the day where it belongs. (Currently bundled — see [CREDITS.md §528](CREDITS.md).)
  - **Scott Buckley — *Amberlight*** — contemporary "soft, nostalgic piano and orchestral track." A present-day cinematic voice for the heart of the day, in the same composer lineage as the Penumbra (417) and Meanwhile (852) entries. (Currently bundled — see [CREDITS.md §528](CREDITS.md).)

### 639 Hz — *the table*

- **Folk name:** "Connecting / harmonious relationships."
- **Intention:** The frequency for shared rooms — meals, conversation, reunion. Used in our late-afternoon band.
- **Historical music:**
  - **Renaissance polyphonic Mass settings** — Josquin, Palestrina, Lassus. Music *built on* the principle of independent voices that agree.
  - **Palestrina — *Sicut cervus* (c. 1584)** — four voices imitating each other at a walking pace.
  - **Monteverdi — *Vespro della Beata Vergine* (1610)** — early Baroque, ensemble at full warmth.
  - **Bach — *Brandenburg Concerto No. 3 in G* (BWV 1048)** — three violins, three violas, three cellos: an architecture of conversation.
  - **English consort music** — John Dowland's *Lachrimae* (1604) — viol consort, written for shared rooms.
  - **Lautenbacher / Simard — Native American cedar flute composition (2025)** — contemporary chamber piece: cedar flute lead over guitar, percussion, bass, and synthesizer accompaniment. Brings a non-European voice into the band and shows that the radio's "artistic music a curator chose" rule includes modern composition, not just the canonical sacred repertoire. (Currently bundled — see [CREDITS.md §639](CREDITS.md).)
  - **Dehlavi & Payvar — Concertino for Santur** — Iranian classical concertino built around the *santur* (struck-string trapezoidal zither) as solo voice in conversation with the ensemble; performed by master santur player Manoochehr Sadeghi. Brings a Persian classical voice to the table band — fourteen minutes of "instruments in agreement" architecture from a tradition the radio had not yet heard from. (Currently bundled — see [CREDITS.md §639](CREDITS.md).)

### 741 Hz — *the clearing*

- **Folk name:** "Awakening intuition / detox."
- **Intention:** A late-morning tone, used when the head needs to rinse. Bright but not sharp.
- **Historical music:**
  - **Bach — *Goldberg Variations*, Aria (BWV 988, 1741)** — a piece written, by tradition, to pass a sleepless night clearly.
  - **Mozart — *Piano Sonata No. 16 in C major*, K. 545 ("Sonata facile", 1788)** — articulate, transparent.
  - **Domenico Scarlatti — *Sonata in D minor*, K. 9** — keyboard works as small acts of mental clarity.
  - **Couperin — *Les Barricades Mystérieuses* (1717)** — French harpsichord; a piece that resolves slow puzzles in the listener.
  - **Buxtehude — organ preludes** — North German Baroque, the ancestor of Bach's clarity.
  - **Cécile Chaminade — Flute Concertino in D, Op. 107 (1902)** — Chaminade (1857–1944) was the first female composer awarded the Légion d'honneur; the Concertino is her best-known work, written as a Paris Conservatoire flute examination piece. Flute and orchestra in the bright, articulate, transparent register the band asks for, beside the Couperin harpsichord and the Mozart K.545 — and the band's first female-composed voice. (Currently bundled — see [CREDITS.md §741](CREDITS.md).)

### 852 Hz — *the high window*

- **Folk name:** "Returning to spiritual order."
- **Intention:** A tone for stepping back from the immediate. The dial's penultimate option — chosen, not auto-played.
- **Historical music:**
  - **Hildegard von Bingen — *O ignis Spiritus Paracliti* (12th c.)** — soaring single-voice chant, written by a mystic who described her music as light.
  - **Tallis — *If ye love me* (c. 1565)** — short English motet, devastating in its restraint.
  - **Arvo Pärt is electronic-era** — but his medieval forebears, the **Notre-Dame school** (Léonin, Pérotin), wrote the same vertical air.
  - **Beethoven — *Moonlight Sonata*, Adagio sostenuto (1801)** — the "stepping back" piece of the standard repertoire.
  - **Beethoven — *Hammerklavier Sonata*, Op. 106, Adagio sostenuto (1818)** — seventeen minutes of late-style suspended Beethoven; the longest slow movement in the standard repertoire and a piece that asks the listener to step back further still. (Currently bundled — see [CREDITS.md §852](CREDITS.md).)
  - **Debussy — *Clair de Lune* (1890, pre-electronic by decades)** — French Impressionism; a piece that sounds like a high window.
  - **Satie — *Gnossienne No. 1* (1890)** — Satie's modal piano writing — no time signature in the original score, the bar-lines suggested rather than ruled. The textbook "high window" piece of late-19th-century French music: present, suspended, refusing to resolve. (Currently bundled — see [CREDITS.md §852](CREDITS.md).)
  - **Mendelssohn — *Venetianisches Gondellied*, Op. 30 No. 6 (1830s)** — the "Venetian Gondola Song" from the second book of *Lieder ohne Worte*. An F♯-minor barcarolle whose left-hand rocking figure carries the right-hand melody the way water carries a boat. (Currently bundled — see [CREDITS.md §852](CREDITS.md).)
  - **Ravel — *Pavane pour une infante défunte* (1899)** — the "Pavane for a dead princess." Five minutes of late-Romantic French piano contemplation; one of the most-cited contemplative pieces in the standard repertoire and the canonical companion to the Debussy above. (Currently bundled — see [CREDITS.md §852](CREDITS.md).)
  - **Scott Buckley — *Meanwhile*** — contemporary "liquid, dreamy world to float in"; ethereal piano + heavily processed synth pads. The high-window register in a present-day processed-piano voice, sitting beside the Debussy *Clair de Lune* and the Ravel *Pavane*. (Currently bundled — see [CREDITS.md §852](CREDITS.md).)

### 963 Hz — *the crown*

- **Folk name:** "The God frequency."
- **Intention:** The top of the Solfeggio scale. The dial keeps it as the rightmost tone — present, but reserved for moments the listener reaches for.
- **Historical music:**
  - **Tallis — *Spem in Alium* (c. 1570), final cadence** — forty voices arriving on the same chord. The Western canon's most sustained moment of "everything in agreement."
  - **Tallis — *Videte miraculum* (c. 1575)** — the Marian responsory for the Feast of the Purification; soprano-led upper voices sustained over an alto-tenor ground, eight minutes of the same vertical air the band asks for. (Currently bundled — see [CREDITS.md §963](CREDITS.md).)
  - **Bach — *Mass in B minor* (BWV 232, completed 1749)** — Bach's last and largest sacred work. The *Sanctus* is the canonical "everything in agreement" movement; the *Agnus Dei* (currently bundled, for licensing reasons — see [CREDITS.md](CREDITS.md)) sits in the same register of arrival.
  - **Hildegard von Bingen — *O virtus Sapientiae*** — chant at the upper end of the female register; written about wisdom as a wheel turning.
  - **Allegri — *Miserere*, the high C of the boy soprano** — the moment that, by tradition, was forbidden to be transcribed outside the Sistine Chapel until Mozart wrote it down from memory at fourteen.
  - **Byzantine *cherubic hymn*** — Eastern Orthodox liturgy; the moment the choir signals the descent of the holy. The bundled long-form Byzantine ecclesiastical hymn carries the same *ison* (held drone) tradition. (Currently bundled — see [CREDITS.md §963](CREDITS.md).)
  - **Guqin — *Liu Shui* (Flowing Water)** — the piece NASA placed on the Voyager Golden Record in 1977 as the single Chinese contribution to humanity's interstellar mixtape. Silk-string zither, vast pauses, and the canonical "listening to the cosmos" piece in the Chinese repertoire — three traditions (Bach, Byzantine, guqin) arriving at the same upper register from different directions. (Currently bundled — see [CREDITS.md §963](CREDITS.md).)
  - **Znamenny chant — *Da molchit vsjaka plot chelovecha*** — *"Let all mortal flesh keep silence,"* the Holy Saturday Cherubikon substitute sung by the Moscow Patriarchal Choir in the indigenous Russian *znamenny* monophonic chant tradition. A third Christian chant voice for the crown band beside the Latin Gregorian elsewhere on the radio and the bundled Byzantine ecclesiastical hymn — Latin, Greek, and Slavic arriving at the same upper-register stillness. (Currently bundled — see [CREDITS.md §963](CREDITS.md).)

---

## The two ambient companions

These are not on the 9-tap dial. They live in the auto loop and in the night band.

### 432 Hz — *Verdi's A*

- **Status:** Not a Solfeggio tone. A *tuning standard* — the pitch the note A is set to.
- **History:** Giuseppe Verdi formally petitioned the Italian government in 1884 to set A = 432 Hz, calling it "the scientific pitch." The petition succeeded briefly. Modern orchestras settled on A = 440 Hz at a 1939 ISO conference. Period-instrument ensembles still use lower pitches: **Baroque pitch (A = 415 Hz)** for Bach, even lower for French Baroque.
- **What it means for SoulRadio:** When we use orchestral or choral recordings, we prefer historically-informed performances at A = 415 or A = 432 over the modern A = 440. A "Verdi tuning" preset re-tunes the room by ~32 cents — a difference the body notices without being able to name.
- **Current build:** Caruso, "Celeste Aida" from Verdi's *Aida* (Victor, 1 February 1904); Caruso, "Una furtiva lagrima" (Victor, 26 November 1911); **Adelina Patti, *Home, Sweet Home*** (Victrola, 1905) — Patti was born in 1843, the last living link to Verdi's own era; **Francesco Tamagno, "Niun mi tema"** from Verdi's *Otello* (Victor, 1903) — Tamagno was Verdi's chosen Otello at the 1887 La Scala premiere, the role written for his voice; and **Ernestine Schumann-Heink, Brahms *Wiegenlied*** (Victor Orchestra, 1906) — the most-sung lullaby in the Western Romantic repertoire, by the contralto Brahms himself had heard in Vienna. The Metropolitan Opera and most major theatres held A at 432–435 Hz from the late 19th century until WWII; all five discs were cut inside that tuning era. Pitch on acoustic-era discs drifts with playback speed, so these are Verdi-tuning-*era* recordings rather than verifiably-432-Hz ones. See [CREDITS.md §432](CREDITS.md).
- **Music:** anything performed by *Jordi Savall / Hespèrion XXI*, *The Tallis Scholars*, *Sir John Eliot Gardiner / English Baroque Soloists*, *Philippe Herreweghe / Collegium Vocale Gent*.

### 7.83 Hz — *the Schumann resonance*

- **Status:** Not audible. The fundamental electromagnetic resonance of the cavity between Earth's surface and the ionosphere — measured by Winfried Otto Schumann in 1952.
- **Intended use:** Below the threshold of human hearing (~20 Hz). The design role is a *pulse* — a soft amplitude modulation riding under whatever else is playing — not an audible tone. The body senses it as cadence, not sound.
- **Current build:** A 7.83 Hz amplitude modulation rides under the carrier whenever the night band plays — implemented as a Media3 audio processor at the engine ([SchumannUnderlay.kt](app/src/main/java/com/soulradio/soulradio/SchumannUnderlay.kt)), engaged and disengaged on a soft 3-second ramp so the band transition has no click. Modulation depth is conservative on purpose; the manifesto's "respect the ear" rules out audible tremolo, so the cadence sits below where the ear names it. The carrier the pulse rides under is a Gregorian chant recorded in the Abbey of Sant'Antimo (Tuscany, 12th c. Romanesque), where the stone gives the recording an 8-second tail. Seven further night-band recordings are bundled alongside it: the long-form **Gregorian *Improperia*** (Good Friday Reproaches, 16+ minutes of unison plainchant); the full **Choral Compline** sung at Trinity Church, Boston (29 May 2016, ~18 minutes) — Compline being the literal night office of the Western canonical hours; the Zen Buddhist **shakuhachi *honkyoku Shika no Tōne*** (*Distant Cry of Deer*) played by Araki Kodō III on Victor 13029 (1925–37) — breath-driven long tones, the closest Eastern equivalent to the Western contemplative night office; a field recording of **Tibetan monks chanting at Gyuto Branch Monastery**, McLeod Ganj (Samuel Corwin, 2016) — the deep overtone (multiphonic) chant tradition, the lowest-register sung sound on earth and the closest acoustic analogue to a sub-audible drone; **Kevin MacLeod's *Tranquility*** (16-min long-form ambient, [incompetech.com](https://incompetech.com), CC BY 4.0) — the night band's contemporary CC-released voice; a 27-minute performance of **Raga Yaman** filmed in Benares (INNOKINO, 2017) — the canonical Hindustani late-evening / first-watch raga, sitar over tanpura drone, the *alap-jor-jhala* arc that this band's drone morphology was built around; and **M.S. Subbulakshmi's *Sacred Melodies of Lord Vishnu*** (37 min) — the Carnatic (South Indian) devotional voice, by the singer who became the first musician of any tradition to receive the Bharat Ratna (1998), sitting beside Raga Yaman as the Hindustani–Carnatic pair. The hum is the room, not a tone.
- **Historical analogue:** stone-cathedral reverb. Romanesque abbeys (Vézelay, Fontenay, Cluny III) had reverberation tails of 8–12 seconds. The *resonance of the room* is the pre-modern equivalent of an ambient low pulse.
- **Candidate, not yet bundled — slow Sufi qawwali devotional layers.** The *hamd* (praise of God), *naat* (praise of the Prophet), and slow *ghazal* settings of the qawwali tradition — sung Sufi poetry over harmonium and tabla, *before* the climactic high-tempo *takrar* sections engineered for *fana*. Would sit beside Raga Yaman and Subbulakshmi's *Sacred Melodies of Lord Vishnu* as the Islamic devotional voice on the night band — a third Indian-subcontinent devotional voice (Hindustani · Carnatic · Sufi). The slow layers fit the night band because their effect requires the listener's participation — they do nothing *to* a non-attending body and so are loop-safe (see [docs/tunables.md § Two modes, two curation rules](docs/tunables.md#two-modes-two-curation-rules) for the cut). The climactic *takrar* sections of qawwali are state-induction-by-mechanism — refused in the loop, but admissible on the dial as a curator's call; that is a separate question from this 7.83 entry. Sourcing constrained by the radio's CC-license rule (see [docs/licensing.md](docs/licensing.md)) — likely path is shrine field-recordings or pre-1960s commercial discs in jurisdictions where they have entered the public domain.
- **Future-curator note — shorter alap-focused Yaman recording.** The currently-bundled `raga_yaman_innokino.opus` is a 27-minute performance covering the full *alap → jor → jhala* arc. Volume analysis of the file shows the back third (the *jhala* finale, with rhythmic tabla acceleration) runs ~+3.2 dB denser in mean energy than the *alap* — borderline against the night band's "stone-cathedral reverb" intention, which is the most demanding wallpaper bar in the schedule. Not blocking — the rise is gradual, peak levels are stable across the recording, and continuous tabla habituates rather than startles. But an *alap*-focused Yaman recording (10–15 min, slow exposition only) would fit the slot more cleanly when sourceable. The current recording stays until that exists.

---

## A note on the medieval root of the word *Solfeggio*

The modern Solfeggio set (174, 285, 396, 417, 528, 639, 741, 852, 963 Hz) was assembled by Joseph Puleo in the 1990s from a numerological reading of *Numbers* 7. It is recent.

The *word* "solfeggio" is much older. Guido of Arezzo, around **1025 AD**, taught his choirs to read pitches by singing the syllables **Ut, Re, Mi, Fa, Sol, La** — drawn from the opening syllables of each line of the hymn ***Ut queant laxis*** (Hymn to St. John the Baptist):

> *Ut queant laxis*
> *Resonare fibris*
> *Mira gestorum*
> *Famuli tuorum*
> *Solve polluti*
> *Labii reatum*
> *Sancte Iohannes*

Each line begins one step higher than the last. From those first syllables — *Ut, Re, Mi, Fa, Sol, La* — descend the modern *do, re, mi, fa, sol, la, ti*, and the word *sol-fa* / *solfège* / *solfeggio*.

When SoulRadio uses the word, we mean both: the modern numerical set *and* the thousand-year-old practice of teaching the body to find a pitch by ear alone.

---

## A note on tuning

Most pre-electronic recordings cited above were made — or are now performed — at A = 440, A = 432, or A = 415. None of them are "tuned to 528 Hz" in the modern Solfeggio sense. Claims that *Canon in D* or *Cello Suite No. 1* are "naturally" at a Solfeggio frequency are retrofitted.

What IS true: these pieces share *modal character, tempo, and acoustic intention* with the corresponding Solfeggio tone. A Bach cello prelude in G major and a 174 Hz drone are not the same sound — but they are the same **request** to the listener: settle, ground, lower the shoulders.

That kinship is the basis on which SoulRadio pairs them. Not numerology. Family resemblance.

---

## What this map is not

- It is not a prescription. No frequency on this list is a treatment for any condition.
- It is not a closed canon. The historical music examples are *one* curator's pairing; another curator would pair differently and be no less correct.
- It is not a substitute for silence. The most useful frequency in the SoulRadio set is the one between tracks.

---

*See also:* [MANIFESTO.md](MANIFESTO.md) · [ORIGINS.md](ORIGINS.md)
