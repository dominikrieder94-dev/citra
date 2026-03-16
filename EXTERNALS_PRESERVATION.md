# Externals Preservation Audit

Status date: 2026-03-15

Purpose:
- Record the current known-good external dependency state for the Android `arm64-v8a` build.
- Avoid accidental cleanup of submodules that may still be required for the current working runtime.
- Keep this read-only until each submodule is proven safe to realign.

Legend:
- `Android path`: whether the submodule is on the active Android build/runtime path.
- `Preserve level`:
  - `must preserve`: do not reset or realign yet.
  - `hold`: avoid changing until audited further.
  - `likely irrelevant for Android`: not on the current Android path, can wait.

| Submodule | Android path | Superproject expects | Current checkout | Dirty paths | Preserve level | Notes |
| --- | --- | --- | --- | ---: | --- | --- |
| `externals/boost` | yes | `36603a1e665e849d29b1735a12c0a51284a10dd0` | `36603a1e665e849d29b1735a12c0a51284a10dd0` | 513 | must preserve | Same pinned commit, but heavily dirty inside working tree. Used by `core`, `input_common`, and OpenGL cache code. |
| `externals/dynarmic` | yes | `b6be02ea7fae63aa661ad00763ebd295d1348591` | `526227eebe1efff3fb14dbf494b9c5b44c2e9c1f` | 465 | must preserve | Critical for CPU emulation. Current checkout differs from recorded gitlink and the original commit is not publicly reachable. |
| `externals/enet` | yes | `39a72ab1990014eb399cee9d538fd529df99c6a0` | `8be2368a8001f28db44e81d5939de5e613025023` | 63 | hold | Required by `network`, which is linked into Android. Unknown whether current drift is functionally required for device boot. |
| `externals/fmt` | yes | `4b8f8fac96a7819f28f4be523ca10a2d5d8aaaf2` | `879463ecad453b33c914eead826535429319764b` | 149 | hold | Required by `common` and `core`. Also explicitly wired in `externals/CMakeLists.txt`. |
| `externals/inih/inih` | no | `2023872dfffb38b6a98f2c45a0eb25652aaea91f` | `577ae2dee1f0d9c2d11c7f10375c1715f3d6940c` | 61 | likely irrelevant for Android | Used by desktop frontend, not by the Android JNI config path. |
| `externals/libressl` | no | `7d01cb01cb1a926ecb4c9c98b107ef3c26f59dfb` | `88b8e41b71099fabc57813bc06d8bc1aba050a19` | 1016 | likely irrelevant for Android | Web service is disabled in the Android build. Very large drift, but not on the active runtime path. |
| `externals/libyuv` | yes | `5b3351bd07e83f9f9a4cb6629561331ecdb7c546` | `30809ff64a9ca5e45f86439c0d474c2d3eef3d05` | 220 | must preserve | Required by Android camera path and native library extraction/loading. |
| `externals/nihstro` | yes | `fd69de1a1b960ec296cc67d32257b0f9e2d89ac6` | `f4d8659decbfe5d234f04134b5002b82dc515a44` | 58 | hold | Used by `video_core` shader/debug code. Not first suspect, but still on the build path. |
| `externals/soundtouch` | yes | `060181eaf273180d3a7e87349895bd0cb6ccbf4a` | `9ef8458d8561d9471dd20e9619e3be4cfe564796` | 137 | must preserve | Required by `audio_core`. Also explicitly forced to `INTEGER_SAMPLES` in top-level external config. |
| `externals/teakra` | yes | `e6ea0eae656c022d7878ffabc4e016b3e6f0c536` | `3d697a18df504f4677b65129d9ab14c7c597e3eb` | 145 | must preserve | Required by DSP/audio LLE path. |
| `externals/xbyak` | no for `arm64-v8a` | `1de435ed04c8e74775804da944d176baf0ce56e2` | `560ca671421e47e32d3c8270623aaa74454570f4` | 154 | likely irrelevant for Android | Relevant for x86_64 Dynarmic path, not for the current ARM64 target. |

## Current safe rule

Do not run any of these yet:
- `git submodule update --init --recursive`
- `git submodule sync --recursive` followed by checkout resets
- `git clean -fdx` inside dirty submodules
- manual `git checkout` to the recorded gitlinks

Until each submodule is audited, the current dirty external state should be treated as part of the only known-good Android runtime.

## Next audit order

1. `externals/dynarmic`
2. `externals/libyuv`
3. `externals/soundtouch`
4. `externals/teakra`
5. `externals/boost`
6. `externals/fmt`
7. `externals/enet`
8. `externals/nihstro`

## Notes

- The superproject already records reachable replacement remotes in `.gitmodules`.
- That is not enough to reproduce the current working runtime, because several submodules have local content drift beyond a simple gitlink change.
- The correct next step is comparison and minimization, not cleanup.

## 2026-03-17 preservation follow-up

Additional preservation commits created for the remaining dirty externals before cleanup:

| Submodule | Previous checkout | Preservation snapshot |
| --- | --- | --- |
| `externals/libressl` | `88b8e41b71099fabc57813bc06d8bc1aba050a19` | `ab327f02cd682101dd3af930b99e6ca40602e1ec` |
| `externals/libyuv` | `30809ff64a9ca5e45f86439c0d474c2d3eef3d05` | `0650e25412d6c47724bedac775835d661603d0a8` |
| `externals/teakra` | `3d697a18df504f4677b65129d9ab14c7c597e3eb` | `be37f163e407f193dbe3394574554878da87285e` |

Notes:
- These are nested-repo preservation commits only until the superproject records the updated gitlinks.
- `externals/libyuv` may still display an unusual marker in `git submodule status`; verify the nested repo directly if that output looks suspicious.

## 2026-03-17 audit classification follow-up

Normalization buckets after the read-only audit pass:

- Easy first:
  - `externals/fmt`
  - `externals/nihstro`
- Needs separation first:
  - `externals/dynarmic`
- Heavy manual review:
  - `externals/boost`
  - `externals/soundtouch`
  - `externals/libressl`
- Broken preservation snapshot:
  - `externals/inih/inih`

Notes:
- `externals/boost` is a broad local import centered on Asio and Align, not a small patch stack.
- `externals/soundtouch` and `externals/libressl` are large local tree replacements on top of newer fork heads.
- `externals/inih/inih` is special: the preserved snapshot commit is an empty-tree deletion commit and must not be treated as a safe baseline.
- `externals/libyuv` has already been normalized back to clean upstream commit `5b3351bd07e83f9f9a4cb6629561331ecdb7c546`; the preserved local snapshot remains recoverable in nested history as `0650e25412d6c47724bedac775835d661603d0a8`.
- `externals/enet` has already been normalized back to clean upstream commit `39a72ab1990014eb399cee9d538fd529df99c6a0`; the preserved local snapshot remains recoverable in nested history as `d60539d6bf267393d73506d0cc16e8e099ff3ccb`.
- `externals/teakra` has already been normalized back to clean upstream commit `e6ea0eae656c022d7878ffabc4e016b3e6f0c536`; the preserved local snapshot remains recoverable in nested history as `be37f163e407f193dbe3394574554878da87285e`.
- `externals/xbyak` has already been normalized back to clean upstream commit `1de435ed04c8e74775804da944d176baf0ce56e2`; the preserved local snapshot remains recoverable in nested history as `0c0903965053ef074da2d16d900fa59e0eeb0d60`.
