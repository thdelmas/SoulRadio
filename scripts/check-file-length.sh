#!/usr/bin/env bash
# Fails if any tracked source file exceeds MAX_LINES.
# Excludes generated/build/asset files and long-form prose.
#
# Usage: scripts/check-file-length.sh [MAX_LINES]
# Default: 500.

set -euo pipefail

MAX_LINES="${1:-500}"

repo_root="$(git rev-parse --show-toplevel)"
cd "$repo_root"

# Tracked files only. Exclude:
#  - build outputs (build/, .gradle/)
#  - IDE / OS noise
#  - bundled audio assets
#  - long-form markdown docs (MANIFESTO/FREQUENCIES/ORIGINS/CREDITS are prose)
#  - the Gradle wrapper (vendored)
exclude_re='^(.*/(build|\.gradle|\.idea)/|app/src/main/assets/|gradle/wrapper/|gradlew(\.bat)?$|.*\.md$)'

mapfile -t files < <(git ls-files | grep -Ev "$exclude_re" || true)

violations=0
for f in "${files[@]}"; do
  [ -f "$f" ] || continue
  lines=$(wc -l < "$f")
  if [ "$lines" -gt "$MAX_LINES" ]; then
    printf '  %s: %d lines (max %d)\n' "$f" "$lines" "$MAX_LINES"
    violations=$((violations + 1))
  fi
done

if [ "$violations" -gt 0 ]; then
  echo
  echo "✗ $violations file(s) exceed $MAX_LINES lines. Split before adding more code."
  exit 1
fi

echo "✓ file-length: all source files under $MAX_LINES lines"
