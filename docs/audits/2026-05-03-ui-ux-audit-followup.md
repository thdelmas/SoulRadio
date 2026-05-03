# SoulRadio — UI/UX Audit (follow-up)

_Date: 2026-05-03 · Scope: full radio screen, AUTO behaviour, in-app notes reader, plus the un-pushed work-in-progress on [TrackEngine.kt](../../app/src/main/java/com/soulradio/soulradio/TrackEngine.kt) and [MainActivity.kt](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt) (player-event-driven dial highlight, BackHandler for notes, per-tab scroll state)._

This is a follow-up to [2026-05-03-ui-ux-audit.md](2026-05-03-ui-ux-audit.md). The first audit's ten findings have all been addressed in commits between `869ef3c` and `4cb2c4c`. The notes reader and the 432 companion landed after that audit was written, so they are in scope here for the first time.

---

## TL;DR

Skeleton is right, manifesto fidelity is high. The remaining issues cluster in three places:

1. **Accessibility on the chrome** — the small text affordances (`notes`, `close`) miss the 48 dp tap-target floor and don't visually read as buttons.
2. **The notes reader** — silently drops markdown tables, which means [FREQUENCIES.md](../../FREQUENCIES.md)'s quick-map (the most useful page in the doc) is invisible in-app.
3. **Microcopy & state recovery** — `untuned · recording forthcoming` is engineer-language; the `auto · paused` notice tells the user *what* but not *what to do*.

Nothing structural. All can be fixed inside the existing files without new dependencies.

---

## What changed since the last audit

Concrete improvements visible in the current tree (not all committed yet):

- **Four-state dial nodes** ([Dial.kt:215-243](../../app/src/main/java/com/soulradio/soulradio/Dial.kt#L215-L243)) — selected+untuned now renders as a thicker outline, not a filled disc. Resolves [audit-1 §6](2026-05-03-ui-ux-audit.md).
- **Static title under each dial node** ([Dial.kt:114-122](../../app/src/main/java/com/soulradio/soulradio/Dial.kt#L114-L122)) — replaces the long-press peek; the room is legible at a glance.
- **AUTO defaults on at first launch** ([PlaybackService.kt:216-219](../../app/src/main/java/com/soulradio/soulradio/PlaybackService.kt#L216-L219)) — manifesto's "just leave it on."
- **Single shared `pulse` transition** ([MainActivity.kt:137-146](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L137-L146)) drives the AUTO dot and the playing-node ring in unison.
- **Player-event-driven dial highlight** ([TrackEngine.kt:46-67](../../app/src/main/java/com/soulradio/soulradio/TrackEngine.kt#L46-L67), [MainActivity.kt:115-131](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L115-L131)) — replaces the 60 s poll. Dial reflects the room after activity recreation and at hour boundaries.
- **`BackHandler` for notes** ([MainActivity.kt:71-74](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L71-L74)) — the device gesture returns to the radio.
- **Per-tab scroll state in notes** ([AboutScreen.kt:49-51](../../app/src/main/java/com/soulradio/soulradio/AboutScreen.kt#L49-L51)) — switching tabs returns to the top, not a stale anchor.

---

## Findings

### 1. Tap targets on `notes` and `close` are below 48 dp — accessibility

The `notes` text ([MainActivity.kt:190-201](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L190-L201)) and the `close` text ([AboutScreen.kt:64-73](../../app/src/main/java/com/soulradio/soulradio/AboutScreen.kt#L64-L73)) are clipped to a `CircleShape` with `padding(horizontal = 12.dp, vertical = 8.dp)` around 10–11 sp text. That lands at roughly **30–32 dp** tall — well below the **48 dp** Material/Android accessibility minimum. The dial nodes (98 dp) and AUTO pill are fine.

**Fix:** bump the tap padding (e.g. `vertical = 16.dp`) so the click region clears 48 dp. The text size and visual presence stays the same.

### 2. The `notes` button doesn't read as tappable

10 sp `Light` in `GoldDim` (#8A7228) on black, with `letterSpacing = 2.sp` and no underline / border / glyph ([MainActivity.kt:190-201](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L190-L201)). It reads as a label, not a control. Manifesto §4 says *findable*, not *invisible*.

**Fix:** the lightest possible affordance — a 1 px underline at GoldDim, or a small leading dot (`· notes`), or a dim outlined glyph. Anything that distinguishes "tappable" from "decorative caption."

### 3. Markdown tables are silently dropped — the FREQUENCIES quick-map is invisible

[AboutScreen.kt:136](../../app/src/main/java/com/soulradio/soulradio/AboutScreen.kt#L136) explicitly skips lines starting with `|`. [FREQUENCIES.md](../../FREQUENCIES.md) opens with a quick-map table (the 24-hour schedule at-a-glance) — the most useful single page in the docs. In-app, that page is the lines around the table, with the table itself missing. Users who open notes for orientation get a silent gap.

**Fix:** either render `|`-rows as plain text with the pipe-separated columns preserved, or add a minimal table renderer (header row + body rows). The latter is preferable for legibility but the former is one extra branch in the existing while-loop.

### 4. The "untuned · recording forthcoming" caption is engineer jargon

[MainActivity.kt:348-355](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L348-L355). First-time users don't know what "tuned" means in this app. The word leaked from the codebase (`tunedKeys`, `isTuned`) into the room.

**Fix:** `silent · recording not yet bundled` — same length, same restraint, no jargon.

### 5. The `auto · paused` notice tells the user *what* but not *what to do*

[MainActivity.kt:284-315](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L284-L315). The notice fades in for 2 s when AUTO flips off as a side effect of a dial tap. Good. But a first-time user has no signal that the AUTO pill is the recovery path — the pill itself is unchanged at the moment of the notice.

**Fix (lightest):** when the notice triggers, briefly pulse the AUTO pill once (a single ~700 ms alpha bump) so the eye links the notice to the affordance. No copy change. No CTA.

### 6. Smaller items (documented, not filed)

These are below the threshold for individual issues but worth noting in the audit so the next pass has them:

- **Caption `Crossfade` stacks visibly on rapid taps** ([MainActivity.kt:324-331](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L324-L331)). 700 ms is fine for one tap; three taps in two seconds produces overlapping fades. The intended use discourages dial-flipping, so accept-as-is.
- **The AUTO hour readout is recompose-driven** ([MainActivity.kt:262-263](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L262-L263)) and won't tick at the hour boundary unless something else recomposes. The dial itself updates from player events, so this is cosmetic.
- **`MarkdownBody` is a non-lazy `Column`** ([AboutScreen.kt:120-141](../../app/src/main/java/com/soulradio/soulradio/AboutScreen.kt#L120-L141)) — fine today; revisit if any doc grows past ~500 lines.
- **Haptic fires on deselect** identically to selection ([Dial.kt:101](../../app/src/main/java/com/soulradio/soulradio/Dial.kt#L101)) — minor sensory mismatch, very low priority.
- **`notes` and the AUTO pill share the top row** — on narrow devices they can crowd. Consider moving `notes` to the bottom (next to/below the caption) so the top is single-purpose: "what is the radio doing."

---

## Suggested order

1. Tap targets on `notes` / `close` (accessibility blocker, two-line fix).
2. Render the markdown table (the quick-map is the page worth opening notes for).
3. Notes-button affordance (manifesto §4 — findable in three seconds).
4. "untuned" caption microcopy.
5. AUTO pill pulse on auto-pause.

Everything in §6 is polish for a future pass.
