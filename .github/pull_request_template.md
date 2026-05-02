## What and why

<!-- One paragraph. Link to the manifesto principle or guide section that motivates it. -->

## Manifesto check

- [ ] No gamification / engagement loops added
- [ ] No analytics, telemetry, or network calls added
- [ ] No medical or health claims in copy
- [ ] If audio added: lossless source + license recorded in `CREDITS.md`
- [ ] If schedule touched: `FREQUENCIES.md`, `Frequencies.forHour()`, and `FrequenciesTest.expectedSchedule` all updated together

## Quality

- [ ] `make check` passes locally
- [ ] No new dependency, OR justified in the description
- [ ] No file over 500 lines
- [ ] No `--no-verify` commits in this branch

## AI-assisted edits (if any)

- [ ] Imports and APIs verified against the codebase (no hallucinated packages)
- [ ] Patterns follow an existing file in the repo
- [ ] Tests added or updated for changed behavior
- [ ] No secrets, hostnames, or PII pasted into prompts
