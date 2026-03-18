# INSIGHTS

## 2026-03-19 Android first release-candidate path
- The Android app was still debug-only operationally even after the runtime cleanup because the repo's `release` build type pointed at a dead local MMJ keystore path (`D:/Android/android-sign-key/dolphin-release-key.jks`), so `assembleRelease` could not even validate signing.
- Release packaging now works with three distinct modes:
  - real release signing via `ANDROID_RELEASE_STORE_FILE`, `ANDROID_RELEASE_STORE_PASSWORD`, `ANDROID_RELEASE_KEY_ALIAS`, and `ANDROID_RELEASE_KEY_PASSWORD`
  - local installable release-candidate signing via `ANDROID_LOCAL_RELEASE_USE_DEBUG_SIGNING=true`
  - unsigned release output when neither signing path is configured
- The first local release-candidate build succeeded after adding the generated R8 suppression for `com.google.errorprone.annotations.Immutable`; without it, `minifyReleaseWithR8` failed on a Tink dependency annotation class that is not needed at runtime.
- This build is still not Play-ready. Lint flags `targetSdkVersion 31` as expired for Google Play, so targeting API 33+ remains a separate releaseability task.
- Debug and release are currently visually indistinguishable on-device because they share the same app label and launcher assets. Only the package name (`org.citra.emu.debug` vs `org.citra.emu`) distinguishes them.

## 2026-03-18 Android SDMC folder override
- For this fork, the useful user-facing picker target is the SDMC root, not the `states` folder, because in-game saves live under `sdmc/Nintendo 3DS/...`.
- A practical Android UX is to accept three inputs for the same setting: the SDMC root itself, the `Nintendo 3DS` directory, or a higher parent folder that already contains `Nintendo 3DS` somewhere below it.
- The current implementation resolves that with a bounded recursive search for a directory literally named `Nintendo 3DS`; if it finds one, the parent becomes the real SDMC root. If it does not, the selected folder is treated as a new SDMC root and a `Nintendo 3DS` directory is created there.
- Custom SDMC roots must not be initialized by copying the app's bundled `assets/sdmc` payload, because that payload is not empty and would contaminate an existing shared folder.
- This is still a Citra-style SDMC override, not real hardware SD-card import logic. Core FS paths in this build continue to use the emulator's fixed zeroed `SYSTEM_ID` and `SDCARD_ID`, so the external folder still needs to match the Citra/MMJ-style `Nintendo 3DS/000.../000...` layout to be useful.

## 2026-03-18 Android save-state folder override
- On current Android builds, the base user directory is initialized under the app-scoped `.../Android/data/<package>/files/citra-emu`, so older shared-storage states under paths like `/sdcard/citra-emu/states` are invisible by default even when the app otherwise works.
- Repointing the entire Android user directory is the wrong first implementation here because scoped-storage and SAF support are still incomplete for generic native directory traversal.
- A focused override for `FileUtil::UserPath::StatesDir` is enough to reuse existing save states without disturbing the rest of the user tree (`sdmc`, `nand`, config, shaders, etc.).
- The existing Android directory picker can provide a real filesystem folder path when the app has broad storage access, so save-state folder selection fits cleanly into the current settings UI as an editor field with a browse action.

## 2026-03-18 Layout-specific running settings
- Once Android running-settings rows become layout-dependent, `RunningSettingDialog` cannot safely save by RecyclerView row order anymore. Hidden layout-specific rows would shift the native settings array and corrupt unrelated values.
- The safer model is to treat each running-setting item's numeric id as its JNI array slot and rebuild the outgoing array by id, leaving undisplayed settings unchanged. That makes conditional layout options possible without a larger UI rewrite first.

## 2026-03-18 Hybrid layout rendering model
- The new hybrid layout is not just another two-rectangle framebuffer calculation. It needs a third on-screen draw rectangle so the primary 3DS screen can be rendered twice at once.
- The shared `FramebufferLayout` model now has to carry an optional extra screen rectangle plus which source texture it mirrors. Only changing `framebuffer_layout.cpp` math is insufficient because the OpenGL frontend previously hard-coded exactly one top-screen quad and one bottom-screen quad.
- Touch behavior should keep using the real `bottom_screen` rectangle only. The duplicated screen in the hybrid side column is display-only and must not become a second touch target.
- For Android, the existing large-layout right-edge alignment step also has to translate the extra hybrid rectangle; otherwise the duplicated screen lags behind when the preset layout is shifted to the display edge.

## 2026-03-18 ES-DE Android 3DS emulator detection
- ES-DE's Android emulator support is driven by bundled `es_find_rules.xml` and `es_systems.xml` resource files, not by fuzzy package discovery.
- Current ES-DE Android rules explicitly recognize `org.citra.emu/.ui.EmulationActivity` as `CITRA-MMJ`, and the Nintendo 3DS system exposes that as the alternative emulator label `Citra MMJ (Standalone)`.
- The repo's Android debug build uses application ID `org.citra.emu.debug`, while the release package ID remains `org.citra.emu`.
- Because ES-DE's bundled Android find rules do not include `org.citra.emu.debug`, the debug build will not be discovered as the built-in `Citra MMJ` entry unless the user adds a custom `es_find_rules.xml` override.
- If both `org.citra.emu` and `org.citra.emu.debug` are installed, ES-DE can only match the release package for the built-in `Citra MMJ` entry.

## Android runtime recovery
- The app can now build, install, boot, and render again on a physical Galaxy S24+.
- The earlier black-screen issue was not a ROM-loading failure. It was a presentation/context issue after valid rendering work had already happened.
- Audio becoming audible while the screen stayed black was a useful boundary: emulation was running, but final presentation was broken.

## Renderer root cause
- The decisive rendering fix was forcing `use_present_thread = false` before `EGLAndroid` is created on Android.
- Without that, Android could create `EGLAndroid` with the shared context path while the renderer was already using the direct swap path, causing final composition to draw into the wrong EGL context/surface.
- Keeping the direct composition path bound to the default framebuffer before final draw remains important.

## CPU emulation root cause
- One earlier hard block was in the Dynarmic A32 exclusive write path.
- Missing exclusive write callbacks caused `STREX` to fail forever, trapping guest code in an atomic retry loop before graphics startup.

## Storage and device testing
- Physical `arm64-v8a` Android device testing is the primary reliable path for this fork.
- The x86_64 Android emulator path expanded scope into unrelated native/dependency issues and should not be treated as the primary target.
- Samsung scoped-storage behavior can make folder selection brittle; device-side runtime validation should distinguish storage access problems from emulation/runtime failures.

## Screen layout transfer
- The top-aligned large-screen layout was transferred into `citra_v2` shared layout code and Android settings.
- Current verification boundary:
  - the new layout option appears in the non-running Android settings UI
  - the in-game running-settings dialog still shows only the old layout options
- Treat that as an Android UI wiring gap, not as a shared-layout math failure.

## 2026-03-16 running-settings follow-up
- The current `RunningSettingDialog` worktree already includes a five-option `SETTING_SCREEN_LAYOUT` mapping with `Large Screen (Top Aligned)` and a `SETTING_LARGE_SCREEN_PROPORTION` seekbar item.
- `NativeLibrary.getRunningSettings()` and `setRunningSettings()` already round-trip `layout_option` and `large_screen_proportion` for the running dialog.
- As of this check, the remaining uncertainty is runtime verification on device, not obviously missing Java/JNI wiring.

## 2026-03-16 top-aligned layout sizing
- `Large Screen (Top Aligned)` was scaling against its full combined width, which let it expand farther horizontally than the original `Large Screen` layout on wide Android displays.
- Reusing the original `Large Screen` layout's horizontal width envelope is the right constraint for this mode; it preserves the top-aligned composition without pushing the secondary screen against the physical display edge.
- Reusing the original `Large Screen` layout's vertical envelope and top-left anchor as well keeps the top-aligned mode inside the same overall bounding box, which is a better match for Android device expectations than independently centering it.
- When the slider changes, the top-aligned composition will not always fill the full original `Large Screen` height while preserving aspect ratio. Centering the overall layout vertically within that bounded height is the correct compromise.
- If the layout should visually reach the same right edge as `Large Screen`, keep the existing size cap but anchor the composed layout block to the original `Large Screen` right boundary instead of the left boundary.

## 2026-03-16 Android large-layout placement
- The emulator activity already requests fullscreen rendering and display-cutout usage; the visible right-side border was not caused by the app surface being letterboxed by Android.
- The remaining border came from `Large Screen` layout placement inside the fullscreen surface.
- Matching the original Android app more closely means shifting `Large Screen` and `Large Screen (Top Aligned)` to the usable right edge on Android after framebuffer layout calculation.
- To use the left camera-hole side as well, Android `Large Screen` and `Large Screen (Top Aligned)` must not apply the usual left safe-inset push; otherwise they can never span the full physical width.
- If a left gap still remains after removing the layout-side safe-inset push, the next Android-side check is decor fitting: the emulation activity must disable system-window fitting so the `SurfaceView` itself extends into cutout space.
- If custom layout can reach the left edge but preset `Large Screen` still cannot, that proves the remaining gap is in preset layout math, not in the Android surface bounds.
- The legacy `Large Screen` math fixes the secondary screen at quarter size, which leaves unused horizontal space on wide Android phones. An Android-specific variant that keeps the primary screen at max height and sizes the secondary screen from the remaining width matches the original app more closely.
- Android safe-inset translation can also leave preset-only vertical slack. For Android `Large Screen` and `Large Screen (Top Aligned)`, top safe-inset translation should be treated the same way as left safe-inset translation: disabled when the goal is full cutout-side utilization.

## 2026-03-16 Android top-layout full-height primary
- The remaining top/bottom border on Android `Large Screen (Top Aligned)` was not an inset problem once `Large Screen` already looked correct.
- The actual mismatch was that Android `Large Screen (Top Aligned)` still used the shared `LargeFrameLayoutTop()` math, while Android `Large Screen` had already moved to its own full-height helper.
- The correct Android behavior is to reuse the Android `Large Screen` primary rectangle exactly, then size only the secondary screen from the remaining width and top-align it. That keeps the primary screen at full height regardless of the slider.
- With that model, the slider affects only the secondary screen size. If the requested secondary size is wider than the remaining space, width becomes the cap rather than vertical envelope math.
- To keep the slider useful past that cap, Android `Large Screen (Top Aligned)` needs a second stage: once the fixed-primary layout saturates, recompute both screens together against the full display bounds so the secondary can keep growing and the primary shrinks proportionally.
- That second stage should preserve aspect ratios, use the full available width, and accept that some vertical slack becomes mathematically unavoidable on very large secondary ratios. Centering the recomputed block vertically is the least surprising fallback in that saturated range.
- The exact "max fill" quick-set value is the boundary between those two stages: the secondary screen exactly fills the remaining width beside the full-height primary without forcing the primary to shrink yet.
- That value depends on the live surface dimensions, so the correct place to expose `Auto Fit` is the in-game running dialog backed by a native calculation, not a static Java constant.

## Android build bootstrap
- `citra_v2/src/android` needed `gradlew` and `gradlew.bat` restored for the existing Taskfile-based workflow to make sense.
- The successful local build path is now `cmd /c gradlew.bat :app:assembleDebug --stacktrace` from `src/android`.
- If a future agent sees "we cannot build anymore", check for missing wrapper scripts before assuming a code regression.
- The stronger reproducibility check now also passes: `cmd /c gradlew.bat clean :app:assembleDebug --stacktrace` succeeds from `src/android`, and the same path is exposed at the repo root as `task build-debug-apk-clean`.
- `src/android/local.properties` remains intentionally untracked and machine-local. A fresh checkout still needs a valid `sdk.dir=...` pointing at the local Android SDK.
- The tracked Android Gradle config currently expects:
  - NDK `29.0.14206865`
  - CMake `3.31.6`
  - `arm64-v8a` only
- Building with JDK `23.0.1` works, but Gradle warns that Java 23 compiling source/target 8 is deprecated. That is not currently a blocker, but it is a future bootstrap risk.
- Re-running `git submodule update --init --recursive --force externals/libyuv` cleared the stray leading `-` marker in `git submodule status`. The repo now reports a fully initialized recursive submodule state again.

## Externals state
- The current working Android runtime still depends on dirty external states that are not fully represented by the superproject gitlinks.
- Do not reset, clean, or realign submodules blindly.
- See `EXTERNALS_PRESERVATION.md` for the current preservation table.
- `git submodule status` may show a surprising leading marker for `externals/libyuv` even when the nested repo is present and usable; verify the nested repo directly before treating that as a failed preservation state.
- A safe preservation checkpoint for a dirty external is two-layered:
  - commit the nested external repo first
  - then commit the updated gitlink in the superproject

## 2026-03-17 libyuv audit
- `externals/libyuv` is not a meaningful custom fork despite the alarming preservation diff against 2026 `origin/main`.
- The preserved snapshot commit `0650e25412d6c47724bedac775835d661603d0a8` sits on top of upstream `30809ff64a9ca5e45f86439c0d474c2d3eef3d05`, but the tree content itself is effectively a rollback to the older superproject-pinned upstream commit `5b3351bd07e83f9f9a4cb6629561331ecdb7c546`.
- The only remaining delta between the preserved snapshot and `5b3351bd07e83f9f9a4cb6629561331ecdb7c546` is five executable-bit-only mode changes on helper scripts:
  - `cleanup_links.py`
  - `source/test.sh`
  - `tools_libyuv/autoroller/roll_deps.py`
  - `tools_libyuv/autoroller/unittests/roll_deps_test.py`
  - `tools_libyuv/get_landmines.py`
- Practical consequence: `libyuv` should be one of the easiest externals to normalize, likely by returning to the old pinned upstream commit or by carrying a trivial mode-only patch if those executable bits matter locally.

## 2026-03-17 dynarmic audit
- The original superproject-expected dynarmic commit `b6be02ea7fae63aa661ad00763ebd295d1348591` is not present in the currently configured dynarmic fork history, so normalization cannot simply assume that old gitlink is still reachable.
- The preserved snapshot `86f70089e833eeb65956efdfcd2ff1dbb70ace9b` is a local commit directly on top of fork head `526227eebe1efff3fb14dbf494b9c5b44c2e9c1f`.
- Most of the enormous diff is not core dynarmic logic. It comes from deleting dynarmic's own `.gitmodules` and inlining the contents of dynarmic's nested `externals/*` repos as normal tracked files.
- After excluding that accidental vendoring, the real dynarmic-local delta is small and reviewable:
  - CMake/build integration adjustments
  - fmt formatter compatibility changes
  - register allocation behavior changes in ARM64 and RISC-V backends
  - CI/repository URL updates
- Practical consequence: dynarmic normalization should be split into two steps:
  - first remove the accidental nested-submodule vendoring and restore dynarmic's own external-management model
  - then audit the small remaining local patch set on top of `526227eebe1efff3fb14dbf494b9c5b44c2e9c1f`

## 2026-03-17 remaining externals audit
- `externals/boost`: the preserved snapshot `4cc38a77d7c5bfd0c73e3ceef8ef54e64387a2a2` is a single local commit directly on top of the old superproject-pinned commit `36603a1e665e849d29b1735a12c0a51284a10dd0`. The diff is broad, about 700 files, and is dominated by a Boost import centered on Asio and Align rather than a reviewable patch queue.
- `externals/soundtouch`: the old gitlink `060181eaf273180d3a7e87349895bd0cb6ccbf4a` is not reachable in the current fork history. The preserved snapshot `26ea8b97eeb87427e5973cda012bd9074536f576` is a local commit on top of fork head `9ef8458d8561d9471dd20e9619e3be4cfe564796` that deletes or rewrites large parts of the project, including autotools files, Android-lib Gradle pieces, SoundStretch sources, and packaging assets. Treat this as a local tree replacement or rollback, not a narrow patch set.
- `externals/fmt`: the preserved snapshot `c4c0c44c0210ddf7e40e2015359555405c3e7e53` is effectively the old gitlink `4b8f8fac96a7819f28f4be523ca10a2d5d8aaaf2` plus two `FMT_USE_USER_DEFINED_LITERALS=0` compile-definition lines in `CMakeLists.txt` and several executable-bit drops on helper scripts.
- `externals/enet`: the preserved snapshot `d60539d6bf267393d73506d0cc16e8e099ff3ccb` differs from the old gitlink `39a72ab1990014eb399cee9d538fd529df99c6a0` only in `enet.dsp`. With `--ignore-space-at-eol`, the diff disappears, so this is effectively line-ending churn.
- `externals/nihstro`: the preserved snapshot `c9af0af155514b5c12a6f2d9e2b10fb98ec66750` is almost normalized already. Relative to the old gitlink `fd69de1a1b960ec296cc67d32257b0f9e2d89ac6`, only four files differ: `examples/assembler/cube/source/_gs.s`, `examples/assembler/cube_lighting/source/_gs.s`, `include/nihstro/bit_field.h`, and `include/nihstro/shader_bytecode.h`.
- `externals/teakra`: the preserved snapshot `be37f163e407f193dbe3394574554878da87285e` is effectively the old gitlink `e6ea0eae656c022d7878ffabc4e016b3e6f0c536` plus seven executable-bit-only mode changes on test and helper scripts.
- `externals/xbyak`: the preserved snapshot `0c0903965053ef074da2d16d900fa59e0eeb0d60` differs from the old gitlink `1de435ed04c8e74775804da944d176baf0ce56e2` only in shell-script and documentation line endings. With `--ignore-space-at-eol`, the remaining visible diff collapses to four test shell scripts. This is low-risk and irrelevant for Android `arm64-v8a`.
- `externals/libressl`: the preserved snapshot `ab327f02cd682101dd3af930b99e6ca40602e1ec` is a large local tree replacement on top of fork head `88b8e41b71099fabc57813bc06d8bc1aba050a19`, with about 947 files changed. This is not a mode-only cleanup candidate, but it is also not on the active Android runtime path.
- `externals/inih/inih`: the actual submodule path matters. The preserved snapshot `319893ccbe95662983177b589a6cb76f90cc8c65` is an empty-tree commit on top of upstream `577ae2dee1f0d9c2d11c7f10375c1715f3d6940c` and deletes the entire upstream `inih` contents. That preservation snapshot is not actually safe and must not be normalized blindly.
- Practical normalization buckets:
  - Easy first: `libyuv`, `fmt`, `enet`, `teakra`, `nihstro`, `xbyak`
  - Needs separation first: `dynarmic`
  - Heavy manual review: `boost`, `soundtouch`, `libressl`
  - Broken preservation snapshot: `externals/inih/inih`

## 2026-03-17 libyuv normalization
- `externals/libyuv` has now been normalized by rewinding the superproject gitlink from the preserved local snapshot `0650e25412d6c47724bedac775835d661603d0a8` to the clean upstream commit `5b3351bd07e83f9f9a4cb6629561331ecdb7c546`.
- This normalization intentionally discards only the five executable-bit-only helper-script changes identified during audit; no Android-relevant source content changed.
- Because the normalized tree content already matched the old expected source state, no Android rebuild was required to justify this step. Rebuild only if a later dependency normalization touches actual code.

## 2026-03-17 enet, teakra, and xbyak normalization
- `externals/enet` has now been normalized back to clean upstream commit `39a72ab1990014eb399cee9d538fd529df99c6a0`. The removed drift was only line-ending churn in `enet.dsp`.
- `externals/teakra` has now been normalized back to clean upstream commit `e6ea0eae656c022d7878ffabc4e016b3e6f0c536`. The removed drift was only seven executable-bit-only mode changes on helper scripts.
- `externals/xbyak` has now been normalized back to clean upstream commit `1de435ed04c8e74775804da944d176baf0ce56e2`. The removed drift was limited to shell-script and documentation line-ending churn and is irrelevant for Android `arm64-v8a`.
- These three submodule cleanups are safe to batch because none of them changed functional source content on the active Android runtime path.

## 2026-03-17 fmt and nihstro normalization boundary
- `externals/fmt` can be normalized safely. Rewinding it to `4b8f8fac96a7819f28f4be523ca10a2d5d8aaaf2` still allows `cmd /c gradlew.bat :app:assembleDebug --stacktrace` to succeed on Android.
- The local `fmt` delta was therefore not required for the current Android build. It only removed user-defined-literal compile definitions and helper-script mode churn.
- `externals/nihstro` cannot be normalized by simple rewind. Reverting it to `fd69de1a1b960ec296cc67d32257b0f9e2d89ac6` fails Android compilation in `include/nihstro/shader_bytecode.h` because the older code specializes `std::make_unsigned`, which current libc++ explicitly forbids.
- The preserved `nihstro` patches are not cosmetic. At minimum, the `BitFieldStorageType` refactor and the extra `return 0;` in `SourceRegister::GetIndex()` are part of the current working Android toolchain compatibility story.

## 2026-03-17 nihstro minimization
- `externals/nihstro` no longer needs to stay on the broader preserved snapshot `c9af0af155514b5c12a6f2d9e2b10fb98ec66750`.
- A cleaner base exists at upstream `f4d8659f85874de9044d197b1d4a7f8340de1d4b`, which already contains newer Boost-related cleanup.
- Rebuilding from clean `f4d8659` shows the actual Android blocker clearly: `include/nihstro/shader_bytecode.h` still specializes `std::make_unsigned` for `SourceRegister`, `DestRegister`, and `OpCode`, and current Android libc++ rejects those specializations outright.
- The minimal working compatibility delta is only:
  - `include/nihstro/bit_field.h`: introduce `BitFieldStorageType` as the customization point used by `BitField`
  - `include/nihstro/shader_bytecode.h`: specialize `BitFieldStorageType` for the enum-like register/opcode wrapper types and keep the fallback `return 0;` in `SourceRegister::GetIndex()`
- That reduced two-file patch was committed locally as `b2291a63a6bdbb095b68dcffde6be3c73887cf17`, Android `:app:assembleDebug` passes, the APK installs to `R3CXB0SJ5GL`, and device runtime is confirmed good.

## 2026-03-17 inih repair
- `externals/inih/inih` was not merely dirty; the superproject was pinned to a broken local commit `319893ccbe95662983177b589a6cb76f90cc8c65` that deleted the entire upstream tree.
- Rewinding it to the clean historical gitlink `2023872dfffb38b6a98f2c45a0eb25652aaea91f` restores the expected source layout (`ini.c`, `ini.h`, `cpp/INIReader.*`, examples, tests).
- Although `inih` is not on the active Android runtime path, Android `:app:assembleDebug` still passes after the repair, which confirms the cleanup did not introduce broader build-system fallout.

## 2026-03-17 dynarmic normalization
- `externals/dynarmic` can be normalized without keeping the accidental vendored copies of dynarmic's nested `externals/*` trees.
- A clean baseline exists at fork commit `526227eebe1efff3fb14dbf494b9c5b44c2e9c1f`. Restoring dynarmic's own submodules on top of that baseline and then re-applying only the small real patch set yields a working Android build.
- The preserved working dynarmic state is now local commit `384d240134f74ebaed6bd748d9662069dcaf3a68`, which keeps the CMake/build tweaks, formatter compatibility adjustments, and register-allocation changes while leaving dynarmic's nested dependency model intact.
- `cmd /c gradlew.bat :app:assembleDebug --stacktrace` still succeeds after this split, so the accidental vendoring was not required for the current Android `arm64-v8a` build.
- Runtime is now also confirmed on the physical device after deployment. The normalized dynarmic state is no longer just a build-clean hypothesis.
- For higher-risk externals, the requested validation flow is now explicit: rebuild, deploy to `R3CXB0SJ5GL`, then wait for device confirmation before treating the cleanup as safe.

## 2026-03-17 soundtouch normalization
- The preserved local `externals/soundtouch` commit `26ea8b97eeb87427e5973cda012bd9074536f576` was not a required code fork. It was mostly a tree replacement that deleted packaging assets, the legacy Android example, and `SoundStretch` sources.
- A straight rewind to clean upstream `9ef8458d8561d9471dd20e9619e3be4cfe564796` fails the Android configure step for two integration reasons:
  - the superproject was still adding `target_include_directories(SoundTouch INTERFACE ./soundtouch/include)`, which conflicts with upstream install/export rules
  - the emulator relies on the integer-sample ABI, but upstream keeps `SOUNDTOUCH_INTEGER_SAMPLES` as a private target definition
- The clean fix is to keep `soundtouch` on upstream `9ef8458d8561d9471dd20e9619e3be4cfe564796` and move the real integration choices into `externals/CMakeLists.txt`:
  - force `SOUNDSTRETCH=OFF` because the emulator does not use the CLI utility and the preserved local tree had deleted its sources anyway
  - propagate `SOUNDTOUCH_INTEGER_SAMPLES` as an interface definition on the `SoundTouch` target
- With those superproject fixes in place, Android `:app:assembleDebug` succeeds again and the normalized APK installs to `R3CXB0SJ5GL`.
- Runtime is now also confirmed on the physical device after deployment. The normalized soundtouch state is no longer just build-clean.

## 2026-03-17 libressl normalization
- The preserved local `externals/libressl` commit `ab327f02cd682101dd3af930b99e6ca40602e1ec` is a large local tree replacement on top of clean fork head `88b8e41b71099fabc57813bc06d8bc1aba050a19`, not a small patch queue worth keeping as active state.
- Because Android builds this repo with `ENABLE_WEB_SERVICE=0`, LibreSSL is outside the active Android runtime path. That makes it a good candidate for direct rewind to a clean fork commit.
- Rewinding `externals/libressl` to clean fork head `88b8e41b71099fabc57813bc06d8bc1aba050a19` leaves Android `:app:assembleDebug` passing, which is the only required validation for the current Android-first recovery goal.
- Practical consequence: `libressl` no longer needs to stay on a preserved local snapshot for Android reproducibility. The preserved local tree replacement can remain only as recoverable history.
- Runtime is now also confirmed on the physical device after deployment. The normalized libressl state is no longer just build-clean.

## 2026-03-17 boost classification
- `externals/boost` is already clean in Git terms because the superproject is pinned to the preserved snapshot `4cc38a77d7c5bfd0c73e3ceef8ef54e64387a2a2`.
- That preserved snapshot is still required for the current Android toolchain. Rewinding to the old clean gitlink `36603a1e665e849d29b1735a12c0a51284a10dd0` breaks Dynarmic compilation under current Android libc++.
- The concrete failure is in old Boost `boost/container_hash/hash.hpp`, which still derives from `std::unary_function`; current libc++ has removed that symbol, so old Boost can no longer compile cleanly in this environment.
- Practical consequence: Boost is not a direct normalization candidate. Treat `4cc38a77d7c5bfd0c73e3ceef8ef54e64387a2a2` as an intentional pinned compatibility snapshot until the broad import is decomposed, replaced with a clean newer upstream Boost snapshot, or otherwise minimized.

## Practical debugging workflow
- The recovered workflow is:
  1. build debug APK
  2. install to physical device with `adb -s <serial> install -r`
  3. clear logcat
  4. reproduce on device
  5. inspect `citra` logs or crash buffer
- Preserve that workflow in the repo-local skill and prefer it over rediscovering ad-hoc commands.
