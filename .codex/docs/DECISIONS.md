# DECISIONS

## 2026-03-19 - Replace hardcoded Android release signing with env-driven signing and local fallback
- Context: The repo's Android `release` build still pointed at a dead local MMJ keystore path, so `assembleRelease` failed immediately, blocking any serious move toward a first releasable APK.
- Decision: Stop hardcoding a release keystore in `src/android/app/build.gradle` and support:
  - explicit signing through `ANDROID_RELEASE_*` environment variables
  - a local release-candidate fallback using the debug keystore when `ANDROID_LOCAL_RELEASE_USE_DEBUG_SIGNING=true`
  - unsigned release builds when no signing config is provided
- Rationale:
  - It removes machine-specific legacy state from version control.
  - It gives the repo a real release path without pretending a publish-ready signing key is available in git.
  - It keeps local device validation possible for the non-debug package without forcing immediate key-management work.
- Consequences:
  - `org.citra.emu` can now be built as a release APK again.
  - The debug-key fallback is only suitable for local release candidates; switching to a real public signing key later will produce a different update lineage.
  - Releaseability work still remains: target SDK modernization, release naming clarity, and final signing-key strategy.

## 2026-03-18 - Add Android SDMC root selection as a separate override, not a full user-dir move
- Context: After the save-state folder work, the owner clarified that the higher-priority need is reusing existing in-game save data under the emulated SD card tree, with a picker that can target either the SDMC root directly or an upper folder that contains it.
- Decision: Add a second Android-only storage setting that overrides `FileUtil::UserPath::SDMCDir`, keep it separate from the existing `states` override, and resolve the selected folder heuristically:
  - if the picked folder is `Nintendo 3DS`, use its parent as the SDMC root
  - if the picked folder already contains `Nintendo 3DS`, use it as the SDMC root
  - otherwise, search descendants for `Nintendo 3DS` and use that parent when found
  - if nothing is found, treat the chosen folder as a new SDMC root
- Rationale:
  - It matches the user's actual mental model of "pick the SDMC space" without forcing them to browse to one exact directory shape.
  - It keeps the change reviewable by reusing the existing path-override mechanism instead of relocating the entire user directory.
  - It avoids corrupting existing external SDMC folders by explicitly skipping bundled `assets/sdmc` seeding when a custom SDMC root is active.
- Consequences:
  - Android settings now expose separate `SDMC Folder` and `Save States Folder` entries.
  - Existing Citra/MMJ-compatible SDMC folders can be reused without moving `nand`, config, shaders, or other user data.
  - Full Android user-directory relocation and richer import/migration UX remain future work.

## 2026-03-18 - Implement Android custom storage as a save-state directory override first
- Context: The owner wanted the Android build to pick up existing save states already stored elsewhere on the phone, but the current app initializes its main user directory inside app-scoped storage and the repo's SAF/native filesystem support is not broad enough yet to relocate the entire Citra user tree safely.
- Decision: Add an Android-only setting that overrides `FileUtil::UserPath::StatesDir` and leave the rest of the user directory rooted in the normal app-managed `citra-emu` path.
- Rationale:
  - It solves the immediate compatibility problem for existing save states with a small, reviewable change.
  - It avoids destabilizing `sdmc`, `nand`, config, and other paths that still assume normal filesystem semantics under the main user directory.
  - It keeps future storage work open: broader import tooling or full user-directory relocation can be evaluated later if needed.
- Consequences:
  - Android settings now expose a dedicated save-state folder path with manual edit, browse, and reset-to-default behavior.
  - Existing save states can be reused by pointing the setting at an old `states` folder.
  - Broader Android storage customization remains a tracked follow-up rather than an implicit promise of this feature.

## 2026-03-18 - Master forked dependency submodule strategy for Android compatibility
- Context: The only reproducible Android runtime in this repo relies on owner-forked submodules (`dominikrieder94-dev/*`) with targeted compatibility patches for `boost`, `dynarmic`, and `nihstro`; one-off local behavior needs in `soundtouch` and `libressl` were moved into superproject CMake configuration.
- Decision: Treat forked submodule URLs as intentional architecture, documented in `.gitmodules`, and keep pinned, curated commits for build stability while upstream alignment is evaluated.
- Rationale:
  - Submodule drift and compatibility fixes are unavoidable with the legacy Android toolchain.
  - This preserves a known-good state in an audit-friendly way instead of keeping an opaque dirty working tree.
  - `EXTERNALS_PRESERVATION.md` and `HANDOVER.md` will continue recording the exact runtime state and migration work.
- Consequences:
  - `.gitmodules` and `HANDOER.md` / `HANDOVER.md` are the single source of truth for submodule remotes and SHAs.
  - Avoid untracked local modifications in these externals; apply any needed patches by branch/PR in the owner forks.
  - Add a future task to remove the special case when upstream solves the compatibility drift cleanly.

## 2026-03-15 - Treat `citra_v2` as the canonical maintained fork
- Context: The older `citra` checkout contains earlier scaffolding and exploratory work, but active recovery and the currently working Android runtime now live in `citra_v2`.
- Decision: Use `citra_v2` as the maintained repo going forward and move repo-operational docs here.
- Rationale:
  - Keeps the working Android baseline in the repo we actively maintain.
  - Avoids split-brain maintenance across two sibling checkouts.
  - Makes future checkpointing and releaseability work concrete.
- Consequences:
  - `AGENTS.md` and `.codex/docs/` now belong here.
  - New work should treat `citra` as historical context, not the active fork.

## 2026-03-15 - Keep Android device validation as the primary runtime target
- Context: Attempting to use an x86_64 Android emulator introduced unrelated ABI and dependency failures that did not reflect the owner's actual target device.
- Decision: Prioritize physical Android `arm64-v8a` devices, with the Galaxy S24+ as the current reference target.
- Rationale:
  - Matches the real deployment target.
  - Avoids false work on emulator-specific ABI problems.
  - Keeps runtime debugging grounded in the path that actually matters.
- Consequences:
  - Emulator support is secondary unless explicitly requested.
  - Android runtime decisions should be validated on-device first.

## 2026-03-15 - Preserve current external dependency state before normalization
- Context: The current working Android runtime relies on external submodule states that do not match the recorded gitlinks and, in some cases, include large local working-tree drift.
- Decision: Audit externals read-only first and preserve the known-good state before trying to normalize or reset submodules.
- Rationale:
  - Blind cleanup could destroy the only known-good Android runtime.
  - The repo is not yet reproducible from gitlinks alone.
  - Preservation-first keeps future normalization grounded in facts.
- Consequences:
  - Dirty submodules are not to be reset casually.
  - `EXTERNALS_PRESERVATION.md` acts as a temporary safety record.

## 2026-03-15 - Force Android EGL setup onto the direct presentation path
- Context: Android rendering was producing a persistent black screen even after the game booted and valid rendering work existed upstream.
- Decision: Force `use_present_thread = false` before `EGLAndroid` creation on Android so the renderer uses the window-surface context consistently.
- Rationale:
  - The broken state came from an EGL context/surface mismatch, not from missing emulation output.
  - This fix restored visible rendering on the target device.
  - It is the key runtime decision that future Android renderer changes must preserve or consciously replace.
- Consequences:
  - Android renderer changes should be checked against this assumption.
  - Any future present-thread restoration needs explicit validation on device.

## 2026-03-15 - Preserve Android debugging workflow as a repo-local skill
- Context: The successful recovery session depended on a repeatable Windows-to-Android workflow: build, install, clear logs, reproduce, inspect logs, patch, repeat.
- Decision: Store this workflow under `.codex/skills/` so future agents can reuse it directly.
- Rationale:
  - Avoids re-deriving commands and device-side debugging patterns.
  - Makes practical recovery work part of repo memory rather than just chat history.
  - Supports future maintenance sessions even after context turnover.
- Consequences:
  - Future agents should use the skill when doing Android device build/deploy/debug work here.

## 2026-03-17 - Keep the current Android bootstrap contract explicit and pinned
- Context: The repo now has a working Android build again, but reproducibility still depends on knowing the exact CLI entrypoint, required Android SDK components, and which externals are intentionally pinned compatibility snapshots rather than clean upstream rewinds.
- Decision: Treat the Windows CLI Android path as a pinned operational contract for now:
  - initialize submodules with `git submodule update --init --recursive`
  - provide `src/android/local.properties` per machine
  - build from `src/android` with `cmd /c gradlew.bat :app:assembleDebug --stacktrace`
  - keep `Taskfile.yml` aligned with that path
  - keep `externals/boost` and `externals/nihstro` on their current compatibility snapshots until a cleaner replacement strategy exists
- Rationale:
  - This is the only fully verified Android build/install/runtime path today.
  - Making it explicit reduces dependence on chat history and local memory.
  - The remaining external exceptions are now understood compatibility constraints, not ambiguous dirt.
- Consequences:
  - Fresh-checkout instructions should describe `local.properties`, pinned NDK/CMake versions, and the exact build command.
  - Generated Android build logs should stay ignored so bootstrap work does not pollute the worktree.
  - Future external cleanup should target replacing or minimizing Boost and Nihstro deliberately, not by blind rewind.
