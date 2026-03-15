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

## Externals state
- The current working Android runtime still depends on dirty external states that are not fully represented by the superproject gitlinks.
- Do not reset, clean, or realign submodules blindly.
- See `EXTERNALS_PRESERVATION.md` for the current preservation table.

## Practical debugging workflow
- The recovered workflow is:
  1. build debug APK
  2. install to physical device with `adb -s <serial> install -r`
  3. clear logcat
  4. reproduce on device
  5. inspect `citra` logs or crash buffer
- Preserve that workflow in the repo-local skill and prefer it over rediscovering ad-hoc commands.
