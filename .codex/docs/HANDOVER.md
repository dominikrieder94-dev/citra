# HANDOVER

## 2026-03-17
- Verified state:
  - The superproject already anchors the preserved external snapshots in commit `c567f73a3`, and the externals classification pass is now complete enough to move from read-only auditing into selective cleanup planning.
  - Low-risk normalizations already completed:
    - `externals/libyuv`
    - `externals/fmt`
    - `externals/enet`
    - `externals/teakra`
    - `externals/xbyak`
  - `externals/libyuv` is now normalized to clean upstream commit `5b3351bd07e83f9f9a4cb6629561331ecdb7c546`. The removed local snapshot commit only carried five executable-bit-only helper-script changes, so this was a gitlink cleanup rather than a source-content change.
  - `externals/fmt` is now normalized to clean upstream commit `4b8f8fac96a7819f28f4be523ca10a2d5d8aaaf2`. Android `:app:assembleDebug` still succeeds after removing the old local compile-definition patch, so that drift was not actually required for the current build.
  - `externals/enet`, `externals/teakra`, and `externals/xbyak` are now normalized to clean upstream commits `39a72ab1990014eb399cee9d538fd529df99c6a0`, `e6ea0eae656c022d7878ffabc4e016b3e6f0c536`, and `1de435ed04c8e74775804da944d176baf0ce56e2` respectively. These rewinds remove only line-ending or executable-bit churn.
  - `externals/inih/inih` is now repaired to clean historical commit `2023872dfffb38b6a98f2c45a0eb25652aaea91f`. The previous superproject gitlink pointed at a broken empty-tree local commit `319893ccbe95662983177b589a6cb76f90cc8c65`.
  - `externals/soundtouch` is now normalized to clean upstream commit `9ef8458d8561d9471dd20e9619e3be4cfe564796`. The required behavior was moved into the superproject instead of kept as a local fork: `externals/CMakeLists.txt` now forces `SOUNDSTRETCH=OFF` and propagates `SOUNDTOUCH_INTEGER_SAMPLES` on the `SoundTouch` target. Android `:app:assembleDebug` passes again and the rebuilt APK installed successfully to `R3CXB0SJ5GL`; device confirmation is still pending.
  - `externals/nihstro` is not a safe rewind candidate. Reverting it to the old gitlink `fd69de1a1b960ec296cc67d32257b0f9e2d89ac6` breaks Android compilation because current libc++ rejects the old `std::make_unsigned` specializations in `include/nihstro/shader_bytecode.h`. The preserved snapshot `c9af0af155514b5c12a6f2d9e2b10fb98ec66750` was restored and the Android build passes again.
  - `externals/dynarmic` is now normalized to local preserved commit `384d240134f74ebaed6bd748d9662069dcaf3a68` on top of clean fork commit `526227eebe1efff3fb14dbf494b9c5b44c2e9c1f`. Dynarmic's own nested submodules are restored, the accidental vendored `externals/*` trees are gone, the small real local patch set remains, Android `:app:assembleDebug` passes, the rebuilt APK installed successfully to `R3CXB0SJ5GL`, and device runtime is confirmed good.
  - Heavy manual-review candidates:
    - `externals/boost`: broad local Boost import centered on Asio and Align
    - `externals/libressl`: large local tree replacement on top of newer fork head `88b8e41b71099fabc57813bc06d8bc1aba050a19`
- First next steps:
  1. Treat `externals/nihstro` as a required local compatibility patch until its changes are separated, documented, or upstreamed.
  2. Wait for device confirmation that the `soundtouch`-normalized APK behaves correctly on `R3CXB0SJ5GL`.
  3. Decide a target strategy for the remaining heavy drifts in `externals/boost` and `externals/libressl`.

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
