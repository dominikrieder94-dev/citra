# DECISIONS

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
