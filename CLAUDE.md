# CLAUDE.md — Working on SoulRadio

_Conventions for any AI assistant (or human collaborator) editing this repo. Short, specific, and binding._

## What this is

An ambient Android radio: a 24-hour auto-loop tied to the hour of day, plus a 9-tap dial of Solfeggio tones and two companions (Verdi 432, Schumann 7.83). The point is the **room**, not the app — see [MANIFESTO.md](MANIFESTO.md).

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
- Curated pre-electronic recordings (chant, Bach, etc.) are the **preferred** audio — but the rule is "artistic music a curator chose," not "no electronic instrument anywhere." A modern composition that uses synth, percussion, or processed sound as a compositional element is fine if it is musical work, not generic ambient slop. What we keep out: white-noise loops, sine-wave New Age fillers, AI-generated background tracks, and any "wellness" audio that wasn't made as music. Bare synthesized sine tones are a fallback, not the product.

If a change risks any of these, stop and flag it.

## Stack

- **Language:** Kotlin (JVM target 11).
- **UI:** Jetpack Compose + Material3.
- **Audio:** AndroidX Media3 (ExoPlayer + MediaSession). The `PlaybackService` owns the player so audio survives screen lock and shows on the lockscreen / Bluetooth.
- **Build:** Gradle 8 + AGP 8.0.2. `minSdk 24`, `targetSdk 34`.
- **Tests:** JUnit 4 unit tests; see [app/src/test/.../FrequenciesTest.kt](app/src/test/java/com/soulradio/soulradio/FrequenciesTest.kt).

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
