# Repository Guidelines

## Project Structure & Module Organization
- This repository contains one Android app module in `app/`, with Gradle config at the root.
- Main Kotlin sources live in `app/src/main/java/com/icon/aibrowserasistor/`, with `MainActivity.kt` as the entry point.
- Compose theme code belongs in `app/src/main/java/com/icon/aibrowserasistor/ui/theme/`.
- Android resources are in `app/src/main/res/` (`values/`, `drawable/`, `mipmap/`, XML layouts/config).
- Local JVM tests are under `app/src/test/java/`; device/emulator tests are under `app/src/androidTest/java/`.
- Product notes and prompt references are in `tsc/`; keep generated artifacts out of `src/`.

## Build, Test, and Development Commands
- `./gradlew assembleDebug` — Build the debug APK.
- `./gradlew test` — Run JVM unit tests.
- `./gradlew connectedAndroidTest` — Run instrumented tests on a connected emulator/device.
- `./gradlew lint` — Run Android lint checks; resolve warnings before merging when possible.
- `./gradlew clean` — Clear build outputs before release packaging.
- Use Android Studio (Hedgehog or newer) for the quickest Compose and emulator feedback loop.

## Coding Style & Naming Conventions
- Use Kotlin with a Compose-first approach, 4-space indentation, and no trailing whitespace.
- Prefer expression-bodied functions when readability is improved.
- Use `UpperCamelCase` for classes/objects, `lowerCamelCase` for functions/properties, and `UPPER_SNAKE_CASE` for constants.
- Name composables as nouns, commonly ending with `Screen` or `Card`; preview functions should end with `Preview` and use `@Preview`.
- Keep resource names lowercase snake_case (for example, `ic_logo.png`); avoid wildcard imports.

## Testing Guidelines
- Mirror the production package structure in both test source sets.
- Name test files with `*Test` (unit) and `*InstrumentedTest` (device).
- Use JUnit4 for logic tests and `androidx.compose.ui.test.junit4` for Compose UI assertions.
- Keep tests deterministic: avoid `Thread.sleep`; prefer fakes and idling-aware approaches.
- Add regression coverage for bug fixes, or explain any deferred coverage in the PR.

## Commit & Pull Request Guidelines
- Follow Conventional Commits (for example, `feat: add tabbed browser view`, `fix: handle null url`).
- Keep subjects imperative and within ~72 characters; use scopes when useful (for example, `feat(app): ...`).
- PRs should describe behavior changes, link related issues, and include verification evidence (such as `./gradlew test`, `./gradlew lint`).
- Include emulator screenshots for visible UI changes.
- Keep each PR focused; move unrelated refactors to follow-up work.

## Security & Configuration Tips
- Never commit secrets. Keep keys in `local.properties` or environment variables, then read them via Gradle properties.
- Review WebView and networking changes for secure defaults (HTTPS-first and least-required permissions).
