# Fastlane metadata

Store-listing metadata in the [Fastlane Supply](https://docs.fastlane.tools/actions/supply/#available-metadata-folders)
layout. This is the **shared source** for the FOSS distribution channels:

- **F-Droid** — `fdroiddata` build metadata can point at this tree.
- **IzzyOnDroid** — reads `fastlane/metadata/android/<locale>/` directly from the repo.
- **Accrescent** — same text content, submitted via its console.

Play Store listing copy lives in [docs/STORE-LISTING.md](../docs/STORE-LISTING.md); keep
the two in sync when the description changes.

## Layout

```
fastlane/metadata/android/en-US/
  title.txt              # app name
  short_description.txt  # ≤ 80 chars (F-Droid "summary")
  full_description.txt   # long description (limited markdown)
  changelogs/<code>.txt  # one file per versionCode (1.txt = versionCode 1)
  images/                # icon, feature graphic, screenshots — see TODO below
```

When `versionCode` bumps, add `changelogs/<newCode>.txt`.

## TODO — images

Screenshots and graphics are not yet committed (they need a real device build).
Drop them here when shot:

```
images/icon.png                       # 512×512
images/featureGraphic.png             # 1024×500
images/phoneScreenshots/1.png … N.png # portrait
```

Shot-list (from [docs/STORE-LISTING.md](../docs/STORE-LISTING.md)):

1. The dial — nine-frequency face, mid-day station active (hero shot).
2. 24-hour loop running — now-playing card with frequency + performer credit.
3. The Radio (door open) — the longer catalogue / labeled exhibits.
4. Solar schedule — settings showing sun-following on.
5. Credits screen — performer + license attribution.
6. (optional) Your library — an imported track filed under a band.
