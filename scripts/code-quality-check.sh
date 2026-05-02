#!/usr/bin/env bash
# Single pipeline run by both the pre-commit hook and CI.
# Keeps "what I run locally" identical to "what CI runs".
#
# Steps:
#   1. File-length cap (500 lines)
#   2. Android Lint   — only when Kotlin / Gradle / manifest files changed
#   3. Unit tests     — only when Kotlin / Gradle files changed
#
# Env:
#   FORCE_FULL=1   skip the change-detection short-circuit, run everything
#   SKIP_GRADLE=1  skip the lint + test steps (e.g. on a machine with no JDK)

set -euo pipefail

repo_root="$(git rev-parse --show-toplevel)"
cd "$repo_root"

step() { printf '\n→ %s\n' "$*"; }

# 1. File length
step "check-file-length (max 500)"
./scripts/check-file-length.sh 500

if [ "${SKIP_GRADLE:-0}" = "1" ]; then
  echo
  echo "SKIP_GRADLE=1 — skipping lint & tests"
  echo "✓ code-quality (partial) ok"
  exit 0
fi

# Detect what changed (staged for pre-commit, full diff otherwise).
if [ "${FORCE_FULL:-0}" = "1" ]; then
  changed_kotlin_or_build=1
  changed_manifest_or_res=1
else
  if git diff --cached --quiet 2>/dev/null && [ -z "${CI:-}" ]; then
    # Nothing staged and not in CI — fall back to "everything in working tree".
    diff_cmd="git ls-files"
  else
    diff_cmd="git diff --cached --name-only --diff-filter=ACMR"
  fi
  changed_files="$($diff_cmd)"
  changed_kotlin_or_build=$(printf '%s\n' "$changed_files" | grep -Ec '\.(kt|kts|gradle)$|^gradle\.properties$' || true)
  changed_manifest_or_res=$(printf '%s\n' "$changed_files" | grep -Ec '(AndroidManifest\.xml|app/src/.*/res/)' || true)
fi

# 2. Lint — runs when manifest, resources, or Kotlin/Gradle changed.
if [ "$changed_kotlin_or_build" -gt 0 ] || [ "$changed_manifest_or_res" -gt 0 ]; then
  step "gradle lint"
  ./gradlew --quiet lint
else
  echo "skip lint  (no Kotlin/Gradle/manifest/res changes staged)"
fi

# 3. Unit tests — runs when Kotlin or Gradle changed.
if [ "$changed_kotlin_or_build" -gt 0 ]; then
  step "gradle test"
  ./gradlew --quiet test
else
  echo "skip test  (no Kotlin/Gradle changes staged)"
fi

echo
echo "✓ code-quality ok"
