# HANDOVER

## 2026-03-15
- Verified state:
  - `citra_v2` is now the intended maintained fork.
  - Android debug APK builds and installs successfully.
  - Physical-device runtime on Samsung Galaxy S24+ boots and renders again.
  - Performance is materially better after removing heavy diagnostics.
  - The top-aligned large-screen layout transfer is partially verified:
    - it appears in the normal Android settings UI
    - it was built and installed successfully
    - it is not yet wired into the in-game running-settings dialog
  - Externals are still dirty and not yet normalized; see `EXTERNALS_PRESERVATION.md`.
  - Android wrapper scripts were restored under `src/android/`; current CLI build entrypoint is `cmd /c gradlew.bat :app:assembleDebug --stacktrace`.
- Key commits already created on `fix/make-build-possible-again`:
  - `a3dba50df` `android: restore device boot and rendering`
  - `95c16f701` `build: update external bootstrap configuration`
  - `9d90a4ed7` `chore: add local android debug taskfile`
  - `072e17ffb` `docs: record current external preservation state`
  - `8d9e02ab4` `docs: add repo operations scaffold for maintained fork`
- Current uncommitted work:
  - screen-layout transfer files in shared layout code and Android settings UI
  - `src/android/gradlew` and `src/android/gradlew.bat`
  - `Taskfile.yml` and the Android debug workflow reference update
  - local Android build logs in `src/android/build_*.log`
- First next steps:
  1. Finish wiring `Large Screen (Top Aligned)` into the in-game running-settings dialog and verify the slider on device.
  2. Keep externals audit read-only until each dependency is classified.
  3. Decide how to normalize submodules and Android bootstrap without losing the current working runtime.
