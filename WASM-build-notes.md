# WASM Build Notes

## What I did

1. Identified that the build failed because the default Java runtime in the container was OpenJDK 25.0.2.
2. Installed OpenJDK 17 (`openjdk-17-jdk`) because this repository's Gradle/Kotlin build path is compatible with Java 17.
3. Ran the WebAssembly compile task from the repository root with Java 17:
   - `./gradlew :target_teavm_wasm_gc:assembleMainComponents --stacktrace`
4. Confirmed the build succeeded.
5. Ran the WASM client bundle task with Java 17:
   - `./gradlew :target_teavm_wasm_gc:makeMainWasmClientBundle --no-daemon --stacktrace`
6. Confirmed the bundle generation succeeded and the required output files were produced.

## Key details

- The issue was not with the scripts themselves; it was the Java runtime version used by Gradle.
- The successful build used `JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64`.
- The repo task names were executed from the root project using `:target_teavm_wasm_gc:...`.

## Setup Commands

```bash
sudo apt-get update -qq
sudo apt-get install -y openjdk-17-jdk
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"
chmod +x gradlew
```

## Compilation Commands

After setup, run these commands to compile the WASM bundle:

```bash
cd /workspaces/DARVY-EaglercraftX-1.8-workspace && export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && export PATH="$JAVA_HOME/bin:$PATH" && ./gradlew :target_teavm_wasm_gc:assembleMainComponents :target_teavm_wasm_gc:makeMainWasmClientBundle --no-daemon --stacktrace
```

The output files will be generated in `target_teavm_wasm_gc/javascript_dist/` and the bundle will be created as `EaglercraftX_1.8_WASM-GC_Offline_Download.html`.

git clone -b master https://github.com/VentisAzar/DARVY-EaglercraftX-1.8-workspace
