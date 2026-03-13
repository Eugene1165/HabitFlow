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

```kotlin
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
Уровень реализации: Middle
Формат работы: обучающий, без генерации готового кода

🎯 1. ТВОЯ РОЛЬ

Ты — технический наставник Android-разработчика.

Ты:
- Идею проекта модернизируешь,
- объясняешь архитектуру,
- проверяешь решения,
- задаёшь наводящие вопросы,
- помогаешь мыслить системно,
- проводишь code review,
- указываешь на архитектурные ошибки,
- помогаешь формализовать бизнес-логику.
- Пишем код исключительно в функциональном стиле

Ты НЕ:
- пишешь готовые файлы,
- генерируешь полностью классы,
- создаёшь copy-paste решения,
- реализуешь фичи за разработчика,
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
| Unit-тесты (статистика, toggle) | ✅ Готов (11 тестов: GetHabitsStatisticsUseCase x5, ToggleHabitEntryUseCase x4, GetAllHabitsStatisticsUseCase x2) |
| Retrofit / синхронизация Habits | ✅ Готов (offline-first, Supabase) |
| Retrofit / синхронизация HabitEntries | ✅ Готов (addEntry/updateEntry работают, данные пишутся в Supabase при toggle) |
| Синхронизация archiveHabit / restoreHabit | ✅ Готов (PATCH через updateEntriesById, offline-first порядок) |
| Сетевое логирование | ✅ Готов (HttpLoggingInterceptor BODY в NetworkModule) |
| Instrumented тесты (Kaspresso) | ✅ 21 тест (все passing): NavigationTest x4, HabitsListTest x4, HabitFormTest x2, HabitInfoTest x3, ArchivedScreenTest x3 (отображение, восстановление, удаление), SettingsScreenTest x3 (отображение, тема, день недели), StatisticsScreenTest x1, CalendarScreenTest x1; AllTestsSuite — единая точка запуска; FakeHabitRepository + FakeHabitEntryRepository + FakeUserPreferencesRepository + FakeReminderScheduler; TestRepositoryModule replaces RepositoryModule + WorkManagerModule |
| Database migrations | ✅ Готов (exportSchema = true, ksp schemaLocation = $projectDir/schemas, app/schemas/2.json в git, MIGRATION_1_2 — добавление isSynced в habit_entries) |
| Retry синхронизации HabitEntries | ✅ Готов (markAsSynced(id) + getUnsyncedEntries() в DAO; addEntry/updateEntry помечают isSynced=true после успешного API; getEntriesForHabit onStart retry всех isSynced=false записей) |
| Dark theme (компоненты) | ✅ Готов (ColorPicker: FlowRow→LazyRow, hardcoded Color.Black→onSurface; HabitCard: убран Color.White контейнер, Color.Black/Gray→Material theme цвета) |
| ViewModel рефакторинг (stateIn) | ✅ Готов (StatisticsViewModel, ArchivedViewModel, CalendarViewModel, SettingsViewModel — переведены на stateIn(); HabitFormViewModel, HabitInfoViewModel, OnBoardingViewModel — оставлены MutableStateFlow по архитектурным причинам) |
| Unit-тесты (финальная проверка) | ✅ Готов (11 тестов покрывают всю бизнес-логику; новые методы DAO не требуют unit-тестов — тестируются на уровне instrumented) |

---

## Roadmap: Junior+ → Middle+

### Блок 1 — Архитектура и обработка ошибок (критично)

**1.1 Result wrapper pattern**
Текущая проблема: ошибки глотаются в пустых `catch {}` блоках по всему коду.
Middle+ решение: ввести `sealed class Result<T>` (Success / Error / Loading) на уровне Domain.
Репозитории возвращают `Flow<Result<T>>` вместо `Flow<T>`.
ViewModels маппируют Result → UiState без try/catch.
Цель: единая стратегия обработки ошибок через все слои.

**1.2 Background sync через WorkManager**
Текущая проблема: retry синхронизации происходит только при открытии конкретного экрана.
Middle+ решение: `SyncWorker` — отдельный Worker который при запуске приложения находит все `isSynced = false` записи и отправляет их на сервер.
Запускается через `OneTimeWorkRequest` при старте приложения.

**1.3 Conflict resolution стратегия**
Текущая проблема: при конфликте локально vs Supabase — неясно кто победит.
Middle+ решение: добавить поле `updatedAt: LocalDateTime` в `HabitEntry`. При синхронизации сравнивать timestamps — побеждает последнее изменение (Last Write Wins).

---

### Блок 2 — Новые фичи (бизнес-логика)

**2.1 Поиск и фильтрация привычек**
`HabitsListScreen` — поле поиска по названию.
Фильтр по типу повторения (Daily / Weekly).
Реализация: `MutableStateFlow<String>` для query + `combine` с основным Flow привычек.
Цель: отработать реактивный поиск без дополнительных запросов к БД.

**2.2 Достижения (Achievements)**
Новый Domain model: `Achievement(id, title, description, unlockedAt?)`.
Логика разблокировки: streak 7 дней, streak 30 дней, 10 привычек выполнено, 100% неделя.
Новый UseCase: `GetAchievementsUseCase` — вычисляет достижения из существующей статистики.
Цель: сложная бизнес-логика без нового API.

**2.3 Виджет статистики на главном экране (Home Screen Widget)**
`AppWidget` с Glance (Jetpack) — показывает привычки на сегодня + прогресс.
Цель: опыт работы с AppWidget API + Glance Compose.

---

### Блок 3 — Качество кода (Middle обязательно знает)

**3.1 Timber для логирования**
Заменить `Log.d/e` и пустые catch на структурированное логирование через Timber.
В debug — логировать все ошибки синхронизации.
В release — только критические ошибки.

**3.2 Проверка производительности**
`HabitsListScreen` — профилировать через Android Studio Profiler.
Убедиться что `LazyColumn` не ребилдит все элементы при toggle одной привычки.
Добавить `key { habit.id }` в `LazyColumn` если отсутствует.

**3.3 Lint + Detekt**
Настроить статический анализ кода.
Добавить `detekt.yml` с правилами для проекта.
Цель: автоматическая проверка архитектурных нарушений.

---

### Блок 4 — Тестирование (Middle пишет тесты самостоятельно)

**4.1 Unit-тесты для Result wrapper**
После введения Result — переписать существующие 11 тестов под новый контракт.
Добавить тесты для Error сценариев (сейчас не покрыты).

**4.2 Unit-тесты для Achievement логики**
`GetAchievementsUseCaseTest` — минимум 6 тестов:
streak 7 дней разблокирует достижение, streak < 7 не разблокирует, и т.д.

**4.3 Integration тесты для Room**
`HabitEntryDaoTest` — instrumented тест для `markAsSynced` + `getUnsyncedEntries`.
Цель: убедиться что SQL запросы работают корректно.

---

### Целевой результат Middle+

| Критерий | Сейчас | Цель |
|---|---|---|
| Архитектурные решения | С подсказками | Самостоятельно |
| Обработка ошибок | Пустые catch | Result pattern |
| Тесты | С помощью | Самостоятельно |
| Бизнес-логика | Понимает | Проектирует сам |
| Flow операторы | Базовые | Сложные уверенно |
| Отладка | Наугад | Системно |