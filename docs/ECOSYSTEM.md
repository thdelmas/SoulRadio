# Ecosystem

SoulRadio sits among siblings — Bios (sensor hub), W2F (mood/bipolar),
Virgil (solo-living safety) — that together form a small modular
bio-hacking suite. SoulRadio's role in that family is the same as its role
in the home: the wallpaper. Always there, never demanding, sometimes
reached for.

For the suite-wide rule, see Bios's
[`docs/ECOSYSTEM_BOUNDARIES.md`](../../Bios/docs/ECOSYSTEM_BOUNDARIES.md).

## The wallpaper rule applies to integrations too

Everything in [MANIFESTO.md](../MANIFESTO.md) survives an ecosystem.
Specifically:

- **§2** — the auto-loop is wallpaper. A sibling that flips bands every
  ninety seconds because heart-rate ticked up has turned the wallpaper into
  a strobe. Manifesto violation.
- **§4** — access is three seconds. A sibling integration that adds a
  configuration screen, a pairing flow, or a permissions dialog between
  the listener and the tone is a violation by a different door.
- **§5** — we do not lie about science. A sibling saying "Bios measured
  your HRV, here is the healing frequency" makes SoulRadio complicit in
  exactly the claim it refused to make alone.

Integrations are admissible only when they preserve all three. The
default is to refuse; the burden of proof is on the integration.

## Standalone first, always

The 24-hour loop, the dial, the Radio catalogue, the listener's library —
all work with **no sibling installed and no permission granted**. Bios
absent, W2F uninstalled, Virgil never opened: SoulRadio is exactly itself.

Anything we wire is a graceful enhancement that vanishes cleanly when the
sibling is removed.

## What inbound signals are admissible — and how

Each row below is a *candidate*, not a commitment. None ships without
explicit user opt-in.

| Source | Signal | Allowed response | Forbidden response |
|---|---|---|---|
| Bios | HRV / arousal state | **Suggest** a band: show a non-modal hint near the dial ("Bios suggests 174 Hz — tap to play"). Listener taps or ignores. | Auto-switch the auto-loop. Override the active band. Mutate `Frequencies.all`. |
| Bios | Sleep stage = deep sleep | Reduce Schumann underlay volume by one ramp step, no UI feedback. | Switch tracks. Pause/resume. Send a notification. |
| W2F | Circadian phase / drift state | **Suggest** a band (same surface as Bios). Phase=evening might suggest 174 even if dial is at 528. | Force the suggestion. Change the auto-loop schedule. |
| Virgil | Alert dispatched / siren active | **Stop playback**, release MediaSession. The room must yield to the emergency. | Resume automatically after the alert. The listener restarts the radio. |
| W2F | SOS state (hypomania / anhedonia) | **Stop the auto-loop.** Play nothing until the user explicitly resumes. A radio playing through a crisis is wrong. | Switch to a "calming" band. We are not a treatment. |

The pattern across all admissible cases: **suggest, gate, or stop**. Never
*push*. The listener is the only entity that selects what plays, except
when an emergency demands silence.

## The minimum inbound API

When the first inbound integration ships, SoulRadio exposes exactly these
intent actions on `PlaybackService` (signature-perm, not exported to
arbitrary callers):

```kotlin
// Hint surface — no playback change, UI shows an opt-in chip on the dial.
const val ACTION_SUGGEST_BAND = "com.soulradio.SUGGEST_BAND"
// extras: EXTRA_BAND_KEY (string), EXTRA_SOURCE (string, e.g. "bios:hrv")

// Stop surface — used by Virgil during alert, by W2F on SOS.
const val ACTION_REQUEST_STOP = "com.soulradio.REQUEST_STOP"
// extras: EXTRA_SOURCE (string), EXTRA_REASON (string, surfaced to listener)
```

There is no `ACTION_PLAY_BAND`. There is no `ACTION_OVERRIDE_AUTO`. There
is no `ACTION_QUEUE_SEQUENCE`. We considered each and refused each — see
§Forbidden below.

The suggestion surface has a built-in **hysteresis floor**: SoulRadio
ignores a new suggestion from the same source within 5 minutes of the
previous one. A sibling that wants to "guide" by suggesting every few
seconds will be silently rate-limited. This is not configurable.

## What is forbidden

- **Auto-switching by biometric.** No "your HRV dropped, here's 528 Hz"
  unprompted. The listener is not a stimulus-response loop.
- **Guided sequences without explicit listener consent in the SoulRadio
  UI itself.** A sibling cannot start a Virgil-prescribed listening session
  by intent alone. The listener must tap "begin" inside SoulRadio.
- **Reading the listener's biometric data.** SoulRadio does not request
  any health permission. If Bios wants to *send* a suggestion, fine; we
  never *pull*. The radio doesn't know your heart rate. The radio never
  asks.
- **Outbound listening telemetry to siblings.** SoulRadio does not tell
  Bios, W2F, or Virgil what band is playing, what track, or for how long.
  MediaSession state is observable by any media-aware app on Android (this
  is how lockscreens work); we don't add new channels on top.
- **Auto-resume after a Virgil/W2F-requested stop.** When stopped by a
  sibling, SoulRadio stays stopped until the listener returns.

## What outbound integrations exist

One: the standard Android MediaSession. Other apps (Virgil, the
lockscreen, Bluetooth headsets, Wear OS) can observe playing state and
metadata via the platform's normal mechanisms. That's it.

SoulRadio does not broadcast `FREQUENCY_CHANGED` or any custom event,
because the only legitimate consumer of that signal today is *the
listener*, and the listener has the screen.

If a future sibling has a real, listener-facing reason to know the
current band, we will add a single read-only `ACTION_QUERY_STATE`
content URI. We have not added it preemptively. (YAGNI applies to APIs
that are also commitments to stability.)

## The keystore decision

The inbound intents above are protected by **signature-level permission**.
Reading them requires the sibling APK to be signed by the same keystore
as SoulRadio. This is a single decision the suite has to make once.

Until it is made:

- The intent actions above are *reserved names*, not implemented surfaces.
- SoulRadio ships with its current keystore.
- No code under [`app/src/main/java/com/soulradio/`](../app/src/main/java/com/soulradio/)
  imports a sibling contract artifact.

When the decision lands (either "SoulRadio joins the suite keystore" or
"the suite uses a different permission model"), update this section and
build the intent surface against that decision.

## Adding an integration: the gate

Before wiring any sibling-driven feature, answer all five:

1. **Does the listener get a moment to choose?** Suggestion, not push.
2. **Does the auto-loop stay coherent if the sibling is uninstalled
   tomorrow?**
3. **Does this require a new SoulRadio permission?** If yes — refuse.
4. **Does the rate of sibling-driven UI change keep the wallpaper
   wallpaper?** Test it for a week before merging.
5. **Does this integration make a medical claim by composition?** "Bios
   says you're stressed, here's the healing band" is the same lie the
   manifesto refused, redistributed across two apps. Refuse.

## Cross-references

- [MANIFESTO.md](../MANIFESTO.md) — what SoulRadio is for
- [ORIGINS.md](../ORIGINS.md) — why the bands, why the auto-loop
- [FREQUENCIES.md](../FREQUENCIES.md) — the canonical 11-band catalogue
- [Bios/docs/ECOSYSTEM_BOUNDARIES.md](../../Bios/docs/ECOSYSTEM_BOUNDARIES.md) — the suite-wide rule
