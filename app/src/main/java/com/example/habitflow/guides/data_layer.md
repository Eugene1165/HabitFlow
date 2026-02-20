# Data Layer — HabitFlow

## Общая схема

```
Domain Layer
    HabitRepository (interface)
    HabitEntryRepository (interface)
           ↑ реализует
Data Layer
    RepositoryImpl
        ↓ вызывает DAO        ↓ вызывает Mapper
        DAO                   Mapper
        ↓ читает/пишет
        Room Database (SQLite)
```

---

## Из чего состоит Data Layer

### 1. HabitDatabase
- Singleton, точка входа к БД
- Содержит две таблицы через Entity
- Предоставляет DAO

```
HabitDatabase
  ├── habitDao()       → HabitDao
  └── habitEntryDao()  → HabitEntryDao
```

### 2. Entity — модель таблицы
- Зеркало строки в SQLite, не бизнес-объект
- Room не умеет LocalDate, RepeatType — всё хранится как String

| Поле | Тип | Пример значения |
|------|-----|-----------------|
| startDate | String | "2024-01-15" |
| repeatType | String | "DAILY" / "WEEKLY_DAYS" / "WEEKLY_COUNT" |
| repeatDays | String? | "MONDAY,WEDNESDAY" |
| reminder | String? | "08:00" |

HabitEntryEntity имеет @ForeignKey на HabitEntity:
- При удалении привычки все её записи удаляются (CASCADE)
- @Index(habitId) — ускоряет поиск записей по привычке

### 3. DAO — Data Access Object
- Интерфейс с SQL запросами
- Room генерирует реализацию через KSP автоматически

Два типа методов:

```kotlin
// Реактивный — живёт пока жив подписчик, Room сам шлёт новые данные
fun getAllActiveHabits(): Flow<List<HabitEntity>>

// Одноразовый — выполнится один раз в фоновом потоке
suspend fun getHabitById(habitId: Int): HabitEntity?
```

### 4. Mapper — конвертер между слоями
- Существует потому что Entity != Domain Model
- HabitEntity — структура для БД
- Habit — бизнес-объект с типизированными полями

```
HabitEntity  →  mapHabitEntityToHabit()  →  Habit
Habit        →  mapHabitToHabitEntity()  →  HabitEntity
```

Пример: RepeatType

```
Entity:  repeatType="WEEKLY_DAYS", repeatDays="MONDAY,WEDNESDAY"
             ↓ mapper
Domain:  RepeatType.WeeklyDays(days=[MONDAY, WEDNESDAY])
```

### 5. RepositoryImpl — оркестратор
- Реализует интерфейс из Domain слоя
- Знает о DAO и Mapper
- Domain НЕ знает об этом классе
- Возвращает только Domain модели, никогда Entity

Flow-методы:
```kotlin
dao.getAllActiveHabits()           // Flow<List<HabitEntity>>
    .map { list ->
        list.map { entity ->
            mapper.mapHabitEntityToHabit(entity)
        }
    }                              // Flow<List<Habit>>
```

Nullable suspend-методы:
```kotlin
dao.getHabitById(id)              // HabitEntity?
    ?.let { mapper.map(it) }      // Habit? — null если не найдено
```

---

## Полный поток данных (пример: список привычек)

```
1. UI подписывается на ViewModel
2. ViewModel вызывает GetAllActiveHabitsUseCase
3. UseCase вызывает HabitRepository.getAllActiveHabits()
4. HabitRepositoryImpl вызывает HabitDao.getAllActiveHabits()
5. Room читает из SQLite WHERE isArchived = 0
6. Room возвращает Flow<List<HabitEntity>>
7. RepositoryImpl маппит каждую Entity → Habit
8. Flow<List<Habit>> идёт до UseCase → ViewModel → UI
9. При изменении БД — Flow автоматически пушит новые данные
```

---

## Ключевые принципы

- Entity не выходит за пределы Data Layer
- UI читает только из Room, не из сети (Offline-first)
- Room — единственный источник правды для UI
- Flow обновляет UI автоматически при изменении БД
- ?.let защищает от NPE при nullable результатах

---

## Kotlin: ?.let

```kotlin
// Без ?.let — возможен NPE
val habit = mapper.map(dao.getById(id))  // crash если null

// С ?.let — безопасно
val habit = dao.getById(id)?.let { mapper.map(it) }
// если null → вернёт null
// если не null → выполнит блок и вернёт Habit
```

---

## Что дальше после Data Layer

Hilt DI — нужно объяснить Hilt как собрать все классы вместе:
- Как создать HabitDatabase
- Как получить DAO из Database
- Что при запросе HabitRepository нужно дать HabitRepositoryImpl

## DI HILT
@Provides — когда ты создаёшь объект сам
Используется когда нужна кастомная логика создания. Ты пишешь тело метода.
Hilt видит: "когда нужен HabitDao — вызови этот метод".

  ---
@Binds — когда ты связываешь интерфейс с реализацией

Используется только для связки Interface → Implementation. Тела метода нет — Hilt сам всё делает.

Hilt видит: "когда кто-то просит HabitRepository — дай HabitRepositoryImpl".

@Binds требует abstract fun внутри abstract class.
