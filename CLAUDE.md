# CLAUDE.md — Working on SoulRadio

_Conventions for any AI assistant (or human collaborator) editing this repo. Short, specific, and binding._

## What this is

An ambient Android radio. Four audio modes: a 24-hour auto-loop tied to the hour of day (the wallpaper); a 9-tap dial of Solfeggio tones and two companions, Verdi 432 and Schumann 7.83 (quick, curated frequency selection); behind a deliberate door, a Radio mode for exploring the wider frequency catalogue (opt-in, never autoplay, never the default surface); and behind another, a Library — the listener's own files, auto-profiled and filed under the dial's bands. Alongside those, two cartography surfaces — Body (a lever map: nervous-system / neurochemical / compositional registers) and Chakra (the Solfeggio→chakra body-centre map) — that are peer-class with the audio modes but carry no audio of their own; see [§ Cartography surfaces](#cartography-surfaces). The point is the **room**, not the app — see [MANIFESTO.md](MANIFESTO.md).

## Reference docs (read before editing)

- [MANIFESTO.md](MANIFESTO.md) — the non-negotiables. Every product/UX decision must trace back here.
- [FREQUENCIES.md](FREQUENCIES.md) — the frequency catalogue and the 24-hour map. **Source of truth** for the auto loop schedule.
- [docs/tunables.md](docs/tunables.md) — the wider landscape of frequencies and tuning systems, with reasons each was or wasn't put on the dial. Read before proposing a new station.
- [ORIGINS.md](ORIGINS.md) — long-form rationale and history.
- [CREDITS.md](CREDITS.md) — audio attributions; update when adding new recordings.

## Non-negotiables (from MANIFESTO.md)

- No gamification, streaks, or engagement loops.
- No advertising, ever. No premium tier hiding a frequency.
- No analytics, telemetry, or sale of listening data.
- No medical/health claims about any frequency.
- Lossless audio when feasible; respect the ear (no jolts, no cuts).
- Radio mode is opt-in only. Never autoplay, never the default surface, never bleeds into the room. The Dial stays at eleven so the room can recede; the Radio is a separate door the listener walks through.
- The user library is opt-in. Default `LibrarySource.APP_ONLY` keeps the loop and dial on the curated catalogue; imports play only when the listener flips the source filter to `MIXED` or `USER_ONLY` from the Library screen. `Frequencies.all` is never mutated at runtime — user files live in [UserTracksStore](app/src/main/java/com/soulradio/soulradio/UserTrack.kt), not in the catalogue.
- Curated pre-electronic recordings (chant, Bach, etc.) are the **preferred** audio — but the rule is "artistic music a curator chose," not "no electronic instrument anywhere." A modern composition that uses synth, percussion, or processed sound as a compositional element is fine if it is musical work, not generic ambient slop. What we keep out: white-noise loops, sine-wave New Age fillers, AI-generated background tracks, and any "wellness" audio that wasn't made as music. Bare synthesized sine tones are a fallback, not the product.

If a change risks any of these, stop and flag it.

## Stack

- **Language:** Kotlin (JVM target 11).
- **UI:** Jetpack Compose + Material3.
- **Audio:** AndroidX Media3 (ExoPlayer + MediaSession). The `PlaybackService` owns the player so audio survives screen lock and shows on the lockscreen / Bluetooth.
- **Build:** Gradle 8 + AGP 8.0.2. `minSdk 24`, `targetSdk 34`.
- **Tests:** JUnit 4 unit tests; see [app/src/test/.../FrequenciesTest.kt](app/src/test/java/com/soulradio/soulradio/FrequenciesTest.kt). One test-only dep: `org.json:json` provides a real implementation in the JVM unit-test runtime — Android's stub `android.jar` throws on `JSONObject` / `JSONArray` calls, and [UserTracksStore](app/src/main/java/com/soulradio/soulradio/UserTrack.kt) round-trip tests need the real one.

## Code conventions

- **Single package:** `com.soulradio.soulradio`. Don't introduce sub-packages unless the file count justifies it.
- **File length:** Hard cap **500 lines** (enforced by `scripts/check-file-length.sh`). Split before nearing the limit.
- **No new dependencies** without flagging in the PR. The dep list is intentionally small.
- **No hardcoded strings in UI** that a translator would need; current strings are intentionally short and English-only — keep that constraint until i18n is on the table.
- **FLAC stays uncompressed in the APK** (`noCompress += ['flac']` in [app/build.gradle](app/build.gradle)). Don't undo this — the decoder needs to seek/loop.
- **Schedule changes go through both** `Frequencies.forHour()` ([Frequency.kt](app/src/main/java/com/soulradio/soulradio/Frequency.kt)) **and** [FREQUENCIES.md](FREQUENCIES.md) **and** the `expectedSchedule` map in [FrequenciesTest.kt](app/src/test/java/com/soulradio/soulradio/FrequenciesTest.kt). The test is the lock; if the markdown changes, the test must change with it.

## Adding a recording

1. Drop the lossless file in `app/src/main/assets/audio/<key>/` (e.g. `audio/528/`). Only the first non-hidden file in the folder is played; the `.gitkeep` stays.
2. Add a `NowPlaying(work, performer)` entry on the `Frequency` in [Frequency.kt](app/src/main/java/com/soulradio/soulradio/Frequency.kt).
3. Add the attribution + license to [CREDITS.md](CREDITS.md). Only **public domain, CC0, CC BY, or CC BY-SA** — see [docs/licensing.md](docs/licensing.md) for the rule and the rejected license tiers (NC, ND, all-rights-reserved).
4. Verify it loops cleanly (no silence gap, no click) on a real device.

The above is the curated catalogue path. Listener-imported recordings (the user library) follow a separate flow — see § The user library below.

## The user library

The listener can import their own audio (SAF picker, `audio/*` mime), and SoulRadio auto-profiles each file at import. The framework — three layers, spectral / temporal / spatial — is documented at [docs/tunables.md § Reading a recording's profile](docs/tunables.md#reading-a-recordings-profile); [AudioProfiler](app/src/main/java/com/soulradio/soulradio/AudioProfiler.kt) implements it. The library is the **fourth mode**: the listener-as-curator surface, parallel to the loop, dial, and Radio.

Architecture:

- **[AudioProfiler.kt](app/src/main/java/com/soulradio/soulradio/AudioProfiler.kt)** — pure-JVM analyzer. PCM in, [BandAssignment](app/src/main/java/com/soulradio/soulradio/AudioProfile.kt) out. Multi-band: a recording can match more than one Solfeggio band (octave coincidences, overtones), or none at all. Empty `matches` is the canonical ancestral case (a 4 Hz drum has signals but no Solfeggio fundamental); the listener files such recordings manually.
- **[Decoder.kt](app/src/main/java/com/soulradio/soulradio/Decoder.kt)** — `MediaCodec` wrapper, `content://` Uri → PCM, capped at 30 s by default. Run on `Dispatchers.IO`.
- **[UserTrack.kt](app/src/main/java/com/soulradio/soulradio/UserTrack.kt)** — the parallel store. Lives next to (never inside) `Frequencies.all`. JSON-persisted in the same `soulradio.state` SharedPreferences as the other single-fact stores. Each track has a `Set<String> assignedBands` so a Tibetan overtone chant can sit on `{"396", "7.83"}` simultaneously. The listener can override the auto-suggestion at any time.
- **[LibrarySource.kt](app/src/main/java/com/soulradio/soulradio/LibrarySource.kt)** — three-way filter: `APP_ONLY` (default, manifesto-aligned), `MIXED` (curated first, then user, in that order), `USER_ONLY` (strict — empty playlist on a band the listener hasn't filed under means silence; the loop's next-hour tick rolls on).
- **[TrackResolver.kt](app/src/main/java/com/soulradio/soulradio/TrackResolver.kt)** — the union point. `urisFor(context, freq)` returns the URI playlist for a band according to `LibrarySource`. Both [PlaybackService.switchTo](app/src/main/java/com/soulradio/soulradio/PlaybackService.kt) (the loop) and [TrackEngine.selectFrequency](app/src/main/java/com/soulradio/soulradio/TrackEngine.kt) (the dial) consult it. The Library screen surfaces it as a three-pill source toggle.
- **[LibraryScreen.kt](app/src/main/java/com/soulradio/soulradio/LibraryScreen.kt)** — Compose UI for import, signal display, and per-track band assignment. Reachable from the `lib` pill in [ModeStrip](app/src/main/java/com/soulradio/soulradio/ModeStrip.kt).

Three things this is **not**:

- **Not an autoplay path.** `LibrarySource.APP_ONLY` is the default and the loop / dial are unaffected until the listener flips the source filter from inside the Library screen.
- **Not a curation event.** `Frequencies.all` is never mutated at runtime — the 9-station structure of the dial is fixed, regardless of what the listener files. The `tunedKeys` "this band has audio" indicator widens with `LibrarySource`: under `APP_ONLY` it reflects the curated catalogue only (user files do not light up curator-empty bands); under `MIXED` / `USER_ONLY` it includes bands the listener has filed audio under, so the dial stays tappable in those modes. The dial *grammar* — nine fixed stations — is the curator's; the *lit set* tracks whatever source the listener has chosen.
- **Not a prescription.** The auto-profile labels (`528 Hz`, `240 BPM`, `pink`, `sub-60 47%`) describe the **file**, never what it will do to a listener — MANIFESTO §5 holds equally for the curated catalogue and the listener's library.

The catalogue's licensing rules ([docs/licensing.md](docs/licensing.md)) apply only to the curated catalogue under `assets/audio/`. What the listener brings into their own library is theirs to file; the radio takes a persistable read permission on the SAF Uri at import and stores no copy.

## Cartography surfaces

Two read-only screens sit alongside the audio modes as peer-class surfaces — same screen-class, same pause-the-player-while-open lifecycle, but they bundle no audio. They are different reading frames for the same field of sound:

- **[BodyScreen.kt](app/src/main/java/com/soulradio/soulradio/BodyScreen.kt)** — *the lever map*. The third reading frame (after Solfeggio name on the dial and Hz in Radio): four registers — Neurological, Autonomic, Neurochemical, Music structure — describing what a listener might *reach for*, with a "where in the radio" pointer at the end of each section. Mirrors the descriptive (never prescriptive) register of [RadioModeScreen](app/src/main/java/com/soulradio/soulradio/RadioModeScreen.kt). Reachable via the `body` pill in [ModeStrip](app/src/main/java/com/soulradio/soulradio/ModeStrip.kt).
- **[ChakraScreen.kt](app/src/main/java/com/soulradio/soulradio/ChakraScreen.kt)** — *the body-centre map*. The fourth reading frame: the seven canonical chakras and where the modern Solfeggio→chakra pairing locates each tone, plus an "outside the seven" section for 174 / 285 / 432 / 7.83. Same status as [FREQUENCIES.md](FREQUENCIES.md)'s **Chakra** field — the tradition's own anatomy, *reported* by the radio, never *endorsed*.

These two are explicitly **not on the strip**. The [ModeStrip](app/src/main/java/com/soulradio/soulradio/ModeStrip.kt) holds five mode pills (`dj`, `dial`, `radio`, `body`, `lib`) plus two utility icons (settings gear, notes book), and is at its width budget on a 360 dp phone. Chakra is reached one click in via a "see also" link at the bottom of [BodyScreen](app/src/main/java/com/soulradio/soulradio/BodyScreen.kt) — same architectural tier, deferred entry path. The dial-smallness rule ("the room recedes when there is space between the things the dial offers") applies equally to the navigation strip.

When adding another reading frame, the test is the same as for the dial in [docs/tunables.md](docs/tunables.md): does this reframe earn its space, or does it crowd the strip? A sixth pill is the same conversation as a twelfth dial station.

## Radio mode catalogue

The wider field exposed in Radio mode (the rows under [RadioModeScreen.kt](app/src/main/java/com/soulradio/soulradio/RadioModeScreen.kt)) is data-driven, not hand-coded into the screen.

- **[Catalogue.kt](app/src/main/java/com/soulradio/soulradio/Catalogue.kt)** — the data model. `CatalogueEntry` (hz, title, group, history, uses, studies, references, usage, optional compositions); `CatalogueGroup` enum for the section headings (reference pitches, Cousto, Schumann harmonics, brainwave bands, entrainment delivery, noise colors, vibroacoustic, named, numerology); `audibleHzFor()` gates the sine-demo-on-tap behaviour to entries whose Hz lands inside [SineDemo.MIN_AUDIBLE_HZ, ∞). Sub-audible and non-numeric rows expand the entry sections only.
- **[CatalogueEntries.kt](app/src/main/java/com/soulradio/soulradio/CatalogueEntries.kt)** — the actual entries. Split off from `Catalogue.kt` so the data file can grow without putting the model file at the 500-line cap.

The five-section structure (history / uses / studies / references / usage) is the contract: Radio is descriptive of practice, never prescriptive of effect. Adding an entry means filling all five — the **studies** field says "no controlled studies" honestly when there are none, and the **references** field names concrete products (Holosync, Brain.fm, Meditative Mind) for biohacker bands or specific recordings for musical bands. Mention in [docs/tunables.md](docs/tunables.md) is required before adding a row — the doc is the gate, the entry is the surface.

## Contribution popup

There is a portfolio-wide spec for the monthly community-building popup (donate / Play review / feedback). SoulRadio implements it with two manifesto-driven deviations from the default:

- **90-day cadence** (vs. ~30 days default) — the radio is meant to recede; the ask spaces wider.
- **Paused-only trigger** — checked once, ~5 s after the screen appears. If the radio is playing (AUTO running, or a tone tapped on), the popup waits for the next session. It never interrupts audio.

The three hard rules are absolute and apply here too: **no Play Billing, no feature gates, no donor-vs-non-donor differentiation.** The only state persisted is `contrib_last_shown_at`. Don't add any logic that pauses the popup after a donation, marks a "supporter" state, or gates anything on payment — that converts the donation into a purchase and violates Play policy. If you're touching [ContributionPopup.kt](app/src/main/java/com/soulradio/soulradio/ContributionPopup.kt) or [ContributionStore.kt](app/src/main/java/com/soulradio/soulradio/ContributionStore.kt), re-read the portfolio guide before changing the model.

To update the donation URL or feedback email, edit the two `private const` lines at the top of [ContributionPopup.kt](app/src/main/java/com/soulradio/soulradio/ContributionPopup.kt).

## Before committing

Run `make check` (or `./scripts/code-quality-check.sh`). It runs the same checks CI does:
file-length cap, `./gradlew lint`, `./gradlew test`. Install the pre-commit hook once with `make hooks-install` so this happens automatically.

If a hook fails, **fix and recommit** — don't `--no-verify`.

## What NOT to add

- Account systems, login, sync, cloud anything.
- Notifications beyond the foreground-service media notification.
- Animations that draw attention to the app.
- Any network call. The app is fully offline by design — don't add a permission for `INTERNET`.
- Google Cast, AirPlay, DLNA, or any other network casting protocol. Bluetooth is the supported "play it on the speaker over there" path — see [docs/output.md](docs/output.md) for the rationale.
- "Helpful" features that turn the radio into an app you have to look at.
