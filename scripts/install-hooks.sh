#!/usr/bin/env bash
# Point this clone at the versioned hooks in .githooks/.
# One-shot: re-run safely if you ever clone fresh.

set -euo pipefail

repo_root="$(git rev-parse --show-toplevel)"
cd "$repo_root"

git config core.hooksPath .githooks
chmod +x .githooks/* scripts/*.sh

echo "✓ git hooks installed (core.hooksPath = .githooks)"
echo "  Bypass once with: git commit --no-verify"
