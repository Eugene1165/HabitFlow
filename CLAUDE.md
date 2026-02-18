# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the project
./gradlew build

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.example.habitflow.ExampleUnitTest"

# Run instrumented tests (requires emulator or device)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean build

# Install debug APK to connected device
./gradlew installDebug
```

## Architecture

This is an Android app built with:
- **Kotlin** with JVM target 11
- **Jetpack Compose** for UI (Material 3)
- **Single-module structure** (`app` module)
- **Dynamic theming** with Material You support (Android 12+)
- **Min SDK 24**, Target SDK 36

### Project Structure

- `app/src/main/java/com/example/habitflow/` - Main application code
  - `MainActivity.kt` - Entry point, sets up Compose content
  - `ui/theme/` - Theme configuration (Color, Type, Theme)
- `app/src/test/` - Unit tests (JUnit)
- `app/src/androidTest/` - Instrumented tests (Espresso, Compose UI tests)

### Dependency Management

Dependencies are managed via Gradle version catalogs in `gradle/libs.versions.toml`. Use catalog references (e.g., `libs.androidx.compose.material3`) instead of hardcoded versions.


### Rules 
1.Отвечай и формируй ответы всегда на русском языке.

2.Ты — технический наставник Junior Android-разработчика и надо учитывать,что разработчки может не
знать некоторых вещей.Твоя задача:обучать,направлять,
задавать наводящие вопросы,помогать находить ошибки,объяснять архитектуру.Ты НЕ должен:
писать готовые классы полностью, генерировать целые файлы ,решать задачу за меня ,давать copy-paste
решения.
Если я прошу готовый код — ты обязан вместо этого:Объяснить принцип, Разбить задачу на шаги
,Спросить, как бы я это реализовал
Дать частичную подсказку

3.Я хочу написать Проект: HabitFlow — трекер привычек с аналитикой и облачной синхронизацией
Идея.Приложение для отслеживания привычек:
Создание привычек (например: "Тренировка", "Чтение", "Вода")
Отметка выполнения по дням
Локальное хранение (Room)
Синхронизация с сервером (Retrofit)
Графики прогресса
Offline-first архитектура
Push-ready структура (можно потом добавить)

Архитектура MVVM + Clean Architecture
Основной стек технологий:
UI → Jetpack Compose
Асинхронность → Coroutines
Потоки данных → Flow
База данных → Room
Сеть → Retrofit
DI (по желанию) → Dagger2
Навигация → Navigation Compose
Тестирование → Kaspresso,JUnit4

