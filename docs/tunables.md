# Tunables — what is on the dial, what was considered, what was passed over

A reference for the question that will keep arriving: *"why isn't 136.1 Hz on the radio?"*, *"what about A=415?"*, *"could we add a brainwave band?"*

This document is a defence of the dial's curation *and* the inventory for Radio mode. It sits next to [FREQUENCIES.md](../FREQUENCIES.md) (which describes the eleven that *are* on the dial) and [licensing.md](licensing.md) (which describes which recordings are eligible to fill them). The rules that decide what belongs on the dial — and what can be exposed in Radio without bleeding into the room — are in [MANIFESTO.md](../MANIFESTO.md); this document just shows the work.

Two senses of "tunable" are tangled in casual use. They have to be separated before the catalogue makes sense.

- **Reference pitches** are the value of A4 — they set the key the whole piece is in. A=440 vs A=432 vs A=415 is a property of *the recording*, not of *the dial*. You cannot tune the radio to A=415; you can only choose recordings already performed at A=415.
- **Standalone tones** are a fixed Hz played as the thing itself. 528 Hz, 7.83 Hz. These *are* what dial stations are made of.

The radio currently mixes them on purpose: nine standalone tones (the Solfeggio set) plus two companions, one of which (432) is a reference pitch attached to a curated set of acoustic-era recordings, and one of which (7.83) is a sub-audible standalone tone standing in until the underlay layer ships. Eleven stations total. See [Frequency.kt](../app/src/main/java/com/soulradio/soulradio/Frequency.kt) for the source of truth.

## Three modes, three curation rules

The radio has three distinct modes, and the curation logic that decides what belongs is different for each.

**Auto loop** (passive — "DJ"). The 24-hour clock-driven schedule in `Frequencies.forHour()`. The listener has not asked for anything; the radio is wallpaper. The auto loop must:

- follow the diurnal arc (morning → noon warmth → afternoon connection → evening dissolve → night settling),
- never ask for energy the listener didn't volunteer,
- never jolt between bands,
- refuse any register that breaks "the room recedes" — urgency, ecstasy, surprise.

The auto loop currently uses **eight** of the eleven dial stations: 396 → 741 → 528 → 639 → 417 → 285 → 174 → 7.83. It deliberately excludes **852, 963, and 432**.

**Dial** (proactive, quick access). The 9-tap dial — eleven stations, one tap each. The listener has reached for a station; the tap is the consent. The looser rules here are:

- the listener invited it, so the station can carry more weight than the auto loop would,
- registers that would be inappropriate to surprise someone with become defensible (high-window suspension, crown arrival, an acoustic-era operatic voice),
- still no medical claims, still no engagement loops — those clauses are absolute regardless of mode.

**The 852, 963, and 432 stations exist *only* as proactive Dial choices, not on the auto loop.** This is not an arbitrary cut-off; it is the rule that lets the auto loop stay wallpaper. The "high window" and "the crown" are too vertical to drop on someone at 4 p.m. unannounced. Verdi's A and the acoustic-era opera voices are deliberately *narrative* — they ask to be sat with — and the auto loop cannot sit you down.

The dial is held to **eleven** for a structural reason, not an aesthetic one: the room recedes when there is space between the things the dial offers, and there is no space if the dial keeps growing. Adding a twelfth station to the dial does not happen lightly — see the decision tree below.

**Radio** (proactive, extensive access). A separate, opt-in surface for exploring the wider catalogue documented in this file — frequencies considered for the dial that did not earn a slot, plus the eleven that did. The Radio is governed by the [MANIFESTO.md](../MANIFESTO.md) clause *"Exploration is opt-in, never autoplay, never the default surface"*: it does not run on its own, it does not bleed into the auto loop or dial, and the listener has to deliberately walk into it. The looser rules here are:

- the listener has not just tapped a station, they have *changed surface* — the consent is stronger than a dial tap, and the catalogue can be correspondingly wider,
- frequencies refused for the dial on **curation grounds** (no tradition, sine-only, would crowd the dial) can appear in Radio because the dial-smallness rule no longer applies — Radio is precisely the room where the wider catalogue lives,
- frequencies refused for the dial on **manifesto grounds** (medical-claim framing, engagement-loop framing, no-effect filler) remain refused in Radio. Changing surface does not change the non-negotiables.

The single most important curatorial axis the radio has is which of these three modes a frequency belongs in. Most "should we add X?" questions resolve cleanly once you ask it of each mode separately.

---

## On the dial

### The nine Solfeggio tones (standalone)

| Hz  | Name             | Provenance                                                                      |
|-----|------------------|---------------------------------------------------------------------------------|
| 174 | foundation       | Lowest tone of the modern Solfeggio set (Puleo, 1990s, from *Numbers* 7).      |
| 285 | slow turn        | Solfeggio.                                                                      |
| 396 | morning gate     | Solfeggio. *Ut queant laxis* — first syllable of the medieval hexachord.       |
| 417 | dissolver        | Solfeggio. *Re*.                                                                |
| 528 | centre           | Solfeggio. *Mi* — the popular tradition's "love frequency."                    |
| 639 | table            | Solfeggio. *Fa*.                                                                |
| 741 | clearing         | Solfeggio. *Sol*.                                                               |
| 852 | high window      | Solfeggio. *La*.                                                                |
| 963 | crown            | Solfeggio. The top of the set.                                                  |

The set is folkloric, not scientific. It is on the dial because it is the one nine-tone vocabulary the modern listener is most likely to recognise the *intention* of — and because the band names ("foundation," "slow turn," "table," "crown") give a curator nine doors to gather pre-electronic music behind. See [FREQUENCIES.md](../FREQUENCIES.md) for the historical music paired with each.

### The two companions

| Hz   | Name           | Provenance                                                                           |
|------|----------------|--------------------------------------------------------------------------------------|
| 432  | Verdi's A      | Reference pitch petitioned by Verdi to the Italian government in 1884.              |
| 7.83 | Schumann       | Fundamental electromagnetic resonance of the Earth–ionosphere cavity (Schumann, 1952). |

432 sits on the radio as a curator's call: the band plays only acoustic-era recordings cut inside the A = 432–435 tuning era (Caruso, Patti, Tamagno, Schumann-Heink). It is not a 432 Hz sine, and it is not a runtime pitch-shift applied to modern A = 440 recordings — the lower pitch is carried by the discs themselves. 7.83 is below the threshold of human hearing (~20 Hz); it currently stands in for the *room* it would have ridden inside (Sant'Antimo chant, Tibetan overtone chant, late-night raga). Both are documented at length in [FREQUENCIES.md § The two ambient companions](../FREQUENCIES.md#the-two-ambient-companions).

---

## Considered, not added

### Reference pitches (other historical A4 standards)

| Pitch        | Era / context                                | Why not a station                                                                                                                                                            |
|--------------|----------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| A = 440      | Modern ISO standard (1939, ratified 1955).   | Already the default tuning of most bundled recordings. Making it a station would mean making "the modern world" a station — there is nothing to listen to that isn't already at A=440. |
| A = 415      | Baroque pitch (Bach, Vivaldi).               | A property of historically-informed recordings, not a station. A Bach cello prelude at A=415 belongs on 174 (foundation), not on its own band.                                |
| A = 392      | French Baroque *ton de chapelle*.            | Same logic as A=415 — it is the tuning of the *recording*, not a thing to tune the radio to.                                                                                  |
| A = 466      | German Baroque *Chorton* (church organs).    | Same.                                                                                                                                                                          |
| A = 435      | 1859 French *diapason normal*.               | Same. Bridges 432 and 440; nothing on the dial would change if it existed.                                                                                                    |
| A = 421.6    | Mozart-era.                                  | Same.                                                                                                                                                                          |
| A = 422.5    | Handel's tuning fork.                        | Same.                                                                                                                                                                          |
| A = 444 / 449 | Modern sharper standards (Boston, 19th c. Paris Opera). | Same. Brighter than 440; not a contemplative direction.                                                                                                                       |
| C = 256 ("scientific pitch") | Sauveur, 1701; revived in some New-Age circles.    | Already approximated by A=432 (which gives C≈256). Adding it duplicates Verdi's A.                                                                                            |

**The general rule:** a reference pitch is a property of the *recording*, not a dial station. It can shape curation (we prefer Baroque-pitch Bach over A=440 Bach for the 174 band) without earning its own slot.

### Cousto's Cosmic Octave (planetary tones)

Hans Cousto's 1978 system pitch-shifts orbital periods up into audible range. The numbers are real arithmetic; the claim that they're *meaningful* to a listener is folkloric.

| Hz       | Name                              | Why not                                                                                                                                                          |
|----------|-----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 136.1    | Earth year ("OM tone")            | Adopted by Indian classical practice as a tanpura tuning, which is a real and defensible use — but the radio already covers that lineage at 396 (Bhairavi) and 7.83 (Yaman, Subbulakshmi). |
| 194.18   | Earth day                         | Cousto-derived, not a tradition. Numerology.                                                                                                                      |
| 126.22   | Sun                               | Same.                                                                                                                                                              |
| 210.42   | Moon                              | Same.                                                                                                                                                              |
| 144.72 / 183.58 / 147.85 / others | Mars / Jupiter / Saturn / planets | Same. Adding one means adding the set; the set is precisely the "wellness audio that wasn't made as music" the manifesto rejects.                                  |
| 14.3 / 20.8 / 27.3 / 33.8 | Schumann harmonics      | The fundamental (7.83) is already on the dial as a sub-audible companion. Stacking the harmonics would turn the night band into a frequency lab.                   |

**Reason for refusal:** as commonly delivered, these tones arrive as bare sine waves with a "wellness" intention attached — exactly the category MANIFESTO §5 (no medical claims) and the "what we keep out" clause in [CLAUDE.md](../CLAUDE.md) push against. The radio's rule is *artistic music a curator chose*. A pitch-shifted orbital period, on its own, is a number with a wellness gloss attached, not music. A specific lineage could in principle rescue a specific tone — Indian classical's adoption of 136.1 as a tanpura tuning is the closest case — but that lineage is already covered at 396 and 7.83, and none of the others has such a bridge.

### Brainwave bands (entrainment territory)

The cut here is on **product framing**, not on the Hz value. [MANIFESTO §5](../MANIFESTO.md) forbids medical/health *claims* about a frequency — a rule about copy and UX, not about the number. 7.83 sits inside the Theta band (4–8 Hz) and ships as a station; what makes it not-a-binaural-beats-station is the framing (Schumann resonance, geophysical), the role (sub-audible companion, amplitude modulation under music), and the content (not a bare sine sold with state copy). A brainwave-band Hz is admissible only if an integration can clear *both* the no-claims rule and the curation tests below — and most can't, on the curation side.

| Band   | Hz range  | Why no foreground station                                                                                                                                                                       |
|--------|-----------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Delta  | 0.5–4     | Sub-audible — can only function as pulse/modulation, the role 7.83 already fills. No musical tradition at this register a curator could fill; a foreground "Delta station" would land as bare sine and arrive pre-loaded with sleep-induction framing the market built around it. |
| Theta  | 4–8       | 7.83 already lives here. A second Theta station would either duplicate it or push the band toward meditation-induction framing 7.83 deliberately avoids by sitting under music rather than as foreground.                                          |
| Alpha  | 8–12      | Audible, but no musical tradition behind the band as such — would land as bare sine, refused by the audio-side rule in [CLAUDE.md](../CLAUDE.md), not by the Hz value. "Relaxation" is the dominant market framing and would be hard to escape with copy alone.                |
| Beta   | 12–30     | Same "no tradition to curate" problem. "Focus / productivity" framing dominates — and "productivity" is the engagement-loop register the radio rejects (no streaks, no goals).                                  |
| Gamma  | 30–100 (40 typical) | Same. Pre-loaded with "cognition / attention" framing; no tradition to fill the band as anything but bare sine.                                                                          |

**Reason for refusal:** the manifesto refuses *claims*, not Hz values. A brainwave-band foreground station is refused because (a) more decisively, no brainwave band has a musical tradition a curator can fill — so the station lands as a bare sine, refused on audio grounds — and (b) the product category arrives pre-loaded with state-induction framing that's difficult to escape with neutral copy alone. 7.83 is the in-repo existence proof that a brainwave-band Hz can be integrated honestly: it does so by sitting *under* music as a sub-audible pulse, not by being the foreground.

### Numerological / Pythagorean tones

| Set                           | Why not                                                                                                                                       |
|-------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| 111, 222, 333 … 999 ("angel numbers") | Numerology with no musical tradition behind it. Bare sines; nothing to curate.                                                                |
| Pythagorean ratios as tones   | Pythagoras's contribution is a *system of intervals*, not a list of frequencies. Belongs to "out of scope" below.                              |

### Other named tones in circulation

| Tone     | Claimed source                                | Why not                                                                                                       |
|----------|-----------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| 8 Hz "Earth pulse" / "genius frequency" | Various New-Age sources.                  | Folklore stacked on top of folklore. 7.83 already covers the sub-audible pulse role honestly.                |
| 40 Hz "neuroscience gamma"     | Real research; product claims are not.        | Medical-claim territory.                                                                                       |
| 1122 Hz                        | Sometimes appended to the Solfeggio set.      | Marginal even within the Solfeggio tradition. Nine is enough.                                                  |

---

## Out of scope — different product

These are tuning *systems* (not single Hz values). Exposing them would mean letting the user choose how the *whole* musical universe is divided, which is a teaching tool, not a radio.

| System                  | What it is                                                                                  |
|-------------------------|----------------------------------------------------------------------------------------------|
| 12-tone equal temperament | The standard Western division of the octave into twelve equal semitones.                   |
| Just intonation         | Pitches built from small whole-number frequency ratios (3:2, 4:3, 5:4 …).                  |
| Pythagorean             | Pitches built by stacking pure perfect fifths.                                               |
| Meantone (¼-comma, ⅙-comma) | Renaissance and early Baroque keyboard temperaments.                                       |
| Werckmeister / Kirnberger | Bach-era well-temperaments. (One bundled recording uses Kirnberger — see Scarlatti K.87 on 741.) |
| 22 shrutis              | The microtonal grid of Hindustani classical music.                                           |
| Maqam quarter-tones     | Arabic / Turkish / Persian intervals between the Western semitones.                          |
| Slendro / Pelog         | Javanese gamelan tuning systems.                                                             |

The radio's relationship to these systems is editorial: when a recording is performed in a non-equal-tempered system, that is a feature of the recording (and worth noting in [CREDITS.md](../CREDITS.md)). It is not something the dial selects.

---

## Curation rules — the music side

The catalogue above filters by *frequency*. This section filters by *what the music does, or fails to do, for the listener.*

The radio's stance: **music is a tool the listener can reach for to take back control of their attention.** A station that does something for the listener — slows them down, lifts them up, opens contemplative space, sharpens focus, induces ecstatic or contemplative states, drives a workout, releases an emotional charge — is a tool the listener can use. The radio is *not* afraid of mechanism; mechanism is the point. The only thing the radio refuses across the board is **meaningless** music.

### One refusal, applied everywhere

**Music with no effect.** Generic ambient filler, sine-wave wellness slop, AI-generated background tracks, anything that is audio without being music. This is the manifesto's anti-filler rule, already in [CLAUDE.md](../CLAUDE.md) — *"what we keep out: white-noise loops, sine-wave New Age fillers, AI-generated background tracks, and any 'wellness' audio that wasn't made as music."* Music has to *do* something for the listener to earn a place on the radio.

Refused on **both** the loop and the dial. The single absolute rule.

#### Note on [MANIFESTO.md](../MANIFESTO.md) line 41 — *"We will not gamify your nervous system"*

Read in context with the four lines around it (no advertisements, no data sale, no "louder in your life" features, no frequency-as-prescription), this is a rule about **app and UI design**: no streaks, no notifications, no points-and-progression UI, no dopamine-loop interface patterns. It is *not* a rule about audio content. A listener who taps a station with strong-effect music — including drop-structure — has reached for a tool; the manifesto's anti-gamification rule applies to what the *app* does to the user, not to what musical architecture a tapped station carries.

Drop-structure music has real positive uses for a listener who reaches for it deliberately: catharsis (Aristotle's *Poetics*), workout motivation (the dominant fitness genre for measurable reasons), mood discharge, the collective effervescence the music originally exists to produce, aesthetic craft. The line between "drop-structure pop" and "climactic classical" (Beethoven's 5th, Tchaikovsky's *1812 Overture*, Mahler's symphonies) is genuinely fuzzy — all of them are build-and-payoff machines. Treating drops as a categorical refusal would either rule out climactic classical too (which is wrong) or admit a curator-judgment line that isn't a manifesto rule (which is what we actually do).

So drops are treated under the loop/dial cut below, with **autonomy-erosion noted as a curatorial concern, not a refusal.**

### Three modes, three curation rules

The radio has three surfaces — the auto loop, the dial, and the Radio — and they have different jobs, so they have different bars.

- **Loop = music that has no effect on a non-attending listener.** The loop is wallpaper. It plays whether or not the listener is paying attention; it has to be safe in the absence of engagement. The loop's rule is therefore strict: only music whose effect *requires the listener's participation*. Sant'Antimo plainchant, M.S. Subbulakshmi's sacred Carnatic devotional (on [7.83](../FREQUENCIES.md#783-hz--the-schumann-resonance)), Tibetan overtone chant, slow Sufi devotional poetry, slow contemplative Bach. Take the listener's attention away and the music is just beautiful sound; nothing is being done *to* a non-attending body.
- **Dial = music the listener has reached for.** The tap *is* the consent. A station the listener selected can include music that does anything to them — energize, calm, induce contemplative or ecstatic states, sharpen focus, drive a workout, discharge an emotion — because the listener has invited it. The dial's rule is permissive about effect; what the dial actually *offers* is an editorial choice by the curator.
- **Radio = the wider catalogue, behind a deliberate door.** The listener has changed surface, not just tapped a station. The audio-side rules are the same as the Dial's — tap is consent, the universal refusal of meaningless music still applies — but the *frequencies* available are wider (see the Three modes section above). What changes for Radio is the catalogue, not the music rules.

Four patterns are refused **in the loop** that are admissible **on the dial and in Radio**:

- **State-induction by mechanism** — binaural beats, hemi-sync, isochronic tones, the climactic high-tempo *takrar* sections of qawwali engineered for *fana*, the worked-up sections of charismatic gospel and Pentecostal shape-note revival. The recording does the work; the listener's attention isn't required. Refused in the loop because the loop catches a non-attending listener. Admissible on the dial because the tap is the consent — currently no such station is offered, but the cut allows it.
- **Propulsion / urgency** — *Summer* from Vivaldi's Four Seasons (the storm), BWV 565 *presto*, fast minimalism (Reich's *Piano Phase*, Glass at speed), trailer cues. Tempo as mechanism. The cut here is **pressing vs. participatory**: *pressing* music drives the listener's nervous system forward against will (refused in the loop; admissible on the dial when the station announces it). *Participatory* music — a Brandenburg allegro, a partita courante, Vivaldi *Spring* mvt. I, the bright sections of a Hindustani morning raga — has its energy in the *interplay* of voices and lets the listener dip in and out without losing place. Participatory music is admissible in loop bands whose intention already asks for some energy (the table, the morning gate, the clearing). A 3 a.m. listener should not be put into fight-or-flight by the dial. A 4 p.m. listener can sit at a table that is alive.
- **Sharply contoured dynamics** — sudden jumps, unannounced silence-then-loud, attention-grabbing transitions. The loop respects the room; the dial can host more sharply contoured music if a station announces it.
- **Drop-structure architecture** — EDM build/drop, trailer *braaams*, hook-drop pop, drop-conditioned trailer scores. Refused in the loop (nothing should condition a non-attending listener's nervous system). Admissible on the dial as a curator's call. **Curatorial consideration — autonomy-erosion:** drops differ from other mechanism music in that they tend to *condition compulsion over time* — each "voluntary" tap can gradually erode actual choice in a way that calm-inducing binaural beats or contemplative *takrar* don't. Weigh that against the real positive uses (catharsis, exercise motivation, mood discharge) before adding such a station. The curator's bar is higher here than for, say, a binaural-beat station, but it is not a manifesto refusal.

### Summary

| Pattern | Loop | Dial | Radio |
|---------|------|------|-------|
| No-effect filler / wellness slop | Refused | Refused | Refused |
| State-induction by mechanism (binaural beats, *takrar*) | Refused | Admissible (curator's call) | Admissible (curator's call) |
| Propulsion / urgency | Refused | Admissible when a station announces it | Admissible when a station announces it |
| Sharply contoured dynamics | Refused | Admissible when a station announces it | Admissible when a station announces it |
| Drop-structure (EDM build/drop, trailer *braaams*, hook-drop pop) | Refused | Admissible (curator's call; autonomy-erosion concern) | Admissible (curator's call; autonomy-erosion concern) |
| Tradition music (plainchant, Subbulakshmi, slow Sufi poetry, contemplative Bach) | Admissible | Admissible | Admissible |

The unifying principle: **the radio is a set of tools the listener can reach for to shape their own attention.** The loop is the room (wallpaper, no demands, safe to ignore); the dial is the conversation (effects the listener invited at quick reach); the Radio is the room next door (the wider catalogue, walked into deliberately). The single universal refusal is **meaningless music** — the only kind that cannot be a tool, by definition. Everything else is a question of which mode the music belongs in, and for some patterns (drop-structure most prominently) whether the curator chooses to offer it given the trade-off between the music's real positive uses and its autonomy-erosion risk over time.

---

## The decision tree

Any "should we add X?" proposal has to clear distinct tests for each mode, because the auto loop, the dial, and the Radio have different rules. A frequency might earn the Radio without earning the dial; a frequency might earn the dial without earning the auto loop. They are independent gates, asked in order from strictest to loosest.

**The shared test (any mode):**

1. **No medical or therapeutic claim** in the radio's UI or copy. The radio describes what people have *used* a frequency for ("a clearing tone," "the morning gate") and never what it will *do* to the listener — see the **House rule** at the top of [FREQUENCIES.md](../FREQUENCIES.md) and [MANIFESTO.md](../MANIFESTO.md)'s *"we will not pretend a frequency is a prescription"* line. Music on the station may have strong effects — that is the point, per the [Curation rules](#curation-rules--the-music-side) above — but the radio's own description of the station never claims them.
2. **Not meaningless music.** Bare wellness sines, AI background tracks, and white-noise loops are refused everywhere — see the universal refusal in [Curation rules](#one-refusal-applied-everywhere).

Both are hard gates: a no on either drops the proposal entirely.

**The auto-loop test (strictest):**

3. Can a curator fill this band with *artistic music a curator chose* that meets the loop's wallpaper rule (effect requires the listener's participation)?
4. Does it fit a specific window of the diurnal arc without displacing what's already there?
5. Is its register *wallpaper-grade* — does it survive being arrived at without warning?
6. Does it transition cleanly from the band before and into the band after?

If any answer is no, the station does not belong in the auto loop. It may still belong on the dial or in Radio.

**The dial test (stricter):**

3. **Curator-feasibility.** Can a curator fill this band with *artistic music a curator chose*? A musical tradition already attached to the frequency is the obvious path — 396 has the medieval hexachord, 432 has the acoustic-era opera lineage, 528 has the Solfeggio set. A frequency without such a tradition can still be argued for, but the burden shifts to the proposer to name the actual recordings, with attributions and licenses that pass [licensing.md](licensing.md).
4. Does it cover experiential ground the existing eleven dial stations don't already cover?
5. Is the register one a listener might *reach for* deliberately?
6. Is the dial-smallness rule still respected — i.e., does the gain from this station outweigh the loss of space between dial offerings? *The dial stays small so the room can recede.* Adding a twelfth station to the dial is the highest bar in this document.

If any answer is no, the station does not belong on the dial. It may still belong in Radio.

**The Radio test (loosest):**

3. Is there *something* — a tradition, a documented use, a credible historical or musical bridge — that lets this frequency exist as more than a number with a wellness gloss? Radio is the wider catalogue, but it is not a sine generator. A frequency that fails this test is not "in the catalogue at all" — it lives in the [Considered, not added](#considered-not-added) section as a documented refusal, not as a Radio station.
4. Has the manifesto's framing rule been preserved? Radio cannot be the loophole through which medical claims, engagement loops, or wellness-product framing re-enter — the non-negotiables apply to every surface.

The default for any proposal is **no on the dial.** The dial is more useful at eleven than at fifty. Radio admits more, but admits nothing automatically; this document is still the gate.

---

## Why this catalogue exists

The radio could be much bigger. Most music apps are. The discipline of staying at eleven *on the dial* is the discipline of the manifesto: the room recedes when there is enough space between the things the dial offers, and there is no space if the dial keeps growing.

That discipline is what makes Radio mode possible without contradicting the manifesto. Because the dial stays small, there is a separate surface — Radio, behind a deliberate door — where the wider catalogue can live without bleeding into the room. This document is now two things at once: the *defence* of the dial's eleven, and the *inventory* of what Radio mode can expose. A frequency that earns a place in this document earns the right to be considered for Radio. A frequency that earns a place on the dial has cleared a much higher bar.

When a future contributor (or a future me) proposes "let's add 136.1 Hz to the dial," this document is the answer that says: we considered the whole landscape, we know what it would mean, and the dial is more useful at eleven than at fifty. When the proposal is "let's expose 136.1 Hz in Radio," the answer comes from the Radio test in the decision tree above.

---

*See also:* [MANIFESTO.md](../MANIFESTO.md) · [FREQUENCIES.md](../FREQUENCIES.md) · [licensing.md](licensing.md) · [Frequency.kt](../app/src/main/java/com/soulradio/soulradio/Frequency.kt)
