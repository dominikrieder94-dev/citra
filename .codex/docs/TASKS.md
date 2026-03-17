# TASKS

## Priority
- [ ] Decide how to replace or minimize the broad local Boost import while keeping Android toolchain compatibility.
- [ ] Validate the current Android build/runtime on at least one representative set of games and record residual issues.
- [ ] Split releaseability work from future feature workstreams such as screen layout improvements.
- [ ] Decide whether current Android build warnings should be reduced now or left as tracked modernization debt.

## Done
- [x] Minimize `externals/nihstro` to a two-file local Android libc++ compatibility patch on top of clean upstream `f4d8659f85874de9044d197b1d4a7f8340de1d4b`, then rebuild, deploy, and confirm on device. (2026-03-17)
- [x] Remove or ignore local generated Android build logs so the worktree stays readable. (2026-03-17)
- [x] Normalize Android build bootstrap so a fresh checkout can build from CLI without machine-specific assumptions: keep wrapper scripts, confirm `Taskfile.yml`, document prerequisites, and decide what belongs in version control. (2026-03-17)
- [x] Define a reproducible bootstrap strategy that can recreate the current Android-working state. (2026-03-17)
- [x] Classify and preserve `externals/nihstro` as an intentional local compatibility patch instead of treating it as a simple rewind candidate. (2026-03-17)
- [x] Classify `externals/boost` as an intentional pinned compatibility snapshot for the current Android toolchain; old clean commit `36603a1e665e849d29b1735a12c0a51284a10dd0` fails build because Boost still uses `std::unary_function`. (2026-03-17)
- [x] Confirm on device that the `externals/libressl` normalization behaves correctly on `R3CXB0SJ5GL`. (2026-03-17)
- [x] Normalize `externals/libressl` to clean fork head `88b8e41b71099fabc57813bc06d8bc1aba050a19`, then rebuild and deploy the APK. (2026-03-17)
- [x] Confirm on device that the `externals/soundtouch` normalization behaves correctly on `R3CXB0SJ5GL`. (2026-03-17)
- [x] Normalize `externals/soundtouch` to clean upstream commit `9ef8458d8561d9471dd20e9619e3be4cfe564796` by moving the real Android integration requirements into `externals/CMakeLists.txt`, then rebuild and deploy the APK. (2026-03-17)
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
