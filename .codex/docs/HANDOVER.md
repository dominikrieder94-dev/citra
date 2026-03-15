# HANDOVER

## 2026-03-15
- Verified state:
  - `citra_v2` is now the intended maintained fork.
  - Android debug APK builds and installs successfully.
  - Physical-device runtime on Samsung Galaxy S24+ boots and renders again.
  - Performance is materially better after removing heavy diagnostics.
  - Externals are still dirty and not yet normalized; see `EXTERNALS_PRESERVATION.md`.
- Key commits already created on `fix/make-build-possible-again`:
  - `a3dba50df` `android: restore device boot and rendering`
  - `95c16f701` `build: update external bootstrap configuration`
  - `9d90a4ed7` `chore: add local android debug taskfile`
  - `072e17ffb` `docs: record current external preservation state`
- First next steps:
  1. Keep externals audit read-only until each dependency is classified.
  2. Decide how to normalize submodules without losing the current working runtime.
  3. Continue Android runtime validation and track any remaining lifecycle issues separately from renderer recovery.
