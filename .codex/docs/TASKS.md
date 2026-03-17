# TASKS

## Priority
- [ ] Classify and preserve `externals/nihstro` as an intentional local compatibility patch instead of treating it as a simple rewind candidate.
- [ ] Decide the target strategy for the heavy drifts in `externals/boost`, `externals/soundtouch`, and `externals/libressl`.
- [ ] Define a reproducible bootstrap strategy that can recreate the current Android-working state.
- [ ] Normalize Android build bootstrap so a fresh checkout can build from CLI without machine-specific assumptions: keep wrapper scripts, confirm `Taskfile.yml`, document prerequisites, and decide what belongs in version control.
- [ ] Validate the current Android build/runtime on at least one representative set of games and record residual issues.
- [ ] Split releaseability work from future feature workstreams such as screen layout improvements.
- [ ] Remove or ignore local generated build logs so the worktree stays readable.

## Done
- [x] Confirm on device that the `externals/dynarmic` normalization behaves correctly on `R3CXB0SJ5GL`. (2026-03-17)
- [x] Separate accidental vendoring from the real local patch set in `externals/dynarmic`, restore dynarmic's nested submodules, and verify Android `:app:assembleDebug` still passes. (2026-03-17)
- [x] Repair `externals/inih/inih` by restoring the clean historical gitlink in place of the broken empty-tree snapshot. (2026-03-17)
- [x] Normalize `externals/fmt` back to the clean upstream commit and verify Android `:app:assembleDebug` still passes. (2026-03-17)
- [x] Normalize `externals/enet`, `externals/teakra`, and `externals/xbyak` back to their clean upstream commits. (2026-03-17)
- [x] Normalize `externals/libyuv` back to the clean upstream commit already matching its source content. (2026-03-17)
- [x] Finish the screen-layout transfer by wiring `Large Screen (Top Aligned)` and the proportion slider into the in-game running-settings dialog, then verify the behavior on the Galaxy S24+. (2026-03-16)
- [x] Audit and classify dirty `externals/*` state without changing it. (2026-03-17)
- [x] Restore Android device boot and rendering on the maintained fork. (2026-03-15)
- [x] Record current external preservation state before normalization. (2026-03-15)
- [x] Establish repo-local operational docs under `.codex/docs/`. (2026-03-15)
- [x] Add a repo-local Android device debugging skill under `.codex/skills/`. (2026-03-15)
- [x] Transfer the first pass of the top-aligned large-screen layout feature into `citra_v2` and build/install it on device. (2026-03-15)
