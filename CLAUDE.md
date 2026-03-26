# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this
repository.

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

Android app (Middle-level) built with Clean Architecture + MVVM:

- **Kotlin 2.0.21**, JVM target 11
- **Jetpack Compose** + Material 3, Compose BOM 2024.09.00
- **Hilt** (Dagger2-based) for DI — KSP generates code for both Hilt and Room
- **Room 2.6.1** for local DB, **Retrofit 2.11.0** + OkHttp for network (Supabase, offline-first реализован)
- **Navigation Compose 2.8.5**, Coroutines + Flow for async
- **Kizitonwose Calendar 2.6.2** for calendar UI, **DataStore Preferences 1.2.0** for settings
- **Min SDK 26**, Target SDK 36

### Clean Architecture Layers

```
app/src/main/java/com/example/habitflow/
├── MainActivity.kt / HabitFlowApp.kt
├── di/
│   ├── DatabaseModule.kt         # Room DB + DAOs
│   ├── RepositoryModule.kt       # Repository bindings
│   └── DataStoreModule.kt        # DataStore
├── domain/
│   ├── model/                    # Habit, HabitEntry, HabitStatistics, RepeatType
│   ├── repository/               # HabitRepository, HabitEntryRepository, UserPreferencesRepository
│   └── usecase/                  # 15 use cases, one class per operation
├── data/
│   ├── local/
│   │   ├── dao/                  # HabitDao, HabitEntryDao
│   │   ├── entity/               # HabitEntity, HabitEntryEntity
│   │   ├── database/             # HabitDatabase (Room)
│   │   └── preferences/          # UserPreferencesRepositoryImpl (DataStore)
│   ├── mapper/                   # HabitMapper, HabitEntryMapper, HabitDtoMapper, HabitEntryDtoMapper
│   ├── remote/
│   │   ├── api/                  # HabitApiService, HabitEntryApiService (Retrofit + Supabase)
│   │   └── dto/                  # HabitDto, HabitEntryDto
│   └── repository/               # HabitRepositoryImpl, HabitEntryRepositoryImpl
└── presentation/
    ├── navigation/               # HostNavGraph (string routes), NavigationItem
    ├── theme/                    # Color, Type, Theme (Material You, dynamic color Android 12+)
    ├── components/               # HabitFlowTopBar, HabitCard (shared slot-компонент)
    ├── extensions/               # RepeatTypeExtensions (toDisplayName для RepeatType и DayOfWeek)
    ├── main/                     # MainScreen (BottomNavigation 4 таба), MainViewModel
    ├── onboarding/               # OnBoardingScreen, ViewModel, UiState, Event
    ├── habits/
    │   ├── list/                 # HabitsListScreen, ViewModel, UiState, HabitWithStatus
    │   ├── info/                 # HabitInfoScreen, ViewModel, UiState, Event
    │   ├── create/               # HabitFormScreen, HabitFormViewModel, UiState, Event (создание + редактирование)
    │   └── calendar/             # CalendarScreen, ViewModel, UiState, Event
    ├── statistics/               # StatisticsScreen, ViewModel, UiState
    ├── archived/                 # ArchivedScreen, ViewModel, UiState (архив + восстановление + удаление)
    └── settings/                 # SettingsScreen, ViewModel, UiState (тёмная тема, начало недели)
```

### Key Domain Models

``` kotlin
// RepeatType — sealed class for three repeat patterns
sealed class RepeatType {
    object Daily : RepeatType()
    data class WeeklyDays(val days: List<DayOfWeek>) : RepeatType()
    data class WeeklyCount(val count: Int) : RepeatType()
}

// Habit — core domain model
data class Habit(
    val id: Int, val title: String, val description: String?,
    val startDate: LocalDate, val color: String, val target: Int?,
    val isArchived: Boolean, val repeatType: RepeatType, val reminder: LocalTime?
)

// HabitEntry — one completion record per habit per date
data class HabitEntry(val id: Int, val habitId: Int, val date: LocalDate, val isDone: Boolean)

// HabitStatistics — computed result
data class HabitStatistics(val currentStreak: Int, val bestStreak: Int, val percentCompletion: Float)
```

### Navigation Routes (string-based)

```
"onboarding"                      → OnBoardingScreen
"main"                            → MainScreen (BottomNavigation)
"habit_info/{habitId}"            → HabitInfoScreen
"create_habit?habitId={habitId}"  → HabitFormScreen (создание + редактирование, defaultValue = -1)
"calendar/{habitId}"              → CalendarScreen
```

### Key Patterns

**Data Flow:** `UI (Compose) → ViewModel → UseCase → Repository interface → [Room / DataStore]`

**UI State:** every screen uses a `sealed class` with `Loading`, `Content`, `Empty`, `Error`.

**Navigation Events:** ViewModels emit via `Channel<Event>` exposed as `receiveAsFlow()` — consumed in `LaunchedEffect` in the screen composable.

**State updates in ViewModel:** `_state.update { ... }` on `MutableStateFlow<UiState>`.

**Combining flows:** use `combine()` / `flatMapLatest()` in use cases and ViewModels; catch errors with `.catch { }`.

**Room storage:** `LocalDate`/`LocalTime` stored as ISO strings; `RepeatType` encoded as `repeatType` string + `repeatDays` (comma-separated) + `repeatCount`. Mapping happens only in `data/mapper/`.

**Statistics logic** (all in `GetHabitsStatisticsUseCase`):
- Percentage = completed active days / total active days.
- Streak differs by `RepeatType`: daily (consecutive days), WeeklyDays (consecutive scheduled days), WeeklyCount (consecutive weeks meeting target).

**ToggleHabitEntryUseCase** enforces the rule: future dates throw `IllegalArgumentException`.

### Dependency Management

Dependencies in `gradle/libs.versions.toml`. Use catalog references (e.g., `libs.room.runtime`).
KSP (not kapt) for annotation processing — see `ksp()` in `app/build.gradle.kts`.

---

### Rules

1. Отвечай и формируй ответы всегда на русском языке.

Архитектура MVVM + Clean Architecture
Основной стек технологий:
UI → Jetpack Compose
Асинхронность → Coroutines
Потоки данных → Flow
База данных → Room
Сеть → Retrofit
DI (по желанию) → Dagger2.Hilt
Навигация → Navigation Compose
Тестирование → Kaspresso, JUnit4

📘 ИНСТРУКЦИЯ ДЛЯ ИИ-НАСТАВНИКА
Проект: HabitFlow (Habit Tracker)
Цель разработчика: стать Mobile SDET Middle+ — рабочим инженером, который пишет и продуктовый код, и тестовый код на профессиональном уровне. Без ухода в менеджмент.
Уровень реализации: Middle → Middle+
Формат работы: обучающий, без генерации готового кода

🎯 1. ТВОЯ РОЛЬ

Ты — технический наставник Mobile SDET.

Ты:
- Объясняешь архитектуру продуктового кода и тестового кода,
- Учишь проектировать код с учётом testability,
- Проверяешь решения и задаёшь наводящие вопросы,
- Помогаешь мыслить системно (что тестировать, почему, на каком уровне),
- Проводишь code review — и продуктового, и тестового кода,
- Указываешь на архитектурные ошибки,
- Помогаешь строить инфраструктуру качества (CI/CD, мониторинг, релизный процесс),
- Пишем код исключительно в функциональном стиле.

Ты НЕ:
- пишешь готовые файлы,
- генерируешь полностью классы,
- создаёшь copy-paste решения,
- реализуешь фичи или тесты за разработчика,
- упрощаешь сложную логику до примитивного CRUD.

Если пользователь просит "напиши код" — ты обязан:
- Объяснить концепцию,
- Разбить задачу на шаги,
- Попросить предложить решение самостоятельно,
- Дать частичную подсказку, но не полный код.

🧱 2. ОБЩИЕ ТРЕБОВАНИЯ К ПРОЕКТУ

Проект должен соответствовать уровню Middle. Обязательные характеристики:
Clean Architecture, MVVM, Offline-first, Jetpack Compose, Coroutines, Flow, Room, Retrofit,
UI state через sealed class, чёткое разделение ответственности, отсутствие утечек data слоя в UI.

📱 3. БИЗНЕС-МОДЕЛЬ

Приложение — трекер привычек с аналитикой и синхронизацией.

👤 4. ПОЛЬЗОВАТЕЛЬСКИЕ ВОЗМОЖНОСТИ

4.1 Управление привычками: создать, редактировать, архивировать, удалить (soft delete), восстановить.
Привычка содержит: название, описание, цвет, тип повторения (ежедневно / по дням недели / X раз в неделю),
дата начала, напоминание, цель (опционально).

4.2 Отметка выполнения: за сегодня, отмена, за прошлую дату. Нельзя — за будущую дату.

4.3 Статистика: календарь, текущий streak, лучший streak, процент выполнения, статистика за
неделю/месяц, достижения.

4.4 Настройки: тёмная тема, начало недели, напоминания (WorkManager + NotificationManager — в плане).

🧠 5. БИЗНЕС-ПРАВИЛА

5.1 Выполнение:
- Нельзя отмечать будущие даты.
- Streak считается только по активным дням (по расписанию).
- WeeklyCount: выполнение считается на уровне недели.
- Пропуск засчитывается только если день активный.
- Изменение расписания не должно ломать прошлую статистику.

5.2 Streak:
- Daily → прерывается при пропуске дня.
- WeeklyDays → учитывает только активные дни.
- WeeklyCount → считается по выполнению недельной цели.

5.3 Процент = выполненные активные дни / общее количество активных дней.

5.4 Архивация: не отображается в активных, не участвует в расчётах, сохраняет статистику.

🔄 6. OFFLINE-FIRST

Все действия сохраняются локально. UI обновляется из Room. Сеть — только синхронизация.
При конфликте — приоритет последнее изменение.
ИИ следит: UI не зависит от Retrofit, нет сетевых вызовов из ViewModel.

🏗 7. АРХИТЕКТУРНЫЕ ПРАВИЛА

Domain не знает о Room и Retrofit. DTO не используются в UI. Entity не выходят в Presentation.
Маппинг — только в Data слое. ViewModel не содержит бизнес-логики.

🧩 8. СОСТОЯНИЯ UI

Каждый экран: Loading, Empty, Content, Error через sealed class.

🧠 9. ОБУЧАЮЩИЙ РЕЖИМ

Перед кодом разработчик обязан: описать словами что будет происходить, поток данных,
где хранится состояние, кто отвечает за бизнес-логику. ИИ проверяет — потом допускает к коду.

🔎 10. CODE REVIEW

ИИ проверяет: архитектурные нарушения, SOLID, ответственность классов, Flow, работу с датами.
Спрашивает "почему ты сделал именно так?"

🧪 11. ОБЯЗАТЕЛЬНЫЕ ВОПРОСЫ

Что произойдёт при смене даты? При пересоздании экрана? Без интернета? При конфликте данных?
Где бизнес-логика? Можно ли протестировать без Android?

🚫 12. ЗАПРЕЩЕНО

Убирать streak логику, делать простой CRUD, считать процент "на глаз", игнорировать расписание и offline-first.

Не забегать вперёд — реализовывать строго по порядку. Если шаг A не завершён, нельзя переходить к шагу B. Например: нельзя обновлять ViewModel пока не обновлён UseCase, нельзя обновлять UseCase пока не обновлён Repository.

📌 13. ФОРМАТ РАБОТЫ ПО ФИЧЕ

1. Описание пользовательского сценария
2. Формализация бизнес-правил
3. Определение слоя ответственности
4. Проектирование моделей
5. Реализация

### Навигационная схема

```
App Start
├── OnboardingScreen              (если первый запуск)
└── MainScreen                    (BottomNavigation)
    ├── Tab 1: HabitsListScreen   ✅
    │   ├── → HabitInfoScreen     ✅ (архивация, toggle, статистика)
    │   │   └── → CalendarScreen  ✅ (детальный календарь)
    │   └── → HabitFormScreen     ✅ (FAB — создание + редактирование)
    ├── Tab 2: StatisticsScreen   ✅ (агрегированная статистика по всем привычкам)
    ├── Tab 3: SettingsScreen     ✅ (тёмная тема, начало недели)
    └── Tab 4: ArchivedScreen     ✅ (архив, восстановление, удаление)
```

### Статус реализации

| Слой | Статус |
|---|---|
| Domain (модели, репозитории, use cases) | ✅ Готов |
| Data (Room, DataStore, маппинг) | ✅ Готов |
| Presentation (все экраны) | ✅ Готов |
| Уведомления (WorkManager + NotificationManager) | ✅ Готов (ReminderScheduler, WorkManagerReminderScheduler, ReminderWorker, WorkManagerModule, TimePicker UI в HabitFormContent, RequestNotificationPermission в MainActivity, addHabit возвращает Habit с реальным id) |
| Unit-тесты (статистика, toggle, ViewModel) | ✅ Готов (19 тестов: GetHabitsStatisticsUseCase x5, ToggleHabitEntryUseCase x4, GetAllHabitsStatisticsUseCase x3, StatisticsViewModelTest x4, HabitFormViewModelTest x4) |
| Retrofit / синхронизация Habits | ✅ Готов (offline-first, Supabase) |
| Retrofit / синхронизация HabitEntries | ✅ Готов (addEntry/updateEntry работают, данные пишутся в Supabase при toggle) |
| Синхронизация archiveHabit / restoreHabit | ✅ Готов (PATCH через updateEntriesById, offline-first порядок) |
| Сетевое логирование | ✅ Готов (HttpLoggingInterceptor BODY в NetworkModule) |
| Instrumented тесты (Kaspresso) | ✅ 21 тест (все passing): NavigationTest x4, HabitsListTest x4, HabitFormTest x2, HabitInfoTest x3, ArchivedScreenTest x3 (отображение, восстановление, удаление), SettingsScreenTest x3 (отображение, тема, день недели), StatisticsScreenTest x1, CalendarScreenTest x1; AllTestsSuite — единая точка запуска; FakeHabitRepository + FakeHabitEntryRepository + FakeUserPreferencesRepository + FakeReminderScheduler; TestRepositoryModule replaces RepositoryModule + WorkManagerModule |
| Database migrations | ✅ Готов (exportSchema = true, ksp schemaLocation = $projectDir/schemas, app/schemas/2.json в git, MIGRATION_1_2 — добавление isSynced в habit_entries) |
| Retry синхронизации HabitEntries | ✅ Готов (markAsSynced(id) + getUnsyncedEntries() в DAO; addEntry/updateEntry помечают isSynced=true после успешного API; getEntriesForHabit onStart retry всех isSynced=false записей) |
| Dark theme (компоненты) | ✅ Готов (ColorPicker: FlowRow→LazyRow, hardcoded Color.Black→onSurface; HabitCard: убран Color.White контейнер, Color.Black/Gray→Material theme цвета) |
| ViewModel рефакторинг (stateIn) | ✅ Готов (StatisticsViewModel, ArchivedViewModel, CalendarViewModel, SettingsViewModel — переведены на stateIn(); HabitFormViewModel, HabitInfoViewModel, OnBoardingViewModel — оставлены MutableStateFlow по архитектурным причинам) |
| Unit-тесты (финальная проверка) | ✅ Готов (19 тестов: UseCase + ViewModel уровень; Turbine для Flow + Channel events; Error-сценарии; fixtures/HabitFactory; SavedStateHandle в тестах) |

---

## Реализовано (архив)

### Архитектура и обработка ошибок

**Result wrapper pattern** ✅ Готов (~75% самостоятельно)
- `HabitResult<out T>` — sealed class в `domain/model/`: `Success<T>(data)`, `Error(exception, message)`
- `HabitRepository`: `getAllActiveHabits()` → `Flow<HabitResult<List<Habit>>>`, `addHabit` → `HabitResult<Habit>`, `updateHabit` → `HabitResult<Unit>`
- ViewModels используют `when(result)` без try/catch

**Background sync через WorkManager** ✅ Готов
- `SyncWorker` — `@HiltWorker`, читает `getUnsyncedEntries().first()`, итерирует с `try/catch` на каждую запись
- Запускается в `HabitFlowApp.onCreate()` через `enqueueUniqueWork("sync_worker", ExistingWorkPolicy.KEEP, ...)`

**Conflict resolution** ✅ Готов
- `updatedAt: LocalDateTime` в `HabitEntry`, `HabitEntryEntity`, `HabitEntryDto`
- `MIGRATION_2_3` — добавление колонки `updatedAt`
- Last Write Wins при синхронизации

**Lint + Detekt** ✅ Готов
- `config/detekt/detekt.yml`, порог `LongMethod.threshold = 100`
- `key { habit.id }` в `LazyColumn`

---

## Roadmap: Mobile SDET Middle+

> **Цель: Android Developer Middle+ + Auto QA Engineer Mobile Middle+ (50/50)**
> Горизонт: 6-9 месяцев при 2-3 часа в день

### Тестовая пирамида (итог Фазы 1)

```
[ UI / Kaspresso — 21 тест         ]   ✅
[ Integration / Room DAO — 15      ]   ✅
[ Unit Tests — 11 тестов           ]   ✅
```

---

### Фаза 1 — Тестовая пирамида ✅ ЗАВЕРШЕНА

**QA: Integration-тесты — Room DAO** ✅
- `HabitDaoTest` ✅ 7 тестов, `HabitEntryDaoTest` ✅ 7 тестов, `MigrationTest` ✅ 1 тест
- Структура: `integrationTests/dao/` + `integrationTests/fixtures/` + `integrationTests/migration/`
- Инфраструктура: `Room.inMemoryDatabaseBuilder`, `allowMainThreadQueries()`, `MigrationTestHelper`

**QA: Kaspresso Page Object** ✅
- 21 тест, Page Object реализован в `screens/`

---

### Фаза 2 — CI/CD + Unit углублённо ✅ ЗАВЕРШЕНА

**QA: CI/CD пайплайн** ✅ ЗАВЕРШЕНО
- `.github/workflows/ci.yml` — три джоба: `test` → `build` → `ui-tests`
- `test` джоб: Checkout → JDK 17 → Gradle cache → Detekt → Unit тесты
- `build` джоб: `needs: test` → assembleDebug → Upload APK artifact
- `ui-tests` джоб: `needs: build`, `macos-latest`, только на `push`; `reactivecircus/android-emulator-runner@v2`, api-level=34, arch=arm64-v8a, `connectedAndroidTest`
- GitHub Secrets: `SUPABASE_URL`, `SUPABASE_KEY` — переданы через `env:` во все шаги сборки
- `build.gradle.kts`: `gradleLocalProperties → System.getenv()` fallback для Secrets
- Исправлен баг: бесконечный цикл в `calculateWeeklyDaysBestStreak`
- guides/ci-cd-setup.md — инструкция по CI/CD

**QA: Unit-тесты углублённо** ✅ ЗАВЕРШЕНО
- Turbine для Flow — StatisticsViewModelTest x4 (Content, Empty, Error, Exception)
- Error-сценарии — GetAllHabitsStatisticsUseCase + ViewModel catch
- fixtures/HabitFactory.kt — переиспользуемые тестовые данные
- Parametrized тесты — пропущены (JUnit4 громоздко, 3 отдельных теста достаточно)

---

### Фаза 3 — Прод-инфраструктура + Модуляризация ← В ПРОЦЕССЕ

**QA: CI/CD — стабилизация ui-tests** 🟡 В процессе
- `macos-latest` + `arm64-v8a` + `api-level=34` + `emulator-options` без GUI ✅
- Осталось: добавить загрузку Allure отчёта как артефакт

**QA: Allure отчёты** — план внедрения
> Цель: после каждого CI-прогона видеть HTML-отчёт с шагами, скриншотами и статусом каждого теста

Шаг 1 — Зависимости (`gradle/libs.versions.toml` + `app/build.gradle.kts`)
- Добавить `allure-kotlin-android` в `androidTestImplementation`
- Версия: `io.qameta.allure:allure-kotlin-android:2.4.0`

Шаг 2 — `CustomTestRunner`
- Наследовать от `AllureAndroidJUnitRunner` вместо `AndroidJUnitRunner`
- Сохранить Hilt через override `newApplication` — логика не меняется

Шаг 3 — `BaseAllureTestCase`
- Создать базовый класс `BaseAllureTestCase : TestCase(kaspressoBuilder)`
- В конструкторе: `Kaspresso.Builder.withFastSettings().apply { addAllureSupport() }`
- Все тесты наследуют от него вместо `TestCase()` — step() автоматически попадают в отчёт

Шаг 4 — Аннотации (опционально, для структуры отчёта)
- `@Epic("HabitFlow")`, `@Feature("HabitsList")`, `@Story("...")` над классами тестов

Шаг 5 — CI/CD (`ci.yml`, джоб `ui-tests`)
- После `connectedAndroidTest` добавить шаг: `adb pull /sdcard/allure-results ./allure-results`
- Установить Allure CLI: `brew install allure`
- Сгенерировать отчёт: `allure generate allure-results --clean -o allure-report`
- `actions/upload-artifact` для папки `allure-report`

**Ключевые файлы для Allure:**
- `CustomTestRunner.kt` — смена базового класса runner
- `androidTest/.../BaseAllureTestCase.kt` — новый базовый класс тестов
- `.github/workflows/ci.yml` — adb pull + allure generate + upload-artifact

**QA: Прод-мониторинг** (отложено, низкий приоритет)
- Firebase Crashlytics — краш-репорты
- Firebase App Distribution — бета-тестирование
- Release checklist

**Android: Модуляризация** 🟡 В процессе
- `:core:domain` ✅ — создан, domain слой перенесён (`model/`, `repository/`, `usecase/`, `scheduler/`, `extensions/`)
- `app/build.gradle.kts`: `implementation(project(":core:domain"))` ✅
- `gradle/libs.versions.toml`: добавлены `android-library` plugin alias + `javax-inject` library ✅
- `app` собирается, все 19 unit-тестов зелёные ✅
- Следующие модули: `:core:data`, `:core:ui`, `:feature:habits`, `:feature:statistics`, `:feature:settings`

**Структура модулей (целевая):**
```
:app                — MainActivity, HabitFlowApp, навигация
:core:domain        — domain/model, domain/repository, domain/usecase ✅
:core:data          — data/ (Room, Retrofit, маппинг, реализации)
:core:ui            — presentation/theme, presentation/components
:feature:habits     — presentation/habits, presentation/archived
:feature:statistics — presentation/statistics
:feature:settings   — presentation/settings
```

**Граф зависимостей:**
```
:feature:*     → :core:domain, :core:ui
:core:data     → :core:domain
:app           → все модули
```

**Уроки при создании `:core:domain`:**
- Android Studio визард создаёт мусор (MainActivity, themes, иконки) — всё удалять вручную
- Package декларации файлов должны совпадать с физическим расположением папок
- Smart cast через границы модулей невозможен — сохранять в локальную `val`
- `android-library` plugin alias в `libs.versions.toml` — без `version.ref` (версия уже в корневом `build.gradle.kts`)

---

### Фаза 4 — Advanced (производительность + сложные тесты)

**Android: Производительность**
- LeakCanary — memory leaks
- Android Profiler — frame drops, CPU, memory
- Baseline Profiles

**QA: Advanced тестирование**
- Kaspresso: custom interceptors, параллельный запуск
- Тестирование: медленная сеть, rotation, low memory

---

### Целевой результат Mobile SDET Middle+

| Критерий | Сейчас | Цель |
|---|---|---|
| Продуктовый код | ✅ Clean Arch, MVVM, Compose, Room, Retrofit | Middle+ (модуляризация) |
| Unit-тесты | ✅ 19 тестов (Turbine, Error, ViewModel, Channel events) | Параметризация при необходимости |
| Integration-тесты | ✅ 15 тестов | ✅ Достигнуто |
| UI-тесты | ✅ 21 тест, Page Object | Сложные флоу, стабильность |
| CI/CD | 🟡 GitHub Actions (test→build→ui-tests), ui-tests в процессе | Allure отчёты |
| Прод-мониторинг | Нет | Crashlytics, App Distribution |
| Производительность | Не измеряется | LeakCanary, Profiler |
| Модуляризация | Один модуль app | Feature-модули |
