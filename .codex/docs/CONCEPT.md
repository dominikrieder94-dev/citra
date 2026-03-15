# CONCEPT

## Architecture map
- `src/`: emulator runtime, frontend, Android JNI bridge, platform integrations.
- `externals/`: third-party code and submodules; currently not yet normalized.
- `dist/`: packaging assets.
- `.codex/docs/`: operational memory for this maintained fork.
- `.codex/skills/`: reusable agent workflows for this repo.

## Working model
- Treat Android `arm64-v8a` device runtime as the first-class validation path.
- Preserve the current known-good runtime before simplifying or normalizing dependencies.
- Separate product changes from bootstrap changes from dependency-state changes.
- Keep debugging workflows reproducible by documenting exact commands, paths, and device-side checks.

## Current technical reality
- The app now boots and renders on a physical Android device again.
- The repo still contains unresolved external dependency drift beyond the recorded submodule gitlinks.
- Bootstrap/release work should proceed with preservation-first rules until externals are audited and minimized.
