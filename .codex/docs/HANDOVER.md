# HANDOVER

## 2026-03-17
- Verified state:
  - The superproject already anchors the preserved external snapshots in commit `c567f73a3`, and the externals classification pass is now complete enough to move from read-only auditing into selective cleanup planning.
  - Easy normalization candidates:
    - `externals/fmt`: effectively the old gitlink plus two compile-definition lines and helper-script mode changes
    - `externals/enet`: line-ending-only churn in `enet.dsp`
    - `externals/teakra`: seven executable-bit-only mode changes
    - `externals/nihstro`: only four files differ from the old gitlink
    - `externals/xbyak`: line-ending-only churn in test scripts and docs, irrelevant for Android `arm64-v8a`
  - `externals/libyuv` is now normalized to clean upstream commit `5b3351bd07e83f9f9a4cb6629561331ecdb7c546`. The removed local snapshot commit only carried five executable-bit-only helper-script changes, so this was a gitlink cleanup rather than a source-content change.
  - Medium-risk cleanup candidate:
    - `externals/dynarmic`: preserved snapshot `86f70089e833eeb65956efdfcd2ff1dbb70ace9b` mixes a small real patch set with a large accidental vendoring of dynarmic's nested `externals/*` submodules. The original expected gitlink is not reachable in the current fork history.
  - Heavy manual-review candidates:
    - `externals/boost`: broad local Boost import centered on Asio and Align
    - `externals/soundtouch`: local tree replacement or rollback on top of newer fork head `9ef8458d8561d9471dd20e9619e3be4cfe564796`
    - `externals/libressl`: large local tree replacement on top of newer fork head `88b8e41b71099fabc57813bc06d8bc1aba050a19`
  - Broken preservation caveat:
    - `externals/inih/inih` preservation snapshot `319893ccbe95662983177b589a6cb76f90cc8c65` is an empty-tree commit that deletes the entire upstream `inih` contents, so it is not a safe cleanup baseline.
- First next steps:
  1. Continue with the remaining easy bucket: `externals/fmt`, `externals/enet`, `externals/teakra`, `externals/nihstro`, and `externals/xbyak`.
  2. Repair or replace the broken `externals/inih/inih` preservation snapshot before any submodule cleanup touches it.
  3. For `externals/dynarmic`, remove the accidental nested-submodule vendoring first, then audit the small remaining real patch set.
  4. Decide a target strategy for the heavy drifts in `externals/boost`, `externals/soundtouch`, and `externals/libressl` instead of trying to normalize them opportunistically.

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
