# Screen Layout Goal

## Problem
- The shared `Large Screen` layout hard-codes a small secondary screen that is shifted downward.
- The `custom layout` path is pixel-absolute and does not stay responsive when the window size changes.
- Android is the primary user-facing target for this fork, so the shared layout feature also needs Android settings and in-game controls.

## Success criteria
- Add a dedicated layout mode that keeps the two screens top-aligned.
- Keep each screen at its native aspect ratio.
- Let the user adjust the secondary screen scale relative to the primary screen from Android settings and the Android running-settings dialog.
- Persist the new layout mode and scale setting in config for both portrait and landscape Android usage.
- Keep desktop support working because the layout math is shared.

## Non-goals
- Do not change the behavior of the existing `Large Screen` layout.
- Do not redesign the general `custom layout` system in this pass.
- Do not claim build/runtime verification until a local toolchain-enabled Android build is run.
