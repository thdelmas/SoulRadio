# Origins

_How SoulRadio became SoulRadio. A curated record of the seed conversation — the late-night Q&A that turned a stack of "healing frequency" claims into a manifesto, a 24-hour loop, and a 9-tap dial of pre-electronic recordings._

---

## A note before reading

This document is the *origin story*, not the manual. The manual is [FREQUENCIES.md](FREQUENCIES.md); the rules are [MANIFESTO.md](MANIFESTO.md); the audio sources and licenses are [CREDITS.md](CREDITS.md).

The conversation below is preserved because it shows the *trajectory* — where the project started (modern "healing frequency" folklore) and where it landed (a curated radio of pre-electronic music tied to the hour of day). Several of the AI-generated claims that seeded the early questions — "DNA repair," "natural anesthetic," "cellular healing," "wipes away trauma" — have been recast per MANIFESTO §5 ("we do not lie about science"). What remains is the *folkloric intention* of each tone: what people have *used* the frequency for, not what it does to a body.

The body decides whether it agrees.

---

## Q: What is the "God frequency"? _(22:28)_

The musical tone most often called the "God frequency" is **963 Hz**. It belongs to the modern *Solfeggio set* — nine pitches assembled in the 1990s by Joseph Puleo from a numerological reading of *Numbers* 7. The set is recent. The *word* "solfeggio" is much older — see [§ A note on the word *solfeggio*](#a-note-on-the-word-solfeggio) at the end.

### Folk associations of 963 Hz

- **Folk name:** "Returning to oneness" / the crown of the Solfeggio scale.
- **Traditional use:** meditation, contemplative practice, the closing tone of a session.
- **Stance for SoulRadio:** we treat 963 Hz as the *top of the dial* — present, reachable in one tap, but not a tone the auto loop reaches for. People who want it find it. People who don't, don't.

### Other tones often grouped with 963 Hz

- **528 Hz** — popularly called the *Love frequency* or *Miracle tone*. Folkloric association with warmth and the centre of the day. Our midday band.
- **432 Hz** — not a Solfeggio frequency. A *tuning standard* (the pitch the note A is set to), advocated by Verdi in 1884. We use it as one of the two ambient companions; see [FREQUENCIES.md §432](FREQUENCIES.md).

### Stance

These frequencies are not medical devices. They are *rooms* — a tradition asks the listener to bring a particular kind of attention. The body decides whether it agrees.

---

## Q: do these frequencies still work through headphones and an MP3 player? _(22:28)_

Yes. The relevant questions are the listening environment and the source quality, not the playback chain itself.

### Headphones vs. speakers

- **Headphones** isolate the listener from the room — useful for a focused session, less so for a 24-hour ambient loop.
- **Speakers** let the room participate. The cathedral builders knew this; SoulRadio is designed to be heard *in a room*, not pressed into the ear canal. Headphones are fine. Speakers are the design target.
- **Binaural beats** (where each ear gets a slightly different tone) require headphones. SoulRadio does not use binaural beats.

### Compression and source quality

- **Lossless when feasible.** FLAC stays uncompressed in the APK; the decoder needs to seek and loop without artifacts. See [CLAUDE.md § Code conventions](CLAUDE.md).
- **MP3** is enough to carry these frequencies — the tones are well within the human hearing range. But a high-bitrate MP3 is not the same as the original recording, and on long sessions the difference accumulates.
- **Source matters more than format.** A field recording from the Abbey of Sant'Antimo at 192 kbps Vorbis carries more of the room than a 320 kbps MP3 of a synth pad ever will.

### Wired vs. Bluetooth

- **Bluetooth is supported** — it is the way most listeners will play SoulRadio on a speaker across the room. See [docs/output.md](docs/output.md).
- **Network casting (Cast / AirPlay / DLNA) is not supported.** The app has no `INTERNET` permission and does not initiate any network call.
- **Wired is honest.** No re-encoding, no negotiation, no latency. If you have the option, it is the better path.

---

## Q: what are the other frequencies in the tradition? _(22:33)_

The modern Solfeggio set, with the folk associations the tradition gives each tone (folk associations only — no medical claim is made or implied):

### The nine Solfeggio tones

- **174 Hz** — folk name *"pain relief / sense of security."* Used as a ground note. The lowest tone in the set.
- **285 Hz** — folk name *"tissue and field."* Used to slow the body.
- **396 Hz** — folk name *"liberating guilt and fear."* A clearing tone, often used at the start of a practice.
- **417 Hz** — folk name *"undoing situations / change."* A tone of transition.
- **528 Hz** — folk name *"the love frequency / miracle tone."* The popular tradition's centre.
- **639 Hz** — folk name *"connecting / harmonious relationships."* The frequency of shared rooms.
- **741 Hz** — folk name *"awakening intuition."* A clearing tone for the head.
- **852 Hz** — folk name *"returning to spiritual order."* Used to step back from the immediate.
- **963 Hz** — folk name *"the God frequency."* The top of the scale.

### The two ambient companions

Not Solfeggio — included because they sit alongside the set in the tradition:

- **432 Hz** — Verdi's A. A tuning standard, not a tone.
- **7.83 Hz** — the Schumann resonance. Below the threshold of human hearing; sensed as cadence, not as sound.

The full per-tone description, with paired pre-electronic recordings, is in [FREQUENCIES.md](FREQUENCIES.md).

---

## Q: how long should a session be? _(22:37)_

SoulRadio is designed for *constancy*, not for sessions. The manifesto is explicit: *"You should be able to forget it is on and still feel different at the end of the week."* (See [MANIFESTO.md §2](MANIFESTO.md).)

### What the surrounding tradition says

- **Beginners** in the Solfeggio listening tradition often start at 5–15 minutes.
- **Regular listeners** report 20–30 minute sessions.
- **Sleep listeners** play a tone at low volume across an entire night.

### What SoulRadio does

The auto loop runs 24 hours. The dial is for moments you choose. Both are designed to live at low volume, in the background of a room — *wallpaper, not wallpaper-paste* (MANIFESTO §2).

### Hearing safety

The 60/60 rule applies as it does to any audio: no more than 60% volume for no more than 60 minutes at a stretch when using headphones. Speakers at low room volume are the intended listening case.

---

## Q: are frequencies and musical notes the same thing? _(22:39)_

Closely related, not the same.

- **A frequency** is a physical measurement in Hertz (Hz) — vibrations per second.
- **A musical note** is a name (A, B, C…) assigned to a frequency inside a tuning system. The same note name corresponds to different frequencies in different tunings.

### The standard

In modern Western music, the note A above middle C is set to **440 Hz**. This is a 1939 ISO convention — not a law of nature. Period-instrument ensembles use lower pitches:

- **Baroque pitch** (A = 415 Hz) — for Bach, Handel, Vivaldi.
- **French Baroque** (A = 392 Hz) — even lower.
- **Verdi's A** (A = 432 Hz) — the late-19th-century operatic standard.
- **Chorton** (A ≈ 466 Hz) — the high choir pitch of Bach's Leipzig.

### Octaves and harmonics

Doubling a frequency yields the same note an octave higher: 220 Hz, 440 Hz, 880 Hz are all the note A. A musical note from a real instrument is never a single sine — it is a fundamental plus a series of overtones. The overtones are what makes a cello sound like a cello and not like a tone generator.

### What this means for SoulRadio

The Solfeggio numbers (174, 285, 396, 417, 528, 639, 741, 852, 963) are *frequencies*, not notes. Most pre-electronic recordings cited in [FREQUENCIES.md](FREQUENCIES.md) were performed at A = 440, A = 432, or A = 415. None of them are "tuned to 528 Hz" in the modern Solfeggio sense. Claims that *Canon in D* or *Cello Suite No. 1* are "naturally" at a Solfeggio frequency are retrofitted.

What is true: the pieces share *modal character, tempo, and acoustic intention* with the corresponding Solfeggio tone. Family resemblance, not numerology.

---

## Q: pure tone, or music tuned to the frequency? _(22:42)_

Both are practiced in the surrounding tradition. SoulRadio chose music.

### The pure-tone path

A steady sine wave from an oscillator, held for the length of a session.

- **Used for** focused meditation, signal-generator work, brief practice.
- **Experience** the ear adjusts to the absence of variation; the result can be either "transparent" or "fatiguing" depending on the listener and the duration.

### The musical path

A composition whose modal centre, tempo, or acoustic geometry matches the spirit of the tone.

- **Used for** long-form listening, sleep, ambient presence in a room.
- **Experience** the ear stays curious; the body recognises the piece as music, not as instrumentation.

### Why SoulRadio chose music

The pure tone is honest about what the modern Solfeggio set is — a number. The music is honest about what the *tradition behind the number* is — a thousand-year practice of letting a particular kind of sound shape a room.

A Bach cello prelude in G major and a 174 Hz drone are not the same sound. They are the same **request** to the listener: settle, ground, lower the shoulders.

That kinship is what the dial pairs. The synthesized sine tones remain in the codebase as a fallback for bands without a bundled recording — but the curated pre-electronic recordings are the primary product.

---

## Q: a 24-hour loop tied to the hour of day? _(22:44)_

Yes — this is the core design idea. A human is not the same person at 7 a.m. and at 11 p.m. The morning wants clarity. The afternoon wants warmth. The night wants to be let go of. (MANIFESTO §1.)

### The schedule

The auto loop maps each hour of the day to a band. The current schedule, kept in lock-step between [FREQUENCIES.md](FREQUENCIES.md), [Frequency.kt](app/src/main/java/com/soulradio/soulradio/Frequency.kt), and the test that pins it ([FrequenciesTest.kt](app/src/test/java/com/soulradio/soulradio/FrequenciesTest.kt)):

| Hour band     | Tone     | Intention                         |
|---------------|----------|-----------------------------------|
| 06:00–09:00   | 396 Hz   | Releasing the night               |
| 09:00–12:00   | 741 Hz   | Clearing the channel              |
| 12:00–16:00   | 528 Hz   | Warmth, gathering, midday         |
| 16:00–18:00   | 639 Hz   | Connection, table, conversation   |
| 18:00–20:00   | 417 Hz   | Setting down the day              |
| 20:00–22:00   | 285 Hz   | Slowing the body                  |
| 22:00–23:00   | 174 Hz   | Anchoring, low and warm           |
| 23:00–06:00   | 7.83 Hz  | Held by the stone of the abbey    |

**852 Hz** and **963 Hz** are reserved for the dial — chosen, not auto-played. The upper register of the tradition is something a listener reaches for deliberately.

### Why these times

- **Morning (396)** — the dawn office of the monastic Divine Office sat in this register. The day began with a clearing.
- **Late morning (741)** — the head wants articulate, transparent music when work begins. Goldberg Aria, Couperin, Scarlatti.
- **Midday (528)** — the longest stretch. Bach's *Jesu, Joy of Man's Desiring*, Pachelbel's Canon, Vivaldi's *Spring*. The "warm" canon.
- **Late afternoon (639)** — meals, conversation, reunion. Renaissance polyphony was *built on* the principle of independent voices that agree.
- **Early evening (417)** — the transition tone. Allegri's *Miserere*, Victoria's *O vos omnes*. Music written for the act of letting go.
- **Evening (285)** — the slow turn. Tallis's *Spem in Alium*, the *Air on the G String*. Pieces that breathe at the speed of a sleeping body.
- **Late evening (174)** — the ground note. Gregorian chant in low Dorian. The floor a room is built on.
- **Night (7.83)** — the Schumann band. Until a true sub-audible underlay layer ships, the night band stands in for the *room* it would have ridden inside: a Gregorian chant recorded inside a 12th-century Romanesque abbey, where the stone gives the recording an 8-second tail.

### Implementation notes

- **Crossfades, not cuts.** A jump from 963 Hz to 174 Hz is jarring; a 700 ms crossfade is not.
- **Low volume.** The loop is wallpaper. If it interrupts your thinking, it has failed.
- **Speaker over headphones** for ambient use. Bluetooth is fine; network casting is not — see [docs/output.md](docs/output.md).

---

## Q: is there a reference for each frequency from before electronic music? _(22:57)_

This is the question that turned the project. The original answer offered Spotify playlists from "Hz Meditator," "Miracle Tones," "Emiliano Bruguera" — the modern electronic-Solfeggio aesthetic. SoulRadio went the other way.

The catalogue of pre-electronic pairings — Gregorian chant, Bach, Tallis, Hildegard, Victoria, Palestrina, Mozart, Debussy — is in [FREQUENCIES.md](FREQUENCIES.md). The licensing rule for any bundled recording (public domain, CC0, CC BY, or CC BY-SA only) is in [docs/licensing.md](docs/licensing.md). The actual bundle is in [CREDITS.md](CREDITS.md).

The short version of the pivot:

- **The Solfeggio frequencies are recent** (Joseph Puleo, 1990s). The numbers are arbitrary in the sense that no one was tuning to them before 1990.
- **The intentions behind the frequencies are not recent.** The dawn office, the funeral motet, the wedding canon, the cradle song — these have been doing the same emotional work for a thousand years.
- **The honest move** is to play the older music for the older intention, and let the number be a *label* on the room, not a claim about the sound.

A Bach *Cello Suite* prelude is not "tuned to 174 Hz." But it lives in the same register of *settling, grounding, lowering the shoulders*. That kinship is the basis on which SoulRadio pairs them. Not numerology. Family resemblance.

---

## Q: name the app. _(23:06)_

The brainstorm produced four directions:

- **Modern / tech-focused** — Freq24, VibeCycle, Syncro, LoopFlow.
- **Spiritual / abstract** — Oneness, Resonant, Etheric, AuraLoop.
- **Practical / descriptive** — GodFreq, ZenDial, The Healing Scale, Constant Ohm.
- **Compound** — FrequencyWallpaper, DayTune, SoulSync.

None of these landed. The name **SoulRadio** came from outside the list — from the manifesto's own framing. The app is a *radio*, not a player. You do not curate it; you tune it on. And the receiver is the *soul* — the manifesto's word for the part of a person that the ear lets in. (MANIFESTO §6: *"the body is the receiver."*)

The name is a promise: a station, not a playlist. A presence, not a feature.

---

## Q: at what frequency does a church choir sing? _(23:32)_

A choir is a *range*, not a frequency — and historically, it was tuned to whatever pitch the local organ was at.

### Historical tuning standards

- **Modern (A = 440 Hz)** — the 1939 ISO convention. Most contemporary choirs.
- **Verdi's A (A = 432 Hz)** — the late-19th-century operatic standard. Still used by historically-informed performances of late-Romantic repertoire.
- **Baroque pitch (A = 415 Hz)** — about a semitone below modern. Used for Bach, Handel, Vivaldi by period-instrument ensembles.
- **French Baroque (A = 392 Hz)** — even lower; used for Lully, Couperin, Rameau.
- **Chorton (A ≈ 466 Hz)** — the high choir pitch of north German organ lofts. Bach's Leipzig.

### Vocal ranges

A four-part choir spans roughly **80 Hz to 1100 Hz** — bass to soprano, fundamental frequencies. The full sound (with overtones) extends well above that.

### What this means for SoulRadio

When the catalogue picks a Renaissance or Baroque recording, we prefer historically-informed performances at A = 415 or A = 432 over modern A = 440 — the lower tuning tilts the room ~16–32 cents, a difference the body notices without being able to name. See [FREQUENCIES.md §432](FREQUENCIES.md).

---

## A note on the word *solfeggio*

The modern Solfeggio set is recent (1990s). The *word* is much older. Around **1025 AD**, Guido of Arezzo taught his choirs to read pitches by singing the syllables **Ut, Re, Mi, Fa, Sol, La** — drawn from the opening syllables of each line of the hymn ***Ut queant laxis*** (Hymn to St. John the Baptist):

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

## What this document is, and is not

- It **is** the seed conversation, curated. The questions and the order are preserved because the order is the argument.
- It **is not** a science paper. The folkloric claims are presented as folklore — not as physiology.
- It **is not** a substitute for [MANIFESTO.md](MANIFESTO.md) (the rules) or [FREQUENCIES.md](FREQUENCIES.md) (the catalogue). If those documents disagree with this one, they win.

Tune in. Or better — don't. Just leave it on.

— SoulRadio
