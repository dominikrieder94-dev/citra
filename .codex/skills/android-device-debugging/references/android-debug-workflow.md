# Android Debug Workflow

## Scope

This reference captures the workflow that was proven in this repository on Windows with a physical Samsung Galaxy S24+.

## Primary assumptions

- Repository root: `C:\Users\Dominik\Documents\Software Projekte\citra_v2`
- Android SDK `platform-tools` path:
  - `C:\Users\Dominik\AppData\Local\Android\Sdk\platform-tools`
- Physical device was used as the primary target.
- The x86_64 Android emulator path caused unrelated ABI and dependency problems and should not be treated as the default target.

## Current repo helpers

Use `Taskfile.yml` when available:

- `task build-debug-apk`
- `task install-debug-apk`
- `task clear-logcat`
- `task show-crash-log`
- `task show-app-log`

If the device serial changes, update `DEVICE_SERIAL` in `Taskfile.yml`.

## Manual commands

### Build

From `src/android`:

```powershell
.\gradlew.bat :app:assembleDebug --stacktrace
```

Use `clean` only when needed:

```powershell
.\gradlew.bat clean :app:assembleDebug --stacktrace
```

### Install

From repo root:

```powershell
adb -s <serial> install -r "src/android/app/build/outputs/apk/debug/app-debug.apk"
```

### Clear logs

```powershell
adb -s <serial> logcat -c
```

### Crash buffer

```powershell
adb -s <serial> logcat -b crash -d
```

### App/runtime filter

```powershell
adb -s <serial> logcat -d | Select-String -Pattern "org\.citra\.emu\.debug|AndroidRuntime|FATAL EXCEPTION|Fatal signal|backtrace|Abort message|UnsatisfiedLinkError"
```

### `citra` tag logs

```powershell
adb -s <serial> logcat -d -s citra
```

## Practical rules

### Device selection

- Always run `adb devices` first when the device state is uncertain.
- If more than one device/emulator is present, use `-s <serial>` on every command.

### Build vs runtime boundaries

- If the APK launches but the game does not render, do not assume build failure.
- If audio is present while the screen is black, emulation may be running and the fault may be in presentation.
- If standby or screen-off interrupts behavior, classify it as a lifecycle issue, not automatically as a crash.

### Logging discipline

- Clear logs before reproducing.
- Add targeted instrumentation only while the fault boundary is unknown.
- Remove or reduce heavy probes such as repeated framebuffer `glReadPixels` once the root cause is found, because they distort performance.

## Key historical findings from this repo

### Black screen root cause

- A decisive Android renderer fix was forcing `use_present_thread = false` before `EGLAndroid` creation.
- The failure was an EGL context/surface mismatch, not a ROM loading failure.

### Dynarmic guest hang

- A missing A32 exclusive-write callback path caused guest `STREX` loops to fail forever.
- Symptoms looked like a black-screen boot failure but were actually a CPU emulation issue upstream of graphics startup.

### Externals safety rule

- The current working Android runtime depends on dirty external states that are not yet normalized.
- Do not run cleanup or submodule-reset commands as part of ordinary Android debugging.

## When to prefer this workflow

Use this reference when the task involves:
- building the Android debug APK
- installing or updating the app on a device
- reproducing crashes or black-screen issues
- pulling logcat
- distinguishing build issues from runtime issues
