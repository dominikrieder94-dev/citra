# PROGRESS

## 2026-03-15 (repo governance migration)
- Intent: Promote `citra_v2` to the maintained repo with the same agent-operational structure as the retired `citra` checkout.
- Outcome: Created `AGENTS.md`, `.codex/docs/` baseline files, `.codex/docs/projects/README.md`, and a repo-local debugging skill under `.codex/skills/android-device-debugging/`. Captured the current Android recovery state, preservation rules for externals, and reusable Android build/install/logcat workflow.
- Files touched:
  - `AGENTS.md`
  - `.codex/docs/GOAL.md`
  - `.codex/docs/CONCEPT.md`
  - `.codex/docs/TASKS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/DECISIONS.md`
  - `.codex/docs/projects/README.md`
  - `.codex/skills/android-device-debugging/SKILL.md`
  - `.codex/skills/android-device-debugging/agents/openai.yaml`
  - `.codex/skills/android-device-debugging/references/android-debug-workflow.md`

## 2026-03-15 (android recovery checkpoint import)
- Intent: Capture the technically important outcomes from the Android recovery session so future work can continue without rediscovery.
- Outcome: Recorded that the maintained fork now builds a debug APK, installs on a physical Galaxy S24+, boots games, renders again, and performs materially better after cleanup. Also recorded that external dependency state is still preservation-critical and not yet reproducible from gitlinks alone.
- Files touched:
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/DECISIONS.md`
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/PROGRESS.md`
