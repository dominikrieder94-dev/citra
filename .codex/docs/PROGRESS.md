# PROGRESS

## 2026-03-15 (android wrapper recovery and layout verification)
- Intent: Restore a working Android build entrypoint in `citra_v2`, rebuild after the screen-layout transfer, and deploy the resulting APK to the connected phone for first feature verification.
- Outcome: Restored `src/android/gradlew` and `src/android/gradlew.bat`, corrected `Taskfile.yml` to invoke the wrapper via `cmd /c`, rebuilt the Android debug APK successfully, and installed it to device `R3CXB0SJ5GL`. The transferred screen-layout feature is now present in a buildable/installable APK, pending runtime verification on device.
- Files touched:
  - `.codex/docs/PROGRESS.md`
  - `.codex/skills/android-device-debugging/references/android-debug-workflow.md`
  - `Taskfile.yml`
  - `src/android/gradlew`
  - `src/android/gradlew.bat`

## 2026-03-15 (screen layout transfer)
- Intent: Transfer the previously prototyped top-aligned large-screen layout work from the retired `citra` checkout into `citra_v2` without disturbing the now-working Android runtime baseline.
- Outcome: Transferred the shared top-aligned large-screen layout math, wired `EmuWindow` to select it, restored the Android settings/config keys and slider plumbing for the secondary-screen proportion, and imported the ScreenLayout project notes into `.codex/docs/projects/ScreenLayout/`. CLI build verification is still pending because this checkout does not currently expose a Gradle wrapper or a usable `gradle` binary on `PATH`.
- Files touched:
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/projects/ScreenLayout/GOAL.md`
  - `.codex/docs/projects/ScreenLayout/TASKS.md`
  - `src/core/frontend/framebuffer_layout.h`
  - `src/core/frontend/framebuffer_layout.cpp`
  - `src/core/frontend/emu_window.cpp`
  - `src/android/app/src/main/java/org/citra/emu/settings/SettingsFile.java`
  - `src/android/app/src/main/java/org/citra/emu/settings/SettingsAdapter.java`
  - `src/android/app/src/main/java/org/citra/emu/settings/SettingsFragment.java`
  - `src/android/app/src/main/java/org/citra/emu/settings/view/SliderSetting.java`
  - `src/android/app/src/main/java/org/citra/emu/settings/viewholder/SeekbarViewHolder.java`

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
