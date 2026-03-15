# GOAL

## Mission
Keep this Citra fork maintained, buildable, debuggable, and releasable again, with Android as the primary delivery target.

## Canonical objective
- Turn `citra_v2` into the canonical maintained local fork.
- Preserve the current recovered Android device runtime as a known-good baseline.
- Make future workstreams such as screen layout and UX improvements additive instead of destabilizing.

## Current focus (March 2026)
- Preserve and document the current working Android device build/runtime path.
- Normalize repository bootstrap without breaking the only known-good state.
- Improve releaseability and maintainability before taking on broader feature work.

## Success criteria
- One verified Android device build/install/debug path is documented and reusable.
- Repo-operational docs exist under `.codex/docs/` and stay current.
- Architectural decisions from recovery/debugging are captured in `.codex/docs/DECISIONS.md`.
- External dependency state is preserved and eventually normalized without losing buildability.

## Out of scope by default
- Blind externals cleanup or submodule resets.
- Broad upstream sync work without explicit owner request.
- Cross-platform release work before Android releaseability is stable.
