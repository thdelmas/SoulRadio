#!/usr/bin/env bash
#
# take-screenshots.sh — capture Play / F-Droid store screenshots from a
# connected device. Installs the play-debug APK (visually identical to the
# release build), then drives the ModeStrip to each surface and grabs a PNG.
#
# Navigation is resilient to screen size: it dumps the live view hierarchy
# (uiautomator) and taps the centre of each pill found by its text label,
# rather than hard-coded coordinates.
#
# Usage:  scripts/take-screenshots.sh
# Output: fastlane/metadata/android/en-US/images/phoneScreenshots/<n>_<name>.png
#
# Prereqs: a single authorized adb device, python3, and a built play-debug
# APK (the script builds one if missing).

set -euo pipefail

cd "$(dirname "$0")/.."

PKG="com.soulradio.soulradio"
ACTIVITY="$PKG/.MainActivity"
APK="app/build/outputs/apk/play/debug/app-play-debug.apk"
OUT="fastlane/metadata/android/en-US/images/phoneScreenshots"
SETTLE="${SETTLE:-2.5}"   # seconds to let a transition + audio fade settle

# --- preflight --------------------------------------------------------------

count="$(adb devices | grep -cw "device" || true)"
if [[ "$count" -ne 1 ]]; then
  echo "✗ need exactly one authorized adb device (found $count)." >&2
  echo "  Check the 'Allow USB debugging?' dialog on the phone." >&2
  adb devices >&2
  exit 1
fi

if [[ ! -f "$APK" ]]; then
  echo "→ building play-debug APK…"
  ./gradlew assemblePlayDebug
fi

mkdir -p "$OUT"

# --- helpers ----------------------------------------------------------------

# Tap the centre of the first node whose text== the given label.
tap_label() {
  local label="$1"
  adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1
  adb pull /sdcard/ui.xml /tmp/sr_ui.xml >/dev/null 2>&1
  local coords
  coords="$(python3 - "$label" <<'PY'
import re, sys, xml.etree.ElementTree as ET
label = sys.argv[1]
root = ET.parse("/tmp/sr_ui.xml").getroot()
for n in root.iter("node"):
    if n.attrib.get("text", "").strip().lower() == label.lower():
        m = re.match(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", n.attrib["bounds"])
        x1, y1, x2, y2 = map(int, m.groups())
        print((x1 + x2) // 2, (y1 + y2) // 2)
        break
PY
)"
  if [[ -z "$coords" ]]; then
    echo "  ! pill '$label' not found in hierarchy — skipping" >&2
    return 1
  fi
  echo "  · tap '$label' @ $coords"
  adb shell input tap $coords
}

shot() {
  local n="$1" name="$2"
  local file="$OUT/${n}_${name}.png"
  adb exec-out screencap -p > "$file"
  echo "  ✓ $file"
}

settle() { sleep "$SETTLE"; }

# --- run --------------------------------------------------------------------

echo "→ installing $APK"
adb install -r -g "$APK" >/dev/null

echo "→ launching"
adb shell am force-stop "$PKG" || true
adb shell am start -n "$ACTIVITY" >/dev/null
settle; settle   # first launch: let Compose + the loop settle

# 1. The dial / auto surface as it opens (the wallpaper).
shot 1 dial

# 2. Radio — the wider catalogue behind the door.
if tap_label radio; then settle; shot 2 radio; fi

# 3. Body — the lever map cartography surface.
if tap_label body; then settle; shot 3 body; fi

# 4. Library — the listener-as-curator surface.
if tap_label lib; then settle; shot 4 library; fi

# Back to the dial for a clean final state.
tap_label dial >/dev/null 2>&1 || true

echo "✓ done — screenshots in $OUT"
ls -1 "$OUT"
