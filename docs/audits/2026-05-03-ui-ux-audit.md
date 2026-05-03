# SoulRadio — UI/UX Audit

_Date: 2026-05-03 · Scope: the single Compose screen in [MainActivity.kt](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt), AUTO behaviour in [PlaybackService.kt](../../app/src/main/java/com/soulradio/soulradio/PlaybackService.kt), and tap-driven playback in [TrackEngine.kt](../../app/src/main/java/com/soulradio/soulradio/TrackEngine.kt)._

This audit assumes the [MANIFESTO.md](../../MANIFESTO.md) constraints as bedrock. Recommendations either *enforce* a manifesto principle better, or remove a friction the manifesto already implies.

---

## TL;DR

The screen is exceptionally close to the manifesto: one column, one font weight, one accent colour, no chrome. Issues are concentrated in three places:

1. **First-run blindness** — the dial speaks Hz, not intention; new users have to tap nine nodes to learn what the room offers.
2. **AUTO discoverability** — the toggle is a tiny lowercase word when off; its relationship to the dial is unexplained.
3. **State-change abruptness** — colour swaps are instant on a screen whose audio is built around 1.5 s fades; the eye gets a harder cut than the ear.

None require new features. All can be addressed inside the existing screen.

---

## Walkthrough (what a first-time user sees)

Cold launch → `MainActivity.onCreate` ([MainActivity.kt:61-71](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L61-L71)) immediately requests `POST_NOTIFICATIONS` on Android 13+ ([MainActivity.kt:73-79](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L73-L79)). The first thing the user sees is a permission dialog. Then black, then:

- A small lowercase word `auto` near the top.
- A 3×3 grid of dim circles labelled `174 285 396 / 417 528 639 / 741 852 963`.
- A 40 %-width hairline divider.
- Two smaller circles `432 / 7.83` with `Verdi's A` and `Schumann` underneath.
- The line `tap a tone · or leave it` at the bottom.

Default AUTO state is `false` ([PlaybackService.kt:209-212](../../app/src/main/java/com/soulradio/soulradio/PlaybackService.kt#L209-L212)), so the radio is silent until the user taps.

---

## What's working

- **Single-screen, single-tap access** — manifesto §4 ("access is sacred / three seconds"). No menus, no onboarding, no settings page. ✅
- **Palette discipline** — black, gold, two dimmed gold variants, three muted greys. Twelve named colours total ([MainActivity.kt:47-54](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L47-L54)). The eye does not have to choose.
- **Tap targets** — 86 dp dial, 64 dp companions; both well above the 48 dp Material minimum.
- **Selected ≠ tuned ≠ idle** is encoded in three distinct visual states ([MainActivity.kt:221-225](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L221-L225)): gold-fill, gold-border, dim-border. Once learned, the dial is fully legible at a glance.
- **AUTO mirror loop** ([MainActivity.kt:96-105](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L96-L105)) lights the current band on the dial — the screen visually agrees with what the ear is hearing.
- **`tap a tone · or leave it`** is exactly the manifesto's voice in seven words. Don't touch.
- **Tap-again-to-stop** ([MainActivity.kt:112-118](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L112-L118)) — symmetry. No "stop" button needed; the same gesture toggles.

---

## Findings

### 1. The dial labels Hz, not intention — high friction at first run

The 3×3 grid shows only the numeric label ([MainActivity.kt:238-243](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L238-L243)). The intention name (`the morning gate`, `the centre`, `the crown`) only appears in the bottom caption *after* a tone is selected ([MainActivity.kt:336-342](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L336-L342)). The companions, by contrast, *do* show their title under the circle ([MainActivity.kt:301-306](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L301-L306)).

A user who doesn't already know that 528 is "the centre" must tap-and-read each of nine nodes to learn what the radio offers. That's nine interactions before the room is legible. Manifesto §4 ("the shortest distance between a person and the tone they need") is not honoured here.

**Options, ordered by intrusiveness:**

- **A.** Add a single-word title under each dial node (matching companions). Risk: 3×3 grid gets visually denser; the "wallpaper" promise weakens.
- **B.** On first launch only, slowly cycle the caption through each tone's title (`174 — the foundation` → `285 — the slow turn` → …) at, say, 4 s each, without playing audio. After one cycle, settle on the empty-state line. The room introduces itself once, never again.
- **C.** Long-press a dial node to reveal its title in the caption without selecting/playing. Discoverable for the curious, invisible for everyone else.

Recommendation: **C** is the most manifesto-aligned (asks nothing, hides everything until reached for). **B** is the boldest. **A** is safe but compromises the look.

### 2. The AUTO control is hard to find when it matters most — when it's off

When AUTO is off, the pill shows lowercase `auto` in `MuteSoft` (#888888), 11 sp, weight Light ([MainActivity.kt:171-187](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L171-L187)). On a black screen above nine bigger gold circles, the eye does not find it. AUTO is the manifesto's headline behaviour ("a radio you do not have to tune"); it should not require the user to know it exists.

Worse, the off-state copy is just the word `auto`. There is no microcopy explaining what tapping it will do. A first-time user who does spot it has no signal that this is the *whole product*.

**Recommendations:**

- Raise the off-state contrast: at minimum bring it to `Gold` (same colour family as the dial), not grey. Keep the weight Light — that's still respectful — but let the affordance live in the same world as the dial.
- Consider an on-by-default behaviour for first launch (`isAutoEnabled` returns `true` for the first session). Manifesto §"the promise" ends with *"Tune in. Or better — don't. Just leave it on."* The current default behaviour is the opposite.
- The off-state could read `auto · off` (still lowercase, still 11 sp) so the toggle's role is self-evident the first time it's seen.

### 3. Visual state changes are abrupt; audio is not

Audio fades at 1500 ms in both directions ([PlaybackService.kt:206](../../app/src/main/java/com/soulradio/soulradio/PlaybackService.kt#L206), [TrackEngine.kt:33](../../app/src/main/java/com/soulradio/soulradio/TrackEngine.kt#L33)). The UI snaps: the gold fill on tap is instant; the caption replaces in zero ms; the AUTO pill flips colour with no transition. The eye gets a harder cut than the ear, which contradicts manifesto §6 ("crossfades that do not jolt").

**Recommendation:** wrap the dial-node colour transition and the caption visibility in `animateColorAsState` / `AnimatedContent` with ~600–800 ms easing — half the audio fade, so the eye leads the ear by a hair instead of slamming ahead of it.

### 4. The notification-permission prompt is the first user interaction

`maybeRequestNotificationPermission` runs unconditionally on launch ([MainActivity.kt:73-79](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L73-L79)). On Android 13+, the user's first interaction with SoulRadio is a system dialog asking for a permission they have no context for. Manifesto: *"a presence … that asks nothing."*

**Recommendation:** request the permission lazily — only when the user enables AUTO or starts a tone. Better still: don't request it at all. The foreground-service media notification will surface anyway when needed; the explicit permission only buys richer notifications, which the app does not generate. Drop the request.

### 5. AUTO ↔ dial relationship is silent

When AUTO is on and the user taps any dial node, AUTO is silently flipped off ([MainActivity.kt:107-119](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L107-L119)). The pill changes from gold-uppercase to grey-lowercase. There is no copy, no animation pause — and the change in pill state is in the user's peripheral vision while their finger is on the dial.

This is *correct behaviour* (manual choice should override the schedule) but it is unexplained. A user who taps a node "just to see" loses AUTO without realising, and may not understand why the next morning's tone doesn't change at the hour.

**Recommendation:** when AUTO turns off as a *side effect* of a dial tap, briefly (~2 s) show a caption line above the title: `auto paused`. No CTA, no button — just a label that lives long enough to be noticed and then dissolves. Re-enabling AUTO is one tap on the pill.

### 6. Untuned tones are silently inert

A tap on an untuned dial node selects it visually and shows `untuned · recording forthcoming` in the caption ([MainActivity.kt:344-350](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L344-L350)). The dial node also goes full gold — visually identical to a tuned, playing tone. The only signal that "no audio is playing" is the caption text, which sits at the bottom of the screen.

Today this is moot — every dial slot has audio. But [432 Hz is untuned](../../app/src/main/java/com/soulradio/soulradio/Frequency.kt#L85) and is right there on the companion row. Tapping it produces a fully-gold circle, the title `Verdi's A`, and… silence. A user reasonably wonders if their volume is broken.

**Recommendation:** when a frequency is untuned, the selected state should differ — e.g., a gold *outline* fill (border thickened, interior still dark) rather than a solid gold disc. The caption already does the verbal work; the dial should not visually lie.

### 7. Caption content is order-dependent

When tuned, the caption renders title → work → performer in three Text blocks with two Spacers ([MainActivity.kt:336-366](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L336-L366)). The work and performer can be quite long (`Bach · Brandenburg Concerto No. 3, BWV 1048 — Allegro` is 50 chars). At small screen widths this wraps unpredictably and the caption block grows tall, pushing the layout's `Spacer(weight)` distribution.

**Recommendation:** cap caption text to one line each with `softWrap = false` and `overflow = TextOverflow.Ellipsis`, or constrain max width to ~80 % of the column. Reserve a fixed minimum height for the caption region so that selecting a tone does not jiggle the dial vertically.

### 8. No landscape / tablet handling

The layout is a single `Column` with `padding(horizontal = 24.dp)` and weighted spacers ([MainActivity.kt:121-167](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L121-L167)). On a tablet or in landscape, the 3×3 dial floats in the centre with vast black margins; on landscape phones, the vertical weighted spacers collapse and the dial is squashed against the divider.

**Recommendation:** clamp the column to a max width (~480 dp) and centre it. For landscape, consider a `BoxWithConstraints` switch to a 9-circle row + companions on the side, *or* simply lock orientation to portrait in the manifest — manifesto §"frequency is wallpaper" implies the device is mostly idle, and a portrait-only ambient app is a reasonable constraint to declare.

### 9. Colour contrast on dim states is borderline

Quick check against the dimmed states ([MainActivity.kt:47-54](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L47-L54)):

- `GoldDim` (#6E5A1C) on `SlateDeep` (#0E0E0E) — idle dial node text. Contrast ≈ 3.7:1. Below WCAG AA (4.5:1) for normal text, above AA for large text (≥18 sp). The dial labels are 20 sp, so it passes — but only just.
- `Mute` (#555555) on `Bg` (#000000) — empty-state caption. Contrast ≈ 3.0:1. The caption text is 11 sp, so this fails AA for normal text. It is *intentional* (the manifesto wants quiet) but worth knowing.

The manifesto does not invoke WCAG. But the design's "barely there" idle state may be illegible to users on cheap LCDs in daylight, which is precisely the kitchen-counter ambient context the radio is for.

**Recommendation:** lift `Mute` to roughly #707070 and `GoldDim` to roughly #8A7228. The visual hierarchy is unchanged; the text becomes findable in sun.

### 10. Minor

- **First-launch silence is unexplained** — the empty caption `tap a tone · or leave it` is lovely but a brand-new user may not realise *no audio is currently playing*. Consider: if AUTO is off and no tone is selected, append a near-invisible second line `(silent)` or shift the AUTO pill to be the visually loudest element. (Likely subsumed by recommendation 2.)
- **No haptic feedback on tap** — a single soft `HapticFeedbackConstants.CONTEXT_CLICK` on each node tap would confirm the gesture during the 1.5 s audio fade-in without breaking the silence.
- **Companion title typography** is 10 sp with 1 sp letter-spacing ([MainActivity.kt:301-306](../../app/src/main/java/com/soulradio/soulradio/MainActivity.kt#L301-L306)) — readable, but smaller than the empty-state caption (11 sp). If finding 1 is implemented for the dial, harmonise sizes across both rows.
- **`Divider()` is 1 dp at #222222** — invisible on AMOLED in a dim room. Not necessarily wrong (the divider is structural, not decorative). Worth verifying on hardware.

---

## What to NOT do, despite the temptation

Every "improvement" below would violate the manifesto. Listing them so they don't get suggested in code review.

- A welcome / onboarding screen explaining the dial. The manifesto forbids this in spirit (§4).
- A volume slider in-app. System volume + lockscreen control are sufficient and the radio is not a foreground task.
- A "now playing" timeline / scrub bar. The radio is not a player.
- A favourites / pin list. That is a streak in costume.
- A history of what AUTO played. Telemetry by another name.
- Animated visualisations of the audio. The manifesto forbids "animations that draw attention to the app" (CLAUDE.md "What NOT to add").

---

## Suggested order of work

If only some of these land:

1. Drop or defer the notification permission request (#4). _One-line change, immediate UX win._
2. Fix the AUTO off-state visibility and copy (#2). _Largest first-run win for least risk._
3. Animate dial / caption colour transitions (#3). _The eye stops jolting._
4. Visually distinguish untuned-but-selected (#6). _Prevents "is my phone broken" confusion at 432._
5. Surface tone titles via long-press (#1). _Manifesto-aligned discovery._
6. Caption layout stabilisation (#7) and contrast lifts (#9). _Polish._
7. Landscape / tablet handling (#8). _Or declare portrait-only and move on._

Items 1–4 are edits inside the existing files; none introduce new dependencies.
