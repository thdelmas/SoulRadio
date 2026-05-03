# Tunables — what is on the dial, what was considered, what was passed over

A reference for the question that will keep arriving: *"why isn't 136.1 Hz on the radio?"*, *"what about A=415?"*, *"could we add a brainwave band?"*

This document is a defence of the curation, not a roadmap. It sits next to [FREQUENCIES.md](../FREQUENCIES.md) (which describes the eleven that *are* on the dial) and [licensing.md](licensing.md) (which describes which recordings are eligible to fill them). The rule that decides what belongs on the dial is in [MANIFESTO.md](../MANIFESTO.md) — this document just shows the work.

Two senses of "tunable" are tangled in casual use. They have to be separated before the catalogue makes sense.

- **Reference pitches** are the value of A4 — they set the key the whole piece is in. A=440 vs A=432 vs A=415 is a property of *the recording*, not of *the dial*. You cannot tune the radio to A=415; you can only choose recordings already performed at A=415.
- **Standalone tones** are a fixed Hz played as the thing itself. 528 Hz, 7.83 Hz. These *are* what dial stations are made of.

The radio currently mixes them on purpose: nine standalone tones (the Solfeggio set) plus two companions, one of which (432) is a reference pitch attached to a curated set of acoustic-era recordings, and one of which (7.83) is a sub-audible standalone tone standing in until the underlay layer ships. Eleven stations total. See [Frequency.kt](../app/src/main/java/com/soulradio/soulradio/Frequency.kt) for the source of truth.

## Two modes, two curation rules

The eleven stations are not all available in the same way. The radio has two distinct modes, and the curation logic that decides what belongs is different for each.

**Auto loop** (passive). The 24-hour clock-driven schedule in `Frequencies.forHour()`. The listener has not asked for anything; the radio is wallpaper. The auto loop must:

- follow the diurnal arc (morning → noon warmth → afternoon connection → evening dissolve → night settling),
- never ask for energy the listener didn't volunteer,
- never jolt between bands,
- refuse any register that breaks "the room recedes" — urgency, ecstasy, surprise.

The auto loop currently uses **eight** of the eleven stations: 396 → 741 → 528 → 639 → 417 → 285 → 174 → 7.83. It deliberately excludes **852, 963, and 432**.

**Proactive (dial tap)**. The 9-tap dial. The listener has reached for a station — they have signalled intent. The looser rules here are:

- the listener invited it, so the station can carry more weight than the auto loop would,
- registers that would be inappropriate to surprise someone with become defensible (high-window suspension, crown arrival, an acoustic-era operatic voice),
- still no medical claims, still no engagement loops — those clauses are absolute regardless of mode.

**The 852, 963, and 432 stations exist *only* as proactive choices.** This is not an arbitrary cut-off; it is the rule that lets the auto loop stay wallpaper. The "high window" and "the crown" are too vertical to drop on someone at 4 p.m. unannounced. Verdi's A and the acoustic-era opera voices are deliberately *narrative* — they ask to be sat with — and the auto loop cannot sit you down.

This distinction is the single most important curatorial axis the radio has. Most "should we add X?" questions resolve cleanly once you ask it of each mode separately.

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

432 is on the dial as a *tuning* applied to acoustic-era recordings (Caruso, Patti, Tamagno, Schumann-Heink), not as a 432 Hz sine. 7.83 is below the threshold of human hearing (~20 Hz); it currently stands in for the *room* it would have ridden inside (Sant'Antimo chant, Tibetan overtone chant, late-night raga). Both are documented at length in [FREQUENCIES.md § The two ambient companions](../FREQUENCIES.md#the-two-ambient-companions).

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

**Reason for refusal:** these tones are typically delivered as bare sine waves with a "wellness" intention attached — exactly the category MANIFESTO §5 (no medical claims) and the "what we keep out" clause in [CLAUDE.md](../CLAUDE.md) push against. The radio's rule is *artistic music a curator chose*. A pitch-shifted orbital period is not music; it is a number.

### Brainwave bands (entrainment territory)

| Band   | Hz range  | Why not                                                                                                                                                                       |
|--------|-----------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Delta  | 0.5–4     | Below human hearing. Used in binaural-beat apps to "induce sleep" — a medical claim the manifesto refuses.                                                                     |
| Theta  | 4–8       | Same — "meditation states." Crosses 7.83 territory but with intent attached.                                                                                                    |
| Alpha  | 8–12      | "Relaxation." Same.                                                                                                                                                            |
| Beta   | 12–30     | "Focus / productivity." Same — and "productivity" is the engagement-loop framing the radio rejects (no streaks, no goals).                                                     |
| Gamma  | 30–100 (40 typical) | "Cognition / attention." Same.                                                                                                                                                |

**Reason for refusal:** every brainwave band on the market is sold with a state-induction promise. SoulRadio does not promise states. The closest the radio comes to entrainment is the 7.83 *pulse* — and even there, the design role is "soft amplitude modulation under music," not a binaural beat.

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

### Two modes, two curation rules

The radio has two products in one — the auto loop and the dial — and they have different jobs, so they have different bars.

- **Loop = music that has no effect on a non-attending listener.** The loop is wallpaper. It plays whether or not the listener is paying attention; it has to be safe in the absence of engagement. The loop's rule is therefore strict: only music whose effect *requires the listener's participation*. Sant'Antimo plainchant, M.S. Subbulakshmi's sacred Carnatic devotional (on [7.83](../FREQUENCIES.md#783-hz--the-schumann-resonance)), Tibetan overtone chant, slow Sufi devotional poetry, slow contemplative Bach. Take the listener's attention away and the music is just beautiful sound; nothing is being done *to* a non-attending body.
- **Dial = music the listener has reached for.** The tap *is* the consent. A station the listener selected can include music that does anything to them — energize, calm, induce contemplative or ecstatic states, sharpen focus, drive a workout, discharge an emotion — because the listener has invited it. The dial's rule is permissive about effect; what the dial actually *offers* is an editorial choice by the curator.

Four patterns are refused **in the loop** that are admissible **on the dial**:

- **State-induction by mechanism** — binaural beats, hemi-sync, isochronic tones, the climactic high-tempo *takrar* sections of qawwali engineered for *fana*, the worked-up sections of charismatic gospel and Pentecostal shape-note revival. The recording does the work; the listener's attention isn't required. Refused in the loop because the loop catches a non-attending listener. Admissible on the dial because the tap is the consent — currently no such station is offered, but the cut allows it.
- **Propulsion / urgency** — *Summer* from Vivaldi's Four Seasons, BWV 565 *presto*, fast minimalism (Reich's *Piano Phase*, Glass at speed), trailer cues, driving concerti grossi. Tempo as mechanism. A 3 a.m. listener should not be put into fight-or-flight by the dial. Admissible on the dial when the station announces it — *active* Bach (a *Brandenburg* allegro, a partita courante) can live on a tapped morning gate (396) or clearing (741) for a listener who invited the energy.
- **Sharply contoured dynamics** — sudden jumps, unannounced silence-then-loud, attention-grabbing transitions. The loop respects the room; the dial can host more sharply contoured music if a station announces it.
- **Drop-structure architecture** — EDM build/drop, trailer *braaams*, hook-drop pop, drop-conditioned trailer scores. Refused in the loop (nothing should condition a non-attending listener's nervous system). Admissible on the dial as a curator's call. **Curatorial consideration — autonomy-erosion:** drops differ from other mechanism music in that they tend to *condition compulsion over time* — each "voluntary" tap can gradually erode actual choice in a way that calm-inducing binaural beats or contemplative *takrar* don't. Weigh that against the real positive uses (catharsis, exercise motivation, mood discharge) before adding such a station. The curator's bar is higher here than for, say, a binaural-beat station, but it is not a manifesto refusal.

### Summary

| Pattern | Loop | Dial |
|---------|------|------|
| No-effect filler / wellness slop | Refused | Refused |
| State-induction by mechanism (binaural beats, *takrar*) | Refused | Admissible (curator's call) |
| Propulsion / urgency | Refused | Admissible when a station announces it |
| Sharply contoured dynamics | Refused | Admissible when a station announces it |
| Drop-structure (EDM build/drop, trailer *braaams*, hook-drop pop) | Refused | Admissible (curator's call; autonomy-erosion concern) |
| Tradition music (plainchant, Subbulakshmi, slow Sufi poetry, contemplative Bach) | Admissible | Admissible |

The unifying principle: **the radio is a set of tools the listener can reach for to shape their own attention.** The loop is the room (wallpaper, no demands, safe to ignore); the dial is the conversation (effects the listener invited). The single universal refusal is **meaningless music** — the only kind that cannot be a tool, by definition. Everything else is a question of which mode the music belongs in, and for some patterns (drop-structure most prominently) whether the curator chooses to offer it given the trade-off between the music's real positive uses and its autonomy-erosion risk over time.

---

## The decision tree

Any "should we add X?" proposal has to clear two distinct tests, because the auto loop and the dial have different rules.

**The shared test (both modes):**

1. Does it have a musical tradition behind it that a curator can fill with *artistic music a curator chose* (per [CLAUDE.md](../CLAUDE.md))?
2. Can it be added without the radio's UI or copy *claiming* a state, medical effect, or therapeutic outcome? The radio describes what people have *used* a frequency for ("a clearing tone," "the morning gate") and never what it will *do* to the listener — see the **House rule** at the top of [FREQUENCIES.md](../FREQUENCIES.md) and [MANIFESTO.md](../MANIFESTO.md)'s *"we will not pretend a frequency is a prescription"* line. Music on the station may have strong effects — that is the point, per the [Curation rules](#curation-rules--the-music-side) above — but the radio's own description of the station never claims them.
3. Does it cover experiential ground the existing eleven stations don't already cover?

If any answer is no, drop the proposal. If all three are yes, continue.

**The auto-loop test (stricter):**

4. Does it fit a specific window of the diurnal arc without displacing what's already there?
5. Is its register *wallpaper-grade* — does it survive being arrived at without warning?
6. Does it transition cleanly from the band before and into the band after?

If any answer is no, the station does not belong in the auto loop. It may still belong on the dial as a tap-only station.

**The tap-only test (looser):**

4. Is the register one a listener might *reach for* deliberately?
5. Does it ask for something the auto loop should not ask of an unprepared listener?

If both are yes, it can live as a proactive-only station alongside 852, 963, and 432. If neither, the proposal is not earning its slot.

The default for any proposal is **no.** The radio is more useful at eleven than at fifty.

---

## Why this catalogue exists

The radio could be much bigger. Most music apps are. The discipline of staying at eleven is the discipline of the manifesto: the room recedes when there is enough space between the things the dial offers, and there is no space if the dial keeps growing.

When a future contributor (or a future me) proposes "let's add 136.1 Hz," this document is the answer that says: we considered the whole landscape, we know what it would mean, and the radio is more useful at eleven than at fifty.

---

*See also:* [MANIFESTO.md](../MANIFESTO.md) · [FREQUENCIES.md](../FREQUENCIES.md) · [licensing.md](licensing.md) · [Frequency.kt](../app/src/main/java/com/soulradio/soulradio/Frequency.kt)
