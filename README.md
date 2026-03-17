**BEFORE FILING AN ISSUE, READ THE RELEVANT SECTION IN THE [CONTRIBUTING](https://github.com/citra-emu/citra/wiki/Contributing#reporting-issues) FILE!!!**

Citra
==============
[![Travis CI Build Status](https://travis-ci.com/citra-emu/citra.svg?branch=master)](https://travis-ci.com/citra-emu/citra)
[![AppVeyor CI Build Status](https://ci.appveyor.com/api/projects/status/sdf1o4kh3g1e68m9?svg=true)](https://ci.appveyor.com/project/bunnei/citra)
[![Bitrise CI Build Status](https://app.bitrise.io/app/4ccd8e5720f0d13b/status.svg?token=H32TmbCwxb3OQ-M66KbAyw&branch=master)](https://app.bitrise.io/app/4ccd8e5720f0d13b)
[![Discord](https://img.shields.io/discord/220740965957107713?color=%237289DA&label=Citra&logo=discord&logoColor=white)](https://discord.gg/FAXfZV9)

Citra is an experimental open-source Nintendo 3DS emulator/debugger written in C++. It is written with portability in mind, with builds actively maintained for Windows, Linux and macOS.

Citra emulates a subset of 3DS hardware and therefore is useful for running/debugging homebrew applications, and it is also able to run many commercial games! Some of these do not run at a playable state, but we are working every day to advance the project forward. (Playable here means compatibility of at least "Okay" on our [game compatibility list](https://citra-emu.org/game).)

Citra is licensed under the GPLv2 (or any later version). Refer to the license.txt file included. Please read the [FAQ](https://citra-emu.org/wiki/faq/) before getting started with the project.

Check out our [website](https://citra-emu.org/)!

Need help? Check out our [asking for help](https://citra-emu.org/help/reference/asking/) guide.

For development discussion, please join us on our [Discord server](https://citra-emu.org/discord/) or at #citra-dev on freenode.

### Maintained Fork Status

This checkout is the maintained local fork with Android `arm64-v8a` as the primary target.

Current verified state:
- Android debug APK builds from CLI, installs, and runs on a physical Galaxy S24+.
- Submodules are now either normalized to clean pinned commits or intentionally pinned compatibility snapshots.
- Remaining intentional non-clean upstream states:
  - `externals/boost` at `4cc38a77d7c5bfd0c73e3ceef8ef54e64387a2a2`
  - `externals/nihstro` at `c9af0af155514b5c12a6f2d9e2b10fb98ec66750`

For repo-operational history and decisions, see:
- `.codex/docs/HANDOVER.md`
- `.codex/docs/INSIGHTS.md`
- `.codex/docs/DECISIONS.md`
- `EXTERNALS_PRESERVATION.md`

### Android Bootstrap

This is the currently verified Windows CLI path for a fresh checkout.

Prerequisites:
- JDK on `PATH` or `JAVA_HOME` set to a valid JDK
- Android SDK installed and reachable through `src/android/local.properties`
- Android SDK components matching the tracked Gradle config:
  - NDK `29.0.14206865`
  - CMake `3.31.6`
  - platform-tools / `adb`
- Git submodules initialized

Setup:
```powershell
git submodule update --init --recursive
```

Machine-local Android SDK path:
- Create or update `src/android/local.properties`
- Example:

```properties
sdk.dir=C:\\Users\\<you>\\AppData\\Local\\Android\\Sdk
```

Verified build command:
```powershell
cd src/android
cmd /c gradlew.bat :app:assembleDebug --stacktrace
```

Verified clean build command:
```powershell
cd src/android
cmd /c gradlew.bat clean :app:assembleDebug --stacktrace
```

Built APK:
```text
src/android/app/build/outputs/apk/debug/app-debug.apk
```

Optional helper tasks from the repo root:
```powershell
task build-debug-apk
task build-debug-apk-clean
task install-debug-apk
task deploy-debug-apk
```

Notes:
- Android builds currently use `ENABLE_WEB_SERVICE=0`, so `externals/libressl` is not on the active runtime path.
- Do not rewind `externals/boost` or `externals/nihstro` casually; both are currently required for Android toolchain compatibility.

### Development

Most of the development happens on GitHub. It's also where [our central repository](https://github.com/citra-emu/citra) is hosted.

If you want to contribute please take a look at the [Contributor's Guide](https://github.com/citra-emu/citra/wiki/Contributing) and [Developer Information](https://github.com/citra-emu/citra/wiki/Developer-Information). You should also contact any of the developers in the forum in order to know about the current state of the emulator because the [TODO list](https://docs.google.com/document/d/1SWIop0uBI9IW8VGg97TAtoT_CHNoP42FzYmvG1F4QDA) isn't maintained anymore.

If you want to contribute to the user interface translation, please checkout [citra project on transifex](https://www.transifex.com/citra/citra). We centralize the translation work there, and periodically upstream translation.

### Building

* __Windows__: [Windows Build](https://github.com/citra-emu/citra/wiki/Building-For-Windows)
* __Linux__: [Linux Build](https://github.com/citra-emu/citra/wiki/Building-For-Linux)
* __macOS__: [macOS Build](https://github.com/citra-emu/citra/wiki/Building-for-macOS)


### Support
We happily accept monetary donations or donated games and hardware. Please see our [donations page](https://citra-emu.org/donate/) for more information on how you can contribute to Citra. Any donations received will go towards things like:
* 3DS consoles for developers to explore the hardware
* 3DS games for testing
* Any equipment required for homebrew
* Infrastructure setup

We also more than gladly accept used 3DS consoles! If you would like to give yours away, don't hesitate to join our [Discord server](https://citra-emu.org/discord/) and talk to bunnei.
