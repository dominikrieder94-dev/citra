# PROGRESS

## 2026-03-16 (android top-layout max-fill quick action)
- Intent: Add a quick Android UI action to set the top-layout proportion slider to the max-fill value automatically, then rebuild and deploy to the connected Galaxy S24+.
- Outcome: Added a running-settings `Auto Fit` action for `Top Layout Size`. It calls a new native helper that calculates the exact proportion where the secondary screen just fills the remaining width beside the full-height Android large primary screen, then sets the existing slider to that value. Rebuilt `:app:assembleDebug` successfully and installed the new debug APK to device `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/PROGRESS.md`
  - `src/android/app/src/main/java/org/citra/emu/NativeLibrary.java`
  - `src/android/app/src/main/java/org/citra/emu/ui/RunningSettingDialog.java`
  - `src/android/app/src/main/res/layout/list_item_running_seekbar.xml`
  - `src/android/app/src/main/res/values/strings.xml`
  - `src/android/jni/main_android.cpp`
  - `src/core/frontend/framebuffer_layout.cpp`
  - `src/core/frontend/framebuffer_layout.h`

## 2026-03-16 (android top-layout dynamic ratio scaling)
- Intent: Make Android `Large Screen (Top Aligned)` continue responding above the current slider saturation point by recomputing both screen sizes together, then rebuild and deploy to the connected Galaxy S24+.
- Outcome: Updated `LargeFrameLayoutTopAndroid()` to use a two-stage Android layout solver. It now keeps the current full-height primary screen while the requested secondary screen still fits in the remaining width, then switches to a joint fit that shrinks the primary and keeps growing the secondary while preserving both aspect ratios and using the maximum available display area. Rebuilt `:app:assembleDebug` successfully and installed the new debug APK to device `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/PROGRESS.md`
  - `src/core/frontend/framebuffer_layout.cpp`

## 2026-03-16 (android top-layout full-height primary)
- Intent: Make Android `Large Screen (Top Aligned)` keep the same full-height primary screen as Android `Large Screen`, with the slider only affecting the secondary screen width/size, then rebuild and deploy to the connected Galaxy S24+.
- Outcome: Added `LargeFrameLayoutTopAndroid()` and switched Android `Large Screen (Top Aligned)` to use it. The Android top-aligned layout now keeps the primary screen identical to Android `Large Screen`, fills the same vertical extent, and only top-aligns and scales the secondary screen within the remaining width. Rebuilt `:app:assembleDebug` successfully and installed the new debug APK to device `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/PROGRESS.md`
  - `src/core/frontend/framebuffer_layout.cpp`
  - `src/core/frontend/framebuffer_layout.h`
  - `src/core/frontend/emu_window.cpp`

## 2026-03-16 (android large-layout ignore top inset)
- Intent: Let Android `Large Screen` and `Large Screen (Top Aligned)` ignore the remaining top safe-inset push as well, then rebuild and deploy to the connected Galaxy S24+.
- Outcome: Updated the Android large-layout path to skip top safe-inset translation in addition to the left safe-inset translation. Rebuilt `:app:assembleDebug` successfully and installed the new debug APK to device `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `src/core/frontend/emu_window.cpp`

## 2026-03-16 (android cutout surface expansion)
- Intent: Let the emulation surface itself extend into the cutout side by disabling decor fitting and forcing cutout rendering for the emulation activity, then rebuild and deploy to the connected Galaxy S24+.
- Outcome: Updated `EmulationActivity` to call `WindowCompat.setDecorFitsSystemWindows(getWindow(), false)` and explicitly set cutout mode before attaching the emulation view. Rebuilt `:app:assembleDebug` successfully and installed the new debug APK to device `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `src/android/app/src/main/java/org/citra/emu/ui/EmulationActivity.java`

## 2026-03-16 (android large-screen full-width math)
- Intent: Replace the legacy fixed-quarter Android `Large Screen` sizing with a full-width variant that derives the secondary screen from the remaining width, then rebuild and deploy to the connected Galaxy S24+.
- Outcome: Added `LargeFrameLayoutAndroid()` and switched Android `Large Screen` to use it. The primary screen now stays at maximum height while the secondary screen is sized from the remaining width instead of a fixed quarter scale. Rebuilt `:app:assembleDebug` successfully and installed the new debug APK to device `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `src/core/frontend/framebuffer_layout.cpp`
  - `src/core/frontend/framebuffer_layout.h`
  - `src/core/frontend/emu_window.cpp`

## 2026-03-16 (android large-layout full-width left side)
- Intent: Let Android `Large Screen` and `Large Screen (Top Aligned)` use the full left side as well, including the camera-hole side, then rebuild and deploy to the connected Galaxy S24+.
- Outcome: Updated the Android layout path so `Large Screen` and `Large Screen (Top Aligned)` no longer apply the left safe-inset push and now flush to the full window width. Rebuilt `:app:assembleDebug` successfully and installed the new debug APK to device `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `src/core/frontend/emu_window.cpp`

## 2026-03-16 (android large-layout right flush)
- Intent: Match the original Android app's `Large Screen` right-edge behavior by shifting Android large-style layouts to the usable right edge after layout, then rebuild and deploy to the connected Galaxy S24+.
- Outcome: Confirmed that fullscreen/cutout handling was already enabled and applied an Android-only post-layout shift for `Large Screen` and `Large Screen (Top Aligned)` so their right edge reaches the usable display boundary. Rebuilt `:app:assembleDebug` successfully and installed the new debug APK to device `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `src/core/frontend/emu_window.cpp`

## 2026-03-16 (top-aligned right edge anchor)
- Intent: Keep the current top-aligned size limits but pin the composed layout to the original `Large Screen` layout's right boundary, then rebuild and deploy to the connected Galaxy S24+.
- Outcome: Updated `LargeFrameLayoutTop()` so the composed top-aligned layout now keeps its current size cap and vertical centering, but its right edge is anchored to the original `Large Screen` layout's right boundary. Rebuilt `:app:assembleDebug` successfully and installed the new debug APK to device `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `src/core/frontend/framebuffer_layout.cpp`

## 2026-03-16 (top-aligned vertical centering)
- Intent: Center `Large Screen (Top Aligned)` vertically within the original `Large Screen` layout bounds while keeping the two screens top-aligned relative to each other, then rebuild and deploy to the connected Galaxy S24+.
- Outcome: Updated `LargeFrameLayoutTop()` so the composed top-aligned layout is vertically centered within the original `Large Screen` primary bounds instead of being pinned to the top edge. Rebuilt `:app:assembleDebug` successfully and installed the new debug APK to device `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `src/core/frontend/framebuffer_layout.cpp`

## 2026-03-16 (top-aligned height cap)
- Intent: Constrain `Large Screen (Top Aligned)` to the same vertical envelope as the existing `Large Screen` layout, then rebuild and deploy to the connected Galaxy S24+ for device verification.
- Outcome: Updated `LargeFrameLayoutTop()` to derive both its height limit and its top-left anchor from the existing `Large Screen` layout, so the top-aligned mode now stays inside the same overall bounding box. Rebuilt `:app:assembleDebug` successfully and installed the new debug APK to device `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `src/core/frontend/framebuffer_layout.cpp`

## 2026-03-16 (top-aligned width cap)
- Intent: Constrain `Large Screen (Top Aligned)` to the same horizontal envelope as the existing `Large Screen` layout, then rebuild and deploy to the connected Galaxy S24+ for device verification.
- Outcome: Updated `LargeFrameLayoutTop()` so its scale is capped by the same horizontal width limit used by the existing `Large Screen` layout. Rebuilt `:app:assembleDebug` successfully and installed the new debug APK to device `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `src/core/frontend/framebuffer_layout.cpp`

## 2026-03-16 (running settings layout wiring)
- Intent: Finish wiring `Large Screen (Top Aligned)` and its proportion control into the Android in-game running-settings dialog, then build and verify on the connected Galaxy S24+.
- Outcome: Verified by source inspection that the current worktree already contains running-dialog Java/JNI plumbing for the top-aligned layout option and the large-screen proportion slider. Rebuilt `:app:assembleDebug` successfully and installed the updated debug APK to device `R3CXB0SJ5GL`. Runtime UI verification is still pending because the connected phone remained on the lock screen during this pass.
- Files touched:
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`

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

## 2026-03-16 (android top-layout max-fill quick action)
- Intent: Add a quick Android UI action to set the top-layout proportion slider to the max-fill value automatically, then rebuild and deploy to the connected Galaxy S24+.

## 2026-03-17 (external preservation checkpoint)
- Intent: Preserve the remaining dirty externals in their own repos, then record the resulting submodule SHAs in the superproject so the current working dependency state is recoverable before cleanup.
- Outcome: Created preservation commits in `externals/libressl`, `externals/libyuv`, and `externals/teakra`, then prepared the superproject to record those updated gitlinks. Preservation snapshot SHAs: `libressl`=`ab327f02cd682101dd3af930b99e6ca40602e1ec`, `libyuv`=`0650e25412d6c47724bedac775835d661603d0a8`, `teakra`=`be37f163e407f193dbe3394574554878da87285e`.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `EXTERNALS_PRESERVATION.md`
  - `externals/libressl`
  - `externals/libyuv`
  - `externals/teakra`

## 2026-03-17 (audit libyuv)
- Intent: Audit externals/libyuv against its preserved snapshot and prior base to classify whether the drift is an upstream update, local patch set, or replacement churn before normalization.
- Outcome: Verified that the preserved `libyuv` snapshot `0650e25412d6c47724bedac775835d661603d0a8` is a one-off local commit on top of upstream `30809ff64a9ca5e45f86439c0d474c2d3eef3d05`, but the actual tree content matches the older superproject-expected commit `5b3351bd07e83f9f9a4cb6629561331ecdb7c546` exactly except for five executable-bit-only mode changes on helper scripts. This is not a broad custom fork and should be straightforward to normalize.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`

## 2026-03-17 (audit dynarmic)
- Intent: Audit externals/dynarmic against the preserved snapshot and original expected gitlink to classify whether the drift is an upstream bump, custom patch stack, or unrelated replacement before normalization.
- Outcome: Verified that the preserved `dynarmic` snapshot `86f70089e833eeb65956efdfcd2ff1dbb70ace9b` is a local commit on top of fork head `526227eebe1efff3fb14dbf494b9c5b44c2e9c1f`, not a clean upstream bump. The snapshot contains two layers: (1) a real local patch set touching build integration, fmt compatibility, and register allocation behavior; and (2) a large accidental vendoring of dynarmic's own nested `externals/*` submodules after deleting dynarmic's `.gitmodules`. The original superproject-expected commit `b6be02ea7fae63aa661ad00763ebd295d1348591` is not reachable in the current dynarmic fork history.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`

## 2026-03-17 (audit remaining externals)
- Intent: Audit the remaining preserved externals (`soundtouch`, `teakra`, `boost`, `fmt`, `enet`, `nihstro`, `libressl`, `externals/inih/inih`, and `xbyak`) to classify whether each drift is a clean bump, local patch stack, vendoring accident, or mostly irrelevant for Android normalization.
- Outcome: Classified the remaining externals into normalization buckets. `fmt`, `enet`, `teakra`, `nihstro`, and `xbyak` are near-normalized already and differ from the old gitlinks only by tiny local patches, mode changes, or line-ending churn. `boost` is a broad local dependency import centered on Asio and Align, `soundtouch` and `libressl` are heavy local tree replacements on top of newer fork heads, and `externals/inih/inih` turned out to be the only actually broken preservation snapshot because its local preservation commit deletes the entire upstream tree. The audit is now complete enough to move from read-only classification into selective normalization planning.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`
  - `EXTERNALS_PRESERVATION.md`

## 2026-03-17 (normalize libyuv)
- Intent: Normalize `externals/libyuv` first by replacing the preserved local snapshot gitlink with the clean upstream commit that already matches its content, then record the result in the repo docs.
- Outcome: Rewound `externals/libyuv` from the preserved local snapshot `0650e25412d6c47724bedac775835d661603d0a8` to the clean upstream commit `5b3351bd07e83f9f9a4cb6629561331ecdb7c546`. This drops only the five executable-bit-only helper-script changes identified during audit and leaves the actual source tree aligned with the old superproject expectation. No rebuild was run because the normalized tree content is unchanged for Android-relevant source files.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`
  - `EXTERNALS_PRESERVATION.md`
  - `externals/libyuv`

## 2026-03-17 (normalize mode and line-ending externals)
- Intent: Continue the easy-bucket cleanup by normalizing `externals/enet`, `externals/teakra`, and `externals/xbyak`, whose audited drift was limited to line-ending churn or executable-bit-only mode changes.
- Outcome: Rewound `externals/enet` to `39a72ab1990014eb399cee9d538fd529df99c6a0`, `externals/teakra` to `e6ea0eae656c022d7878ffabc4e016b3e6f0c536`, and `externals/xbyak` to `1de435ed04c8e74775804da944d176baf0ce56e2`. These cleanups drop only line-ending churn or executable-bit-only mode changes and do not alter Android-relevant source behavior.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`
  - `EXTERNALS_PRESERVATION.md`
  - `externals/enet`
  - `externals/teakra`
  - `externals/xbyak`

## 2026-03-17 (test fmt normalization)
- Intent: Test whether `externals/fmt` can also be normalized safely by rewinding it to the old clean upstream commit and validating the Android debug build afterward.
- Outcome: Rewound `externals/fmt` to clean upstream commit `4b8f8fac96a7819f28f4be523ca10a2d5d8aaaf2` and verified that `cmd /c gradlew.bat :app:assembleDebug --stacktrace` still succeeds. The removed local delta was limited to two `FMT_USE_USER_DEFINED_LITERALS=0` compile-definition lines plus helper-script mode churn, and the current Android build tolerates their removal.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`
  - `EXTERNALS_PRESERVATION.md`
  - `externals/fmt`

## 2026-03-17 (test nihstro normalization)
- Intent: Test whether `externals/nihstro` can also be normalized safely by rewinding it to the old clean upstream commit and validating the Android debug build afterward.
- Outcome: Rewinding `externals/nihstro` to `fd69de1a1b960ec296cc67d32257b0f9e2d89ac6` breaks the Android `arm64-v8a` build under the current NDK/libc++ toolchain. The old code specializes `std::make_unsigned` for shader register enums in `include/nihstro/shader_bytecode.h`, which now fails with `-Winvalid-specialization`. Restoring the preserved snapshot `c9af0af155514b5c12a6f2d9e2b10fb98ec66750` returns the Android build to a passing state, so `nihstro` is no longer an "easy" normalization candidate.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`
  - `EXTERNALS_PRESERVATION.md`
  - `externals/nihstro`

## 2026-03-17 (deploy normalized debug apk)
- Intent: Deploy the current Android debug APK after the low-risk external normalizations so device testing can continue from the updated dependency state.
- Outcome: Verified connected device `R3CXB0SJ5GL`, confirmed the current `app-debug.apk` artifact exists for `253b715fc`, and installed it successfully with `adb -s R3CXB0SJ5GL install -r`.
- Files touched:
  - `.codex/docs/PROGRESS.md`

## 2026-03-17 (repair inih submodule)
- Intent: Repair `externals/inih/inih` by removing the broken empty-tree preservation snapshot from the active superproject state and restoring the clean historical gitlink the repo originally expected.
- Outcome: Rewound `externals/inih/inih` from the broken empty-tree commit `319893ccbe95662983177b589a6cb76f90cc8c65` back to the clean historical gitlink `2023872dfffb38b6a98f2c45a0eb25652aaea91f`. This restores the actual upstream source tree under `externals/inih/inih/` and removes the last known broken preservation state from the superproject. `cmd /c gradlew.bat :app:assembleDebug --stacktrace` still succeeds afterward.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`
  - `EXTERNALS_PRESERVATION.md`
  - `externals/inih/inih`
