# INSIGHTS

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

## Externals state
- The current working Android runtime still depends on dirty external states that are not fully represented by the superproject gitlinks.
- Do not reset, clean, or realign submodules blindly.
- See `EXTERNALS_PRESERVATION.md` for the current preservation table.
- `git submodule status` may show a surprising leading marker for `externals/libyuv` even when the nested repo is present and usable; verify the nested repo directly before treating that as a failed preservation state.
- A safe preservation checkpoint for a dirty external is two-layered:
  - commit the nested external repo first
  - then commit the updated gitlink in the superproject

## Practical debugging workflow
- The recovered workflow is:
  1. build debug APK
  2. install to physical device with `adb -s <serial> install -r`
  3. clear logcat
  4. reproduce on device
  5. inspect `citra` logs or crash buffer
- Preserve that workflow in the repo-local skill and prefer it over rediscovering ad-hoc commands.
