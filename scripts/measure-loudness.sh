#!/usr/bin/env bash
# Offline EBU R128 loudness pass over the curated catalogue.
#
# Per-file integrated LUFS (input_i) goes to stdout as JSON; the runtime
# normalization layer uses these to scale player.volume so the listener
# does not hear a track rotation as a loudness step (issue #20,
# Manifesto §6 — volumes that do not fatigue).
#
# Target is -16 LUFS — louder than streaming-service -14 (which assumes
# active listening) and quieter than broadcast -23, in the wallpaper zone.
#
# User-imported library files are not measured; the curated catalogue is
# the only normalization domain.
#
# Usage:
#   scripts/measure-loudness.sh              # write to app/src/main/assets/loudness.json
#   scripts/measure-loudness.sh --print      # write JSON to stdout
#   scripts/measure-loudness.sh --report     # human-readable spread report

set -euo pipefail

TARGET_LUFS=-16.0
PARALLEL=4
ASSET_ROOT="app/src/main/assets/audio"
OUT_PATH="app/src/main/assets/loudness.json"

if [[ ! -d "$ASSET_ROOT" ]]; then
  echo "error: $ASSET_ROOT not found (run from repo root)" >&2
  exit 1
fi

if ! command -v ffmpeg >/dev/null 2>&1; then
  echo "error: ffmpeg not installed" >&2
  exit 1
fi

measure_one() {
  local path="$1"
  local rel="${path#"$ASSET_ROOT"/}"
  local json
  json=$(ffmpeg -hide_banner -nostats -i "$path" \
    -af "loudnorm=I=${TARGET_LUFS}:TP=-1.5:LRA=11:print_format=json" \
    -f null - 2>&1 | awk '/^\{/,/^\}/' | tr -d '\n')
  local input_i
  input_i=$(echo "$json" | sed -n 's/.*"input_i"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p')
  if [[ -z "$input_i" ]]; then
    echo "warn: no input_i for $rel" >&2
    return
  fi
  printf '%s\t%s\n' "$rel" "$input_i"
}

export -f measure_one
export ASSET_ROOT TARGET_LUFS

mode="${1:-write}"

# Collect files, measure in parallel, sort for stable output.
tmp=$(mktemp)
trap 'rm -f "$tmp"' EXIT
find "$ASSET_ROOT" -type f ! -name '.gitkeep' | \
  xargs -P "$PARALLEL" -I{} bash -c 'measure_one "$@"' _ {} | \
  sort >"$tmp"

if [[ "$mode" == "--report" ]]; then
  echo "target: ${TARGET_LUFS} LUFS"
  echo
  printf "%-60s %8s %8s\n" "path" "LUFS" "gain_dB"
  while IFS=$'\t' read -r path lufs; do
    gain_db=$(awk -v t="$TARGET_LUFS" -v l="$lufs" 'BEGIN { printf "%+.1f", t - l }')
    printf "%-60s %8s %8s\n" "$path" "$lufs" "$gain_db"
  done <"$tmp"
  echo
  awk -F$'\t' 'BEGIN { min=999; max=-999 } { if ($2<min) min=$2; if ($2>max) max=$2 } END { printf "spread: %.1f LUFS (%.1f .. %.1f)\n", max-min, min, max }' "$tmp"
  exit 0
fi

# Emit JSON {"<asset-relpath>": <input_i_LUFS>, ...}
{
  echo "{"
  awk -F$'\t' '
    NR==FNR { count++; next }
    {
      printf "  \"%s\": %s%s\n", $1, $2, (FNR==count ? "" : ",")
    }
  ' "$tmp" "$tmp"
  echo "}"
} >"${OUT_PATH}.tmp"

if [[ "$mode" == "--print" ]]; then
  cat "${OUT_PATH}.tmp"
  rm "${OUT_PATH}.tmp"
else
  mv "${OUT_PATH}.tmp" "$OUT_PATH"
  echo "wrote $OUT_PATH ($(wc -l <"$OUT_PATH" | tr -d ' ') lines)"
fi
