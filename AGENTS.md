# Repository Guidelines

## Project Structure & Module Organization
- Single Android application module in `app/`; top-level Gradle config lives at the repository root.
- Application code sits in `app/src/main/java/com/icon/aibrowserasistor/`; entry point `MainActivity.kt` with Compose theme helpers under `ui/theme/`.
- UI assets and configuration resources live in `app/src/main/res/` (values, drawables, mipmaps, XML).
- JVM unit tests belong in `app/src/test/java/`; device/emulator tests belong in `app/src/androidTest/java/` (see `ExampleInstrumentedTest.kt`).
- Prompt and design reference notes are under `tsc/`; keep generated artifacts out of `src/`.

## Build, Test, and Development Commands
- `./gradlew assembleDebug` — builds the debug APK.
- `./gradlew test` — runs JVM unit tests.
- `./gradlew connectedAndroidTest` — runs instrumented tests on an attached device/emulator.
- `./gradlew lint` — optional static checks; fix warnings before merging.
- Use Android Studio (Hedgehog or newer) with the bundled emulator for the fastest feedback loop.

## Coding Style & Naming Conventions
- Kotlin + Compose-first; use 4-space indentation, avoid trailing whitespace, prefer expression-bodied functions when clear.
- Classes/objects use UpperCamelCase; functions and properties use lowerCamelCase; constants use UPPER_SNAKE_CASE.
- Compose composables use noun phrases ending in `*Screen`/`*Card`; previews are annotated with `@Preview` and suffixed `Preview`.
- Resource files use lowercase snake_case (`ic_logo.png`, `activity_main.xml`); keep theme-related code under `ui/theme/`.
- Use Android Studio’s formatter; keep imports sorted by the IDE; avoid wildcard imports.

## Testing Guidelines
- Mirror production package structure under `test` and `androidTest`; suffix files with `Test` or `InstrumentedTest`.
- Cover new logic with JUnit4 tests; for Compose UI, rely on `androidx.compose.ui.test.junit4` semantics-based assertions.
- Keep tests deterministic: avoid thread sleeps, prefer idling resources and fakes.
- When fixing bugs, add regression tests or document why coverage is deferred in the PR.

## Commit & Pull Request Guidelines
- Repository lacks existing history; adopt Conventional Commits (e.g., `feat: add tabbed browser view`, `fix: handle null url`) with imperative subjects ≤72 characters.
- Include scope hints when helpful (e.g., `feat(app): ...`).
- PRs should summarize behavior changes, link issues/features, provide test evidence (`./gradlew test`, emulator screenshots for UI), and note configuration impacts.
- Keep PRs focused; split unrelated refactors into follow-ups.

## Security & Configuration Tips
- Never commit secrets; store API keys in `local.properties` or environment variables and access via Gradle properties.
- Review WebView/network changes for secure defaults (HTTPS-first, minimal permissions).
- Run `./gradlew clean` before release packaging to ensure reproducible outputs.
