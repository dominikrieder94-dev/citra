# PROGRESS

## 2026-03-18 (stop tracking local agents instructions)
- Intent: Keep `AGENTS.md` as a machine-local helper file instead of a tracked repo file by removing it from git tracking and adding it to `.gitignore`.
- Outcome: Added `AGENTS.md` to `.gitignore` and removed the file from git tracking with `git rm --cached AGENTS.md`, leaving the local file in place for this checkout. Files touched: `.codex/docs/PROGRESS.md`, `.gitignore`, `AGENTS.md`.

## 2026-03-18 (android custom sdmc directory selection)
- Intent: Extend the Android storage override work from save states to the emulated SD card root so existing `Nintendo 3DS/...` save data can be reused, while also handling the case where the user points at a higher parent directory or an empty folder meant to become a new SDMC root.
- Outcome: Added an Android `SDMC Folder` setting alongside the existing `Save States Folder` entry and generalized the settings UI so both storage paths support manual edit, browse, and reset-to-default behavior. `CitraDirectory` now restores a saved `sdmc_path` on startup, resolves either the SDMC root itself, the `Nintendo 3DS` folder, or a higher parent folder containing it, and falls back to treating the selected folder as a new SDMC root when no existing `Nintendo 3DS` layout is found. The JNI bridge now applies the override through `FileUtil::UserPath::SDMCDir`, and custom SDMC roots intentionally skip bundled `sdmc` asset seeding so an existing shared folder is not polluted with starter content. Verified with `task build-debug-apk`, `task install-debug-apk DEVICE_SERIAL=R3CXB0SJ5GL`, and a startup smoke check on `org.citra.emu.debug`. Current device config still contains an older `states_path` override pointing at `/storage/emulated/0/citra-emu/sdmc`, which should be cleared or corrected during user testing. Recorded the SDMC heuristic and Citra-style path constraint in `.codex/docs/INSIGHTS.md`, logged the behavior choice in `.codex/docs/DECISIONS.md`, and updated `.codex/docs/TASKS.md` to move broader storage relocation into a remaining follow-up. Files touched: `.codex/docs/DECISIONS.md`, `.codex/docs/INSIGHTS.md`, `.codex/docs/PROGRESS.md`, `.codex/docs/TASKS.md`, `src/android/app/src/main/java/org/citra/emu/NativeLibrary.java`, `src/android/app/src/main/java/org/citra/emu/settings/SettingsActivity.java`, `src/android/app/src/main/java/org/citra/emu/settings/SettingsAdapter.java`, `src/android/app/src/main/java/org/citra/emu/settings/SettingsFile.java`, `src/android/app/src/main/java/org/citra/emu/settings/SettingsFragment.java`, `src/android/app/src/main/java/org/citra/emu/utils/CitraDirectory.java`, `src/android/app/src/main/res/values/strings.xml`, `src/android/jni/main_android.cpp`.

## 2026-03-18 (android custom save-state directory selection)
- Intent: Let Android users point the emulator at an existing save-state folder without relocating the whole Citra user directory, so previously created states in another on-device folder can be reused from this fork.
- Outcome: Added an Android-only save-state folder override that rewires `FileUtil::UserPath::StatesDir` while leaving the rest of the user directory unchanged. The Android settings screen now exposes a `Save States Folder` entry under a new `Storage` header; it shows the current path, supports manual editing, offers `Browse` through the existing folder picker, and can reset back to the default app-managed folder. `CitraDirectory` restores the saved path on startup and applies it through a new native `SetStatesPath()` bridge. Verified with `task build-debug-apk` and `task install-debug-apk DEVICE_SERIAL=R3CXB0SJ5GL`, both successful. Recorded the scoped-storage constraint in `.codex/docs/INSIGHTS.md`, the implementation choice in `.codex/docs/DECISIONS.md`, and updated `.codex/docs/TASKS.md` to keep broader storage work as a follow-up instead of an implicit part of this change. Files touched: `.codex/docs/DECISIONS.md`, `.codex/docs/INSIGHTS.md`, `.codex/docs/PROGRESS.md`, `.codex/docs/TASKS.md`, `src/common/file_util.h`, `src/common/file_util.cpp`, `src/android/jni/main_android.cpp`, `src/android/app/src/main/java/org/citra/emu/NativeLibrary.java`, `src/android/app/src/main/java/org/citra/emu/utils/CitraDirectory.java`, `src/android/app/src/main/java/org/citra/emu/settings/SettingsFile.java`, `src/android/app/src/main/java/org/citra/emu/settings/SettingsFragment.java`, `src/android/app/src/main/java/org/citra/emu/settings/SettingsActivity.java`, `src/android/app/src/main/java/org/citra/emu/settings/SettingsAdapter.java`, `src/android/app/src/main/java/org/citra/emu/settings/viewholder/EditorViewHolder.java`, `src/android/app/src/main/res/values/strings.xml`.

## 2026-03-18 (pin layout-settings follow-up scope)
- Intent: Record the owner-requested next-pass screen-layout settings cleanup before switching focus away from layout work, so the UI restructuring goals do not get lost while save-path work proceeds.
- Outcome: Pinned the requested layout-settings follow-up scope in `.codex/docs/TASKS.md`: move screen-layout settings into a more dedicated component, show layout-specific controls dynamically, merge the current large/top-aligned layout family into a clearer combined model, and add per-edge custom padding controls. Files touched: `.codex/docs/PROGRESS.md`, `.codex/docs/TASKS.md`.

## 2026-03-18 (hybrid layout placement options)
- Intent: Add the first two user-facing hybrid layout placement options on Android: one to move the side column left/right and one to swap the stacked order of the side-column screens, while keeping the broader “dynamic per-layout settings UI” cleanup as a follow-up.
- Outcome: Added shared hybrid layout booleans for side-column side and stacked order, extended the hybrid layout math to respect them, and persisted them through Android renderer config, JNI running-settings round-tripping, and the Android settings file model. Android now shows `Side Column on Left` and `Secondary Screen on Top` only when the current layout is `Hybrid Screen` in the standard settings screen and in the running-settings dialog. To support that conditional UI safely, `RunningSettingDialog` now rebuilds the native settings array by stable setting id instead of assuming the visible list order matches the JNI array order. Verified with `task build-debug-apk` and `task install-debug-apk DEVICE_SERIAL=R3CXB0SJ5GL`, both successful. Recorded the save-by-id constraint in `.codex/docs/INSIGHTS.md` and updated `.codex/docs/TASKS.md` for the remaining layout-specific UI work. Files touched: `.codex/docs/INSIGHTS.md`, `.codex/docs/PROGRESS.md`, `.codex/docs/TASKS.md`, `src/core/settings.h`, `src/core/settings.cpp`, `src/core/frontend/framebuffer_layout.h`, `src/core/frontend/framebuffer_layout.cpp`, `src/core/frontend/emu_window.cpp`, `src/android/jni/config/main_settings.h`, `src/android/jni/config/main_settings.cpp`, `src/android/jni/config/config.cpp`, `src/android/jni/main_android.cpp`, `src/android/app/src/main/java/org/citra/emu/settings/SettingsFile.java`, `src/android/app/src/main/java/org/citra/emu/settings/SettingsAdapter.java`, `src/android/app/src/main/java/org/citra/emu/settings/SettingsFragment.java`, `src/android/app/src/main/java/org/citra/emu/ui/RunningSettingDialog.java`, `src/android/app/src/main/res/values/strings.xml`, `src/citra/default_ini.h`, `src/citra/config.cpp`, `src/citra_qt/configuration/config.cpp`.

## 2026-03-18 (hybrid layout first draft)
- Intent: Add a first draft of the new hybrid screen layout mode with fixed default placement, then wire it into the current Android/shared layout selection flow before tackling the additional side/ordering configuration options.
- Outcome: Added a new `HybridScreen` layout mode in shared settings and framebuffer math, extending the frontend layout model to carry one optional extra draw rectangle so the primary 3DS screen can be rendered twice. Wired the mode into `EmuWindow`, the OpenGL Android renderer, the Android settings arrays/running dialog, and the Qt enhancements combo box. Verified the Android path with `task build-debug-apk` and `task install-debug-apk DEVICE_SERIAL=R3CXB0SJ5GL`, both of which succeeded. Recorded the renderer-model constraint in `.codex/docs/INSIGHTS.md` and updated `.codex/docs/TASKS.md` to reflect that only the first hybrid draft is done so far. Files touched: `.codex/docs/INSIGHTS.md`, `.codex/docs/PROGRESS.md`, `.codex/docs/TASKS.md`, `src/core/settings.h`, `src/core/frontend/framebuffer_layout.h`, `src/core/frontend/framebuffer_layout.cpp`, `src/core/frontend/emu_window.cpp`, `src/video_core/renderer_opengl/renderer_opengl.cpp`, `src/android/app/src/main/java/org/citra/emu/ui/RunningSettingDialog.java`, `src/android/app/src/main/res/layout/list_item_running_radio4.xml`, `src/android/app/src/main/res/values/arrays.xml`, `src/android/app/src/main/res/values/strings.xml`, `src/citra_qt/main.cpp`, `src/citra_qt/configuration/configure_enhancements.ui`.

## 2026-03-18 (rename temporary branding to citra bjj)
- Intent: Adjust the temporary Android branding so the launcher label shows `Citra BJJ` instead of only `bjj`, while keeping the current icon and package IDs unchanged.
- Outcome: Updated the shared Android `app_name` and `app_name_version` strings from `bjj` to `Citra BJJ` in the default, Spanish, Russian, and Chinese resource files. Verified the rename with `task build-debug-apk`, which completed successfully. Files touched: `.codex/docs/PROGRESS.md`, `src/android/app/src/main/res/values/strings.xml`, `src/android/app/src/main/res/values-es/strings.xml`, `src/android/app/src/main/res/values-ru/strings.xml`, `src/android/app/src/main/res/values-zh/strings.xml`.

## 2026-03-18 (deploy temporary bjj branding to device)
- Intent: Rebuild and install the newly rebranded Android debug APK on the connected phone so the temporary `bjj` name and blue launcher icon can be verified on-device.
- Outcome: Ran `task deploy-debug-apk DEVICE_SERIAL=R3CXB0SJ5GL` successfully after the temporary branding change. The debug APK rebuilt cleanly and installed to the connected device with `adb install -r -d`, so the `bjj` label and light-blue icon are now available for on-device verification. Files touched: `.codex/docs/PROGRESS.md`.

## 2026-03-18 (temporary android bjj branding)
- Intent: Temporarily rebrand the Android app from `Citra` to `bjj` and switch the launcher icon background from orange to a light blue variant while keeping the current package IDs and runtime behavior unchanged.
- Outcome: Updated the Android app label to `bjj` by switching the application manifest to `@string/app_name` and changing the localized `app_name` / `app_name_version` strings in the default, Spanish, Russian, and Chinese resource files. Changed the adaptive launcher icon background color from orange to light blue in both day and night palettes, and recolored the pre-rendered `ic_citra*.png` launcher icons across all density buckets so older launchers stay visually consistent. Verified the resource changes with `task build-debug-apk`, which completed successfully. Files touched: `.codex/docs/PROGRESS.md`, `src/android/app/src/main/AndroidManifest.xml`, `src/android/app/src/main/res/values/strings.xml`, `src/android/app/src/main/res/values-es/strings.xml`, `src/android/app/src/main/res/values-ru/strings.xml`, `src/android/app/src/main/res/values-zh/strings.xml`, `src/android/app/src/main/res/values/colors.xml`, `src/android/app/src/main/res/values-night/colors.xml`, `src/android/app/src/main/res/mipmap-hdpi/ic_citra.png`, `src/android/app/src/main/res/mipmap-hdpi/ic_citra_round.png`, `src/android/app/src/main/res/mipmap-mdpi/ic_citra.png`, `src/android/app/src/main/res/mipmap-mdpi/ic_citra_round.png`, `src/android/app/src/main/res/mipmap-xhdpi/ic_citra.png`, `src/android/app/src/main/res/mipmap-xhdpi/ic_citra_round.png`, `src/android/app/src/main/res/mipmap-xxhdpi/ic_citra.png`, `src/android/app/src/main/res/mipmap-xxhdpi/ic_citra_round.png`, `src/android/app/src/main/res/mipmap-xxxhdpi/ic_citra.png`, `src/android/app/src/main/res/mipmap-xxxhdpi/ic_citra_round.png`.

## 2026-03-18 (investigate es-de emulator detection)
- Intent: Check why this Android build may not be selectable as the standard Nintendo 3DS emulator in ES-DE by comparing the app's installed package IDs against ES-DE's current Android emulator rules.
- Outcome: Confirmed that ES-DE's Android emulator detection is defined by its bundled `es_find_rules.xml` and `es_systems.xml`, not by generic package scanning. Current ES-DE Android rules recognize `org.citra.emu/.ui.EmulationActivity` as `CITRA-MMJ` and expose it for Nintendo 3DS as `Citra MMJ (Standalone)`, but there is no built-in rule for the repo's debug package ID `org.citra.emu.debug`. The connected phone currently has `org.citra.citra_emu`, `org.citra.emu`, and `org.citra.emu.debug` installed, so ES-DE can only match the release package for the built-in Citra MMJ entry unless a custom `es_find_rules.xml` override is added. Recorded the finding in `.codex/docs/INSIGHTS.md`. Files touched: `.codex/docs/INSIGHTS.md`, `.codex/docs/PROGRESS.md`.

## 2026-03-18 (fresh-clone android build and device deployment check)
- Intent: Re-run the documented Android debug build and deployment flow from this freshly cloned repo against the connected physical phone to confirm the current published state still builds and installs cleanly.
- Outcome: Verified device `R3CXB0SJ5GL` with `adb devices`, then ran `task deploy-debug-apk DEVICE_SERIAL=R3CXB0SJ5GL` successfully from the repo root. The debug APK rebuilt and installed cleanly with `adb install -r -d`. Follow-up smoke check also stayed clean: after clearing `logcat` and launching `org.citra.emu.debug` via `adb shell monkey`, the crash buffer remained empty and the main log showed the app reaching `MainActivity` and `EmulationActivity` with the first `SurfaceView` frame becoming available. Non-blocking warnings observed during build: duplicate Android SDK package locations for NDK `29.0.14206865` and platform `android-31`, plus existing CMake/Gradle deprecation warnings. Files touched: `.codex/docs/PROGRESS.md`.

## 2026-03-18 (revert stray soundtouch ide metadata)
- Intent: Remove the incidental local edit in `externals/soundtouch/source/Android-lib/.project` so the SoundTouch submodule returns to its intended clean upstream state.
- Outcome: Reverted `externals/soundtouch/source/Android-lib/.project` inside the submodule. `git -C externals/soundtouch status --short` is now clean again, confirming the SoundTouch dependency is back to its intended upstream state with no local dirt. Files touched: `.codex/docs/PROGRESS.md`, `externals/soundtouch/source/Android-lib/.project`.

## 2026-03-18 (classify soundtouch dirty state)
- Intent: Check whether the current `externals/soundtouch` dirt is part of the intended patched dependency state or only an incidental local modification, then summarize the clean-state recommendation.
- Outcome: Confirmed that `externals/soundtouch` itself is already at the intended clean upstream gitlink `9ef8458d8561d9471dd20e9619e3be4cfe564796`. The only dirty state is a local edit to `externals/soundtouch/source/Android-lib/.project` that adds Eclipse Buildship Gradle metadata, which is not part of the maintained dependency contract and can be reverted to restore a clean recursive worktree. Files touched: `.codex/docs/PROGRESS.md`.

## 2026-03-18 (explain soundtouch superproject adjustment)
- Intent: Inspect the `HANDOVER.md` note about `externals/soundtouch`, trace the current integration in the fresh-clone repo state, and summarize why the SoundTouch adjustment exists.
- Outcome: Confirmed that the SoundTouch change is an intentional superproject integration fix, not an active code fork inside `externals/soundtouch`. The current repo keeps `externals/soundtouch` pinned to clean upstream commit `9ef8458d8561d9471dd20e9619e3be4cfe564796`, while `externals/CMakeLists.txt` forces `SOUNDSTRETCH=OFF` and propagates `SOUNDTOUCH_INTEGER_SAMPLES` on the `SoundTouch` target so Citra's `audio_core` builds against the same integer-sample ABI as the library. Also confirmed the present dirty submodule state is only a local modification in `externals/soundtouch/source/Android-lib/.project`, separate from the intended integration change. Files touched: `.codex/docs/PROGRESS.md`.

## 2026-03-18 (clean up agents guidance)
- Intent: Align `AGENTS.md` with the current repo documentation layout and refresh any stale status metadata without broadening the governance rules.
- Outcome: Updated `AGENTS.md` so its working-file list now includes the root `EXTERNALS_PRESERVATION.md` file that the repo actively uses for dependency preservation state, and refreshed the embedded status date from `2026-03-15` to `2026-03-18`. Files touched: `.codex/docs/PROGRESS.md`, `AGENTS.md`.

## 2026-03-18 (refine update-docs skill)
- Intent: Review the repo-local `update-docs` skill for clarity, tighten its triggering and workflow guidance, and add any missing skill packaging metadata so future agents can use it reliably.
- Outcome: Rewrote `.codex/skills/update-docs/SKILL.md` to make the trigger conditions explicit, anchor the workflow to the repo's mandatory `AGENTS.md` documentation contract, and clarify which canonical file owns each kind of information. Added the missing `.codex/skills/update-docs/agents/openai.yaml` metadata so the skill is complete and discoverable. The packaged generator/validator scripts were not runnable in this environment because the local Python installation is missing `PyYAML`, so validation for this pass was done by direct file inspection instead. Files touched: `.codex/docs/PROGRESS.md`, `.codex/skills/update-docs/SKILL.md`, `.codex/skills/update-docs/agents/openai.yaml`.

## 2026-03-18 (fresh-clone bootstrap validation)
- Intent: Validate setup, submodule initialization, Android build, and device deployment from the freshly cloned public repo, then capture any repeatable setup steps in Taskfile.yml only after they are proven to work.

- Outcome: Fresh clone bootstrap is now proven from this checkout. git submodule update --init --recursive succeeded against the published submodule forks, src/android/local.properties was created successfully for the default SDK path, 	ask deploy-debug-apk rebuilt the APK and installed it to R3CXB0SJ5GL after changing the install step to allow downgrades with db install -r -d, and Taskfile.yml plus README.md now document the repeatable setup path. Files touched: .gitignore, .env.example, README.md, Taskfile.yml, scripts/ensure-android-local-properties.ps1.

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

## 2026-03-17 (separate dynarmic vendoring)
- Intent: Split the preserved `externals/dynarmic` snapshot into a clean nested-submodule baseline plus the small real local patch set, then rebuild and deploy because this is a higher-risk cleanup.
- Outcome: Rebased `externals/dynarmic` onto clean fork commit `526227eebe1efff3fb14dbf494b9c5b44c2e9c1f`, restored dynarmic's own nested submodules with `git submodule update --init --recursive --force`, re-applied only the small real local patch set, and preserved that cleaned state as local dynarmic commit `384d240134f74ebaed6bd748d9662069dcaf3a68`. `cmd /c gradlew.bat :app:assembleDebug --stacktrace` still succeeds, and the rebuilt debug APK installed successfully to device `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`
  - `EXTERNALS_PRESERVATION.md`
  - `externals/dynarmic`

## 2026-03-17 (confirm dynarmic normalization on device)
- Intent: Close the high-risk `externals/dynarmic` cleanup loop by confirming the rebuilt APK still behaves correctly on the physical Android device after installation.
- Outcome: Device verification succeeded. The user confirmed that the APK built from superproject commit `3b635fa1c` with normalized `externals/dynarmic` still works correctly on `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`

## 2026-03-17 (normalize soundtouch via superproject integration)
- Intent: Replace the preserved local `externals/soundtouch` tree replacement with a clean upstream checkout plus the minimal superproject integration needed for the Android build, then rebuild and deploy because this is another higher-risk cleanup.
- Outcome: Rewound `externals/soundtouch` from preserved local commit `26ea8b97eeb87427e5973cda012bd9074536f576` to clean upstream commit `9ef8458d8561d9471dd20e9619e3be4cfe564796`. The actual required behavior now lives in the superproject: `externals/CMakeLists.txt` forces `SOUNDSTRETCH=OFF` before `add_subdirectory(soundtouch)` and propagates `SOUNDTOUCH_INTEGER_SAMPLES` as an interface definition on the `SoundTouch` target. With those integration fixes in place, `cmd /c gradlew.bat :app:assembleDebug --stacktrace` succeeds again and the rebuilt APK installed successfully to `R3CXB0SJ5GL`. Device confirmation is still pending.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`
  - `EXTERNALS_PRESERVATION.md`
  - `externals/CMakeLists.txt`
  - `externals/soundtouch`

## 2026-03-17 (confirm soundtouch normalization on device)
- Intent: Close the high-risk `externals/soundtouch` cleanup loop by confirming the rebuilt APK still behaves correctly on the physical Android device after installation.
- Outcome: Device verification succeeded. The user confirmed that the APK built from superproject commit `4aec12817` with normalized `externals/soundtouch` still works correctly on `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`

## 2026-03-17 (normalize libressl submodule)
- Intent: Replace the preserved local `externals/libressl` tree replacement with a clean upstream checkout, then rebuild and deploy because this is another higher-risk cleanup even though LibreSSL is outside the active Android runtime path.
- Outcome: Rewound `externals/libressl` from preserved local commit `ab327f02cd682101dd3af930b99e6ca40602e1ec` to clean fork head `88b8e41b71099fabc57813bc06d8bc1aba050a19`. Android `cmd /c gradlew.bat :app:assembleDebug --stacktrace` still succeeds with `ENABLE_WEB_SERVICE=0`, and the rebuilt debug APK installed successfully to `R3CXB0SJ5GL`. Device confirmation is still pending.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`
  - `EXTERNALS_PRESERVATION.md`
  - `externals/libressl`

## 2026-03-17 (confirm libressl normalization on device)
- Intent: Close the higher-risk `externals/libressl` cleanup loop by confirming the rebuilt APK still behaves correctly on the physical Android device after installation.
- Outcome: Device verification succeeded. The user confirmed that the APK built from superproject commit `c8dac8377` with normalized `externals/libressl` still works correctly on `R3CXB0SJ5GL`.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`

## 2026-03-17 (classify boost compatibility snapshot)
- Intent: Test whether `externals/boost` can be normalized by rewinding it from the preserved local snapshot to the older clean gitlink, then rebuild Android to see whether the broad Boost import is actually required.
- Outcome: Rewinding `externals/boost` from preserved snapshot `4cc38a77d7c5bfd0c73e3ceef8ef54e64387a2a2` to old clean commit `36603a1e665e849d29b1735a12c0a51284a10dd0` breaks Android build inside Dynarmic because old Boost `boost/container_hash/hash.hpp` still derives from `std::unary_function`, which current Android libc++ no longer provides. Restoring `4cc38a77d7c5bfd0c73e3ceef8ef54e64387a2a2` returns `cmd /c gradlew.bat :app:assembleDebug --stacktrace` to a passing state. Boost therefore remains an intentional pinned compatibility snapshot for now, not a rewind candidate.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`
  - `EXTERNALS_PRESERVATION.md`

## 2026-03-17 (document reproducible Android bootstrap)
- Intent: Turn the now-cleaned dependency state into an explicit fresh-checkout Android bootstrap path, then validate it with a clean rebuild instead of relying on incremental local state.
- Outcome: Updated `README.md`, `Taskfile.yml`, `.gitignore`, and `.codex/docs/DECISIONS.md` to describe the maintained-fork Android bootstrap contract. Added `task build-debug-apk-clean` and `task deploy-debug-apk`, ignored `src/android/build_*.log`, documented the machine-local `src/android/local.properties` requirement, and revalidated the path with `task build-debug-apk-clean`. Also re-ran `git submodule update --init --recursive --force externals/libyuv`, which cleared the stale `-` marker so `git submodule status --recursive` is now fully clean.
- Files touched:
  - `.codex/docs/DECISIONS.md`
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`
  - `.gitignore`
  - `README.md`
  - `Taskfile.yml`

## 2026-03-17 (deploy bootstrap-validated APK to device)
- Intent: Push the current bootstrap-validated Android debug build to the connected Galaxy S24+ so the documented baseline is also present on the physical target.
- Outcome: Verified `R3CXB0SJ5GL` was connected with `adb devices`, then installed `src/android/app/build/outputs/apk/debug/app-debug.apk` successfully via `task install-debug-apk`.
- Files touched:
  - `.codex/docs/PROGRESS.md`

## 2026-03-17 (minimize nihstro compatibility delta)
- Intent: Reduce `externals/nihstro` from a broad preserved snapshot to the smallest explicit patch set that still compiles under the current Android toolchain, then rebuild and deploy for device verification.
- Outcome: Rebased the active `nihstro` state onto clean upstream `f4d8659f85874de9044d197b1d4a7f8340de1d4b` and kept only a two-file local compatibility patch in `include/nihstro/bit_field.h` and `include/nihstro/shader_bytecode.h`. The reduced patch removes the forbidden `std::make_unsigned` specializations, introduces `BitFieldStorageType` as the extension point, keeps the fallback `return 0;` in `SourceRegister::GetIndex()`, restores Android `:app:assembleDebug` to a passing state, installs successfully to `R3CXB0SJ5GL`, and is confirmed working on device.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/INSIGHTS.md`
  - `.codex/docs/PROGRESS.md`
  - `.codex/docs/TASKS.md`
  - `EXTERNALS_PRESERVATION.md`
  - `externals/nihstro`

## 2026-03-17 (publish active submodule commits to owner forks)
- Intent: Push the currently validated local-only submodule commits for `externals/boost`, `externals/dynarmic`, and `externals/nihstro` to the owner's GitHub forks on stable branch names so the main repo can become fresh-clone reproducible.
- Outcome: Added owner remote `dominik` to `externals/boost`, `externals/dynarmic`, and `externals/nihstro`, created branch `dominik/android-working` in each submodule at the currently validated commit, and pushed those branches successfully:
  - `externals/boost` -> `https://github.com/dominikrieder94-dev/ext-boost`, branch `dominik/android-working`, commit `4cc38a77d7c5bfd0c73e3ceef8ef54e64387a2a2`
  - `externals/dynarmic` -> `https://github.com/dominikrieder94-dev/dynarmic`, branch `dominik/android-working`, commit `384d240134f74ebaed6bd748d9662069dcaf3a68`
  - `externals/nihstro` -> `https://github.com/dominikrieder94-dev/nihstro`, branch `dominik/android-working`, commit `b2291a63a6bdbb095b68dcffde6be3c73887cf17`
- Files touched:
  - `.codex/docs/PROGRESS.md`

## 2026-03-17 (resolve boost fork PR conflict)
- Intent: Merge the owner's current Boost fork `master` into `dominik/android-working`, resolve any conflicts while preserving the validated Android-compatible Boost state, and push the updated branch so the PR can merge cleanly.
- Outcome: A direct cherry-pick of `4cc38a77d7c5bfd0c73e3ceef8ef54e64387a2a2` onto updated Boost `master` and a content merge with `-X ours` both produced broken hybrid Asio trees and Android build failures. The safe resolution was an `ours` strategy merge in `externals/boost`, creating merge commit `e520e425060298281713fcb0e0fc9edd46cafd3b` on `dominik/android-working` with parent `f9b15f6` from fork `master` while keeping the exact validated Android-compatible tree from `4cc38a77d7c5bfd0c73e3ceef8ef54e64387a2a2`. After the merge, `cmd /c gradlew.bat :app:assembleDebug --stacktrace` still passed, and the updated branch was pushed successfully to `https://github.com/dominikrieder94-dev/ext-boost`.
- Files touched:
  - `.codex/docs/PROGRESS.md`
  - `externals/boost`

## 2026-03-18 (post-boost-merge Android validation)
- Intent: Rebuild the Android debug APK after the Boost fork conflict-resolution branch update, install it to the connected device, and confirm the maintained baseline still behaves correctly on hardware.
- Outcome: Verified `R3CXB0SJ5GL` was connected with `adb devices`, rebuilt the current debug APK successfully via `task deploy-debug-apk`, and reinstalled `src/android/app/build/outputs/apk/debug/app-debug.apk` to the phone with `adb install -r`. The build passed without new blockers; remaining validation is the user-facing runtime check on device.
- Files touched:
  - `.codex/docs/PROGRESS.md`


## 2026-03-18 (prepare main repo for published reproducibility)
- Intent: Update the superproject to use the owner's published submodule forks and align the main repository remote state so a fresh clone can fetch the required dependency commits from reachable GitHub remotes.
- Outcome: Verified the pinned submodule SHAs for `externals/boost`, `externals/dynarmic`, and `externals/nihstro` are all reachable from the owner's fork `master` branches, then updated `.gitmodules` to point those three submodules at `dominikrieder94-dev/*` instead of the old upstream remotes. Ran `git submodule sync --recursive` so the local checkout now matches the publishable configuration, and switched the local main-repo remotes so `origin` is `https://github.com/dominikrieder94-dev/citra.git` and the old MMJ base remains available as `upstream`.
- Files touched:
  - `.codex/docs/HANDOVER.md`
  - `.codex/docs/PROGRESS.md`
  - `.gitmodules`


## 2026-03-18 (remove .codex from version control)
- Intent: Stop tracking the local Codex workspace and logs in Git so the published branch can be merged without carrying .codex content forward.
