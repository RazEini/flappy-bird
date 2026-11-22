# Flappy Bird for Android

<div align="center">

<img src="assets/Screen_recording_20251013_144403.gif" alt="Flappy Bird gameplay demo" width="250">

**A polished Flappy Bird-style Android game built with Kotlin and Jetpack Compose.**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material-3-6200EE)](https://m3.material.io/)
[![Android](https://img.shields.io/badge/Android-7.0%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

[Features](#features) · [Screens](#game-screens) · [Getting Started](#getting-started) · [Controls](#controls) · [Project Structure](#project-structure) · [Building](#building)

</div>

---

## Overview

**Flappy Bird** is a native Android game with smooth Compose UI, animated sprites, dynamic obstacles, score tracking, and a simple lives system. The game adapts to the available screen size and includes dedicated start, gameplay, and game-over states.

## Features

| Category | Feature |
|:--|:--|
| Gameplay | Tap-to-flap physics with gravity and jump velocity |
| Obstacles | Dynamically generated pipes with randomized and smoothly varying heights |
| Scoring | Live score and high score tracking |
| Lives | Three lives with temporary invincibility after a hit |
| Animation | Animated bird sprites on the start screen and during gameplay |
| Interface | Jetpack Compose UI with Material 3 components and responsive layouts |
| Screens | Start, playing, and game-over states |
| Graphics | Canvas-rendered pipes and Android drawable assets |

## Game Screens

| Start | Gameplay | Game Over |
|:--:|:--:|:--:|
| Select **START** to begin | Tap to keep the bird airborne | Tap anywhere to return to the start screen |

<p align="center">
  <img src="assets/Screen_recording_20251013_144403.gif" alt="Gameplay recording" width="250">
</p>

## Controls

| State | Action |
|:--|:--|
| Start screen | Tap the **START** button or anywhere on the screen |
| Playing | Tap the screen to make the bird jump |
| Game over | Tap anywhere to return to the start screen |

The score increases once whenever the bird passes a pipe successfully. A pipe or boundary collision removes one life. The player has three lives and loses the game after the third hit.

## Getting Started

### Prerequisites

- Android Studio with support for AGP 8.13
- JDK 11
- Android SDK 35
- Android 7.0 (API 24) or newer
- An emulator or a physical Android device

### Clone the Repository

```bash
git clone https://github.com/RazEini/flappy-bird.git
cd flappy-bird
```

### Run in Android Studio

1. Open the cloned directory in Android Studio.
2. Wait for Gradle to synchronize.
3. Select an emulator or connected device.
4. Click **Run**.

## Building

### Debug APK

**Linux or macOS**

```bash
./gradlew assembleDebug
```

**Windows**

```powershell
.\gradlew.bat assembleDebug
```

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Run Tests

```bash
./gradlew test
```

Instrumented tests require a running emulator or physical device:

```bash
./gradlew connectedAndroidTest
```

## Project Structure

```text
flappy-bird/
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/example/flappybird/
│       │   │   ├── MainActivity.kt
│       │   │   └── ui/theme/
│       │   ├── res/
│       │   │   ├── drawable/
│       │   │   └── values/
│       │   └── AndroidManifest.xml
│       ├── test/
│       └── androidTest/
├── assets/
│   └── Screen_recording_20251013_144403.gif
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/libs.versions.toml
```

## Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Design:** [Material 3](https://m3.material.io/)
- **Build System:** Gradle with Kotlin DSL
- **Rendering:** Android `Canvas` and Compose `Image`
- **Concurrency:** Kotlin Coroutines

## License

This project is licensed under the [MIT License](LICENSE).

---

<div align="center">

Created and maintained by **[Raz Eini](https://github.com/RazEini)**

</div>
