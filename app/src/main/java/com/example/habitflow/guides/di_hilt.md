# DI — Hilt в HabitFlow

## Зачем нужен DI

Без DI зависимости создаются вручную:
```kotlin
val mapper = HabitMapper()
val dao = database.habitDao()
val repository = HabitRepositoryImpl(dao, mapper)
```

С Hilt — все зависимости создаются и передаются автоматически.
Ты только объявляешь что тебе нужно — Hilt сам это предоставит.

---

## Когда @Inject constructor недостаточно

`@Inject constructor` работает только если ты владеешь классом и можешь аннотировать его конструктор.

Но есть два случая когда это невозможно:
- Интерфейс — у него нет конструктора
- Класс создаётся через фабрику/билдер (например Room.databaseBuilder)

Для этих случаев нужен @Module.

---

## @Provides vs @Binds

### @Provides — создаёшь объект сам
Используй когда нужна кастомная логика создания.
Пишешь тело метода сам.

```kotlin
@Provides
@Singleton
fun provideHabitDatabase(@ApplicationContext context: Context): HabitDatabase {
    return Room.databaseBuilder(
        context,
        HabitDatabase::class.java,
        "habit_database"
    ).build()
}
```

### @Binds — связываешь интерфейс с реализацией
Используй только для Interface → Implementation.
Тела метода нет — Hilt сам всё делает.
Требует abstract fun внутри abstract class.

```kotlin
@Binds
abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository
```

### Правило выбора
- Создаю объект через билдер/фабрику → @Provides
- Связываю интерфейс с реализацией → @Binds

---

## @Singleton — время жизни объекта

Без @Singleton Hilt создаёт новый экземпляр при каждом запросе.
С @Singleton — один экземпляр на всё приложение.

Важно: @Singleton ставится на @Provides метод, НЕ на сам класс.
На класс работает только если Hilt создаёт его через @Inject constructor.

```kotlin
@Provides
@Singleton  // ← на метод
fun provideHabitDatabase(...): HabitDatabase { ... }
```

---

## @InstallIn — в какой компонент установить модуль

Каждый @Module обязан указать компонент через @InstallIn.
SingletonComponent — живёт весь жизненный цикл приложения.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule { ... }
```

---

## Структура Hilt в проекте

### Шаг 1 — Application класс с @HiltAndroidApp

Обязательно. Это точка инициализации Hilt.

```kotlin
@HiltAndroidApp
class HabitFlowApp : Application()
```

Зарегистрировать в AndroidManifest.xml:
```xml
<application
    android:name=".HabitFlowApp"
    ...>
```

Точка перед именем — означает "в корневом пакете приложения".
Без точки Android не найдёт класс.

### Шаг 2 — DatabaseModule

Предоставляет Room Database и DAO.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideHabitDatabase(@ApplicationContext context: Context): HabitDatabase {
        return Room.databaseBuilder(
            context,
            HabitDatabase::class.java,
            "habit_database"
        ).build()
    }

    @Provides
    fun provideHabitDao(database: HabitDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    fun provideHabitEntryDao(database: HabitDatabase): HabitEntryDao {
        return database.habitEntryDao()
    }
}
```

Почему @Singleton только на Database, а не на DAO?
DAO берётся из Database которая уже Singleton — DAO всегда будет одним и тем же объектом.

### Шаг 3 — RepositoryModule

Связывает интерфейсы из Domain с реализациями из Data.
Обязательно abstract class, методы abstract fun.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    abstract fun bindHabitEntryRepository(impl: HabitEntryRepositoryImpl): HabitEntryRepository
}
```

### Шаг 4 — @Inject constructor на классах

Классы которые Hilt создаёт сам — помечаются @Inject constructor.

```kotlin
class HabitMapper @Inject constructor()
class HabitEntryMapper @Inject constructor()

class HabitRepositoryImpl @Inject constructor(
    private val dao: HabitDao,
    private val mapper: HabitMapper
) : HabitRepository
```

### Шаг 5 — @AndroidEntryPoint на Activity

Без этой аннотации Hilt не будет инжектировать зависимости в Activity.

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity()
```

---

## Полная цепочка зависимостей

```
HabitFlowApp (@HiltAndroidApp)
    └── DatabaseModule
            ├── provideHabitDatabase → HabitDatabase (@Singleton)
            ├── provideHabitDao      → HabitDao
            └── provideHabitEntryDao → HabitEntryDao
    └── RepositoryModule
            ├── HabitDao + HabitMapper → HabitRepositoryImpl → HabitRepository
            └── HabitEntryDao + HabitEntryMapper → HabitEntryRepositoryImpl → HabitEntryRepository
    └── MainActivity (@AndroidEntryPoint)
            └── ViewModel (@HiltViewModel)
                    └── UseCase → Repository → DAO → Room
```

---

## Частые ошибки

| Ошибка | Причина | Исправление |
|--------|---------|-------------|
| @Singleton на классе вместо метода | Hilt не управляет созданием | Перенести на @Provides метод |
| class вместо abstract class в @Binds модуле | @Binds требует abstract | Добавить abstract |
| Нет @InstallIn | Hilt не знает компонент | Добавить @InstallIn(SingletonComponent::class) |
| android:name без точки | Android не находит класс | Добавить точку: ".HabitFlowApp" |
| Нет @AndroidEntryPoint на Activity | Hilt не инжектирует в Activity | Добавить аннотацию |