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
