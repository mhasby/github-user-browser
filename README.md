# Github User Browser

## Description

A simple app to search Github users and see their public repo and gist

## Tech Stack & Architecture

This project is developed with SOLID and DRY principles in mind, following a simplified version of Clean Architecture
to ensure maintainability while keeping the codebase straightforward and easy to work with.

The core tech stack includes:
- **Hilt** for dependency injection
- **Kotlin Coroutines** for asynchronous and concurrent programming
- **Jetpack Compose** for building modern, declarative UI
- **Jetpack Navigation Compose** for seamless in-app navigation
- **Retrofit** for networking
- **DataStore** for local data persistence

Additionally, the project integrates a simple Flutter module as a proof of concept (POC),
demonstrating the ability to render a Flutter screen within the Android app.

## How to run the app

### Prerequisite

This project use AGP 8.8.2 and also flutter module, so here is the prerequisite to run it:
- **Android Studio** | Recommended Version: Android Studio Koala | 2024.1.1 (or newer)
- **JDK** | Required JDK Version: JDK 17
- **Gradle** | Gradle Version: At least 8.5
- **Flutter** | Recommended Flutter version: 3.29.1 | Minimum Flutter version: 3.19.0 (must support Dart 3.7.0)
- **Android device or emulator** | Minimum SDK 24
- **Github Personal Access Token** | Recommended to increase the rate limit

### Run the app
- Clone this repository and open the project in Android Studio
- Go to `github_gist_browser` folder and run command `flutter pub get`
- Copy `sample.env` to `.env` and add Github Personal Access Token
- Sync the project with gradle and run the app
