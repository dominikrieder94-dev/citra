# HANDOVER

## 2026-03-17
- Verified state:
  - `externals/libyuv` audit result: the preserved snapshot `0650e25412d6c47724bedac775835d661603d0a8` is effectively the older superproject-pinned upstream commit `5b3351bd07e83f9f9a4cb6629561331ecdb7c546`, with only five executable-bit-only mode changes on helper scripts.
  - Practical implication: `libyuv` is not a meaningful custom fork and should be straightforward to normalize early in the externals cleanup.
  - Preservation commits now exist for the remaining dirty external repos:
    - `externals/libressl` at `ab327f02cd682101dd3af930b99e6ca40602e1ec`
    - `externals/libyuv` at `0650e25412d6c47724bedac775835d661603d0a8`
    - `externals/teakra` at `be37f163e407f193dbe3394574554878da87285e`
  - `externals/libyuv` still prints an unusual leading marker in `git submodule status`, but the nested repo is intact, active in `.git/config`, and its preservation commit is reachable locally.
  - The remaining required step to make these snapshots recoverable from the main repo is a superproject commit that records the updated gitlinks.
- First next steps:
  1. Normalize `externals/libyuv` first; it appears reducible to the old pinned upstream commit plus at most mode-only helper-script changes.
  2. After `libyuv`, continue the externals classification with `dynarmic`, `soundtouch`, and `teakra`.

## 2026-03-16
- Verified state:
  - The running settings dialog now shows an `Auto Fit` quick action on `Top Layout Size`. It computes the live Android max-fill slider value natively from the current framebuffer size and sets the slider to the exact threshold where the secondary screen fills the remaining width beside the full-height primary screen.
  - Android `Large Screen (Top Aligned)` now has a two-stage Android-specific solver:
    - while the requested secondary screen still fits beside the full-height primary, the primary remains identical to Android `Large Screen`
    - once that width saturates, both screens are recomputed together so the secondary can keep growing and the primary shrinks proportionally
  - Android `Large Screen (Top Aligned)` now uses `LargeFrameLayoutTopAndroid()` instead of the shared top-layout helper, so its primary screen matches Android `Large Screen` exactly and fills the same vertical extent.
  - In Android `Large Screen (Top Aligned)`, the slider now scales only the secondary screen, which is top-aligned inside the remaining width to the right of the full-height primary screen.
  - The emulator activity already runs fullscreen with `windowLayoutInDisplayCutoutMode=shortEdges`; the previously visible right-side border on Android `Large Screen` was a layout-placement issue rather than an Android surface-size issue.
  - `EmulationActivity` now explicitly disables decor fitting and reapplies cutout mode before attaching the emulation view so the `SurfaceView` itself can extend into cutout space.
  - Android now applies a post-layout right flush for `Large Screen` and `Large Screen (Top Aligned)` and skips the usual left safe-inset push for those layouts, so they can span the full physical width including the camera-hole side.
  - Android `Large Screen` now uses `LargeFrameLayoutAndroid()`, which keeps the primary screen at maximum height and sizes the secondary screen from the remaining width instead of using the legacy fixed-quarter secondary size.
  - `org.citra.emu.debug` rebuilt successfully from the current `fix/make-build-possible-again` worktree on March 16, 2026.
  - The debug APK installed successfully to device `R3CXB0SJ5GL`.
  - Source inspection shows the current Android running-settings dialog code already contains:
    - `Large Screen (Top Aligned)` as a running layout option
    - the running large-screen proportion seekbar
    - JNI round-trip plumbing for both values
  - Device-side runtime verification of that dialog is still pending because the connected phone remained on the lock screen during this session.
- First next steps:
  1. Launch a game on the Galaxy S24+ and confirm that tapping `Auto Fit` on `Top Layout Size` jumps to the expected max-fill value.
  2. Confirm that manual slider movement still works below and above the auto-fit value, including the saturated range where the primary screen shrinks proportionally.
  3. If desired later, decide whether the same quick action should also exist in the non-running Android settings UI; the current implementation is intentionally running-dialog only because it relies on live surface dimensions.

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
