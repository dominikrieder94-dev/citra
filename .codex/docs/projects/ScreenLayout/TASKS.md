# Screen Layout Tasks

## Priority
- [ ] Verify the transferred layout in a local Android build/runtime test and tune the default secondary-screen scale if needed.
- [ ] Decide whether Android should keep one shared layout/scale control for both orientations in settings or expose separate portrait/landscape controls.
- [ ] Re-run a desktop build after the Android follow-up because the shared layout enum and math changed.

## Done
- [x] Transfer the dedicated top-aligned large-screen layout mode into the shared framebuffer layout logic. (2026-03-15)
- [x] Transfer the configurable secondary-screen scale plumbing for Android config/runtime settings. (2026-03-15)
- [x] Preserve the ScreenLayout project notes from the retired `citra` checkout. (2026-03-15)
