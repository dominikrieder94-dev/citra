---
name: android-device-debugging
description: Build, install, run, and debug the Android Citra fork on a connected device from a Windows workspace. Use when the task involves Android APK builds, `adb` install/update flows, logcat capture, native/runtime crash triage, render regressions, or reproducing device-specific issues for this repository.
---

# Android Device Debugging

## Overview

Use this skill when working on the Android fork in a Windows checkout with a connected Android device. It preserves the practical workflow that already worked in this repo: build the APK, install it to the phone, clear logs, reproduce the issue, inspect `logcat`, and iterate.

Read `references/android-debug-workflow.md` for exact commands, expected paths, and repo-specific gotchas.

## Workflow

1. Confirm the current repository and task
- Work from the active repo root.
- Prefer the existing `Taskfile.yml` when it covers the needed workflow.
- If the repo already contains Android debugging tasks, use them instead of inventing new command wrappers.

2. Verify device connectivity
- Run `adb devices` first.
- If multiple devices are connected, always use `adb -s <serial> ...`.
- If `adb` is not on `PATH`, use the SDK `platform-tools` path directly.

3. Build the debug APK
- Prefer the repo's existing Gradle task or `Taskfile.yml` task.
- Use a clean build when changing native code or when artifact staleness is plausible.
- Treat build warnings separately from actual build blockers.

4. Install and reproduce
- Install with `adb install -r`.
- Clear `logcat` before reproducing runtime failures.
- Reproduce on the physical `arm64-v8a` device unless the task explicitly targets emulator behavior.

5. Pull the smallest useful logs
- Start with filtered `logcat` output for the app package or `citra` tag.
- Use the crash buffer when the process actually crashes.
- If the app does not crash, inspect lifecycle and renderer logs before assuming native failure.

6. Iterate surgically
- Add targeted logs only where the current fault boundary is unclear.
- Remove or scale back heavy diagnostics once the root cause is understood, especially anything that can distort frame time or presentation.
- Preserve working device behavior before attempting broad cleanup.

## Decision rules

- Prefer the physical Android device over the x86_64 emulator path for this fork.
- Distinguish build failures, startup failures, runtime crashes, standby/lifecycle issues, and renderer black screens. They are different classes of bugs.
- When externals are dirty, do not reset or normalize them as part of routine Android debugging.
- When a debug build becomes slower after instrumentation, remove the probes before judging runtime performance.

## Reference

- For commands, paths, and repo-specific gotchas, read `references/android-debug-workflow.md`.
