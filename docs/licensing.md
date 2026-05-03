# Audio licensing

SoulRadio is a free app with no advertising, no premium tier, and no plan to monetise listening. We also have no budget for licensing fees, no legal team to negotiate carve-outs, and no appetite for an obligation that could turn into one. Every recording bundled in the APK has to be **free for commercial distribution, in perpetuity, with no fees and no negotiation** — the same rule whether the project ships through F-Droid today or through a paid store tomorrow.

In practice that narrows the field to four license types. The rest, we don't bundle.

## Licenses we accept

### Public domain

The composition's copyright has expired (or never applied — folk material, US-government works) **and** the recording itself is out of copyright. In the US, sound recordings fixed before 1925 entered the public domain on 1 January 2025 under the [Music Modernization Act](https://www.copyright.gov/music-modernization/). For pre-1925 acoustic-era 78s — Caruso, Chaliapin, Belov/Gayler, Pitman's Musopen Beethoven sonatas — that's the bedrock of the bundle.

**Implication:** No attribution legally required (we still credit the performer in [CREDITS.md](../CREDITS.md) — it's the right thing to do). No share-alike obligation. No restrictions on what we do with the file.

### CC0 1.0 — public-domain dedication

A still-living rightsholder waives all copyright and related rights to the maximum extent the law allows. It is the strongest form of free release a copyright holder can issue. Open Goldberg Variations and Open Well-Tempered Clavier (Kimiko Ishizaka, MuseScore) are CC0.

**Implication:** Treat as public domain. No attribution legally required. We attribute anyway.

**Reference:** [creativecommons.org/publicdomain/zero/1.0/](https://creativecommons.org/publicdomain/zero/1.0/)

### CC BY (Attribution)

Free to use, modify, distribute, and sell, in any context, forever, with one obligation: credit the creator and link to the license.

**Implication:** Bundle freely. [CREDITS.md](../CREDITS.md) and the in-app reader satisfy the attribution requirement. No fees, ever.

**Reference:** [creativecommons.org/licenses/by/3.0/](https://creativecommons.org/licenses/by/3.0/) (any version)

### CC BY-SA (Attribution-ShareAlike)

Same as CC BY, plus a copyleft clause: any derivative work must be released under a CC BY-SA license itself. Wikipedia text and most Wikimedia Commons audio is CC BY-SA. The Vivaldi *Spring* (Harrison/Wichita), Bach Brandenburg 3 (Advent Chamber), and Sant'Antimo chant (Zyance) are CC BY-SA.

**Implication for SoulRadio:** Bundling and looping a CC BY-SA recording does not turn the *app* into a derivative work — the app is software code, the recording is content. We attribute, we link the license, we're done. No fees, ever.

**Implication if anyone re-uses the audio:** any modified version of a CC BY-SA recording must also be CC BY-SA. This propagates outward, not into our codebase.

**Reference:** [creativecommons.org/licenses/by-sa/3.0/](https://creativecommons.org/licenses/by-sa/3.0/) (any version)

## Licenses we refuse

### CC BY-NC (NonCommercial)

Free for non-commercial use only. Any commercial use — and the definition of "commercial" is famously vague — requires negotiation with the rightsholder, which means money or a license carve-out we don't have.

**Why we refuse:** The app might never charge a cent and we still don't want to leave that door open. If SoulRadio ever ships in a context that could be construed as commercial (a paid store listing, a kiosk install at a wellness retreat, a hospital partnership) the NC clause becomes a blocker. Cleaner to never bundle it.

### CC BY-ND (NoDerivatives)

You may distribute the recording, but you may not modify it. Looping a track via `ExoPlayer.REPEAT_MODE_ONE` is unlikely to count as a derivative — it's playback, not editing — but trimming a leading silence, normalising loudness, or transcoding 24-bit to 16-bit FLAC plausibly does. ND introduces a constant low-grade question we don't want to answer track-by-track.

**Why we refuse:** Operational friction. The bundle has to be loop-clean ([CLAUDE.md](../CLAUDE.md)), which sometimes means trimming or transcoding. ND blocks that.

### CC BY-NC-ND

Both above. The most restrictive Creative Commons license. The original 528 Hz Fontana cello prelude was CC BY-NC-ND; that is the entry that triggered this document.

### All-rights-reserved (default copyright)

Anything without an explicit free license. Bundling requires negotiating a paid sync license with the performer (and often the label) per recording, per territory, per platform. We don't have the budget; many of the recordings aren't licensable at any reasonable price; the obligation would compound every time we add a track.

**Why we refuse:** The economics. A single sync license can cost more than the entire app will ever earn. The only sustainable rule is to refuse the category outright.

## The decision tree

For every candidate recording:

1. Is the *composition* in the public domain? (For pre-1900 classical: yes, almost always.)
2. Is the *recording* in the public domain, CC0, CC BY, or CC BY-SA?
   - **Yes** → bundle, attribute in [CREDITS.md](../CREDITS.md), done.
   - **No** → skip. No exceptions, no "but it's such a great recording" workaround.

If the band's intention can't be matched at this license tier, the right answer is to leave the band un-tuned (silent fallback) rather than bundle a recording with future obligations attached.

## Why this matters

Audio licensing is the single largest legal-risk vector for a music app. Every recording bundled is a small contract with the rightsholder; over a hundred tracks, those contracts compound into something neither this maintainer nor any future maintainer wants to audit at 2 a.m. before a release.

The rule is binary on purpose. **PD / CC0 / CC BY / CC BY-SA → in. Anything else → out.** No track is so essential that it justifies importing legal risk that survives the maintainer who imported it.

---

*See also:* [CREDITS.md](../CREDITS.md) · [CLAUDE.md](../CLAUDE.md) · [MANIFESTO.md](../MANIFESTO.md)
