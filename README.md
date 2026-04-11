# HabitFlow — Android Habit Tracker                                                                          
                                                                                                             
  Android habit tracker app built to reach **Mobile SDET Middle+** level.                                      
  Clean Architecture · MVVM · Jetpack Compose · Offline-first · 89 tests                                     
                                                                                                               
  ---
                                                                                                               
  ## Features                                                                                                
                                                                                                               
  - Create, edit, archive, restore and delete habits
  - Three repeat types: Daily, WeeklyDays, WeeklyCount
  - Completion toggle with streak calculation
  - Statistics: current streak, best streak, completion percentage
  - Calendar view per habit                                                                                    
  - Reminders via WorkManager + NotificationManager
  - Dark theme and week start day settings                                                                     
  - Offline-first with Supabase sync                                                                         

  ## Architecture

  :app                 — MainActivity, HabitFlowApp, navigation, DI
  :core:domain         — models, repository interfaces, use cases
  :core:data           — Room, Retrofit, mappers, repository implementations                                   
  :core:ui             — theme, shared Compose components
  :feature:habits      — list, info, form, calendar, archived                                                  
  :feature:statistics  — statistics screen                                                                   
  :feature:settings    — settings screen                                                                       
  :feature:onboarding  — onboarding screen
                                                                                                               
  **Data flow:** `UI → ViewModel → UseCase → Repository → [Room / Supabase]`                                 

  **UI state:** every screen uses `sealed class` with `Loading`, `Content`, `Empty`, `Error`                   
   
  **Offline-first:** all writes go to Room first, Supabase sync happens in background via `onStart` and        
  `SyncWorker`                                                                                               

  ## Tech Stack

  | Layer | Technology |
  |---|---|
  | UI | Jetpack Compose + Material 3 |
  | Architecture | Clean Architecture + MVVM |                                                                 
  | DI | Hilt (Dagger 2) |
  | Local DB | Room 2.6.1 |                                                                                    
  | Network | Retrofit 2.11.0 + OkHttp + Supabase |                                                          
  | Async | Coroutines + Flow |                                                                                
  | Background | WorkManager |
  | Navigation | Navigation Compose |                                                                          
  | UI Tests | Kaspresso 1.6.0 |                                                                             
  | Unit Tests | JUnit4 + MockK + Turbine |
  | Integration | MockWebServer + Room in-memory |                                                             
  | CI/CD | GitHub Actions |
                                                                                                               
  ## Testing — 89 tests                                                                                      

  [ UI / Kaspresso — 27 tests    ]  navigation, CRUD, rotation, interruptions, toggle, notifications           
  [ Integration / API — 14 tests ]  MockWebServer: HabitRepositoryImpl, HabitEntryRepositoryImpl
  [ Integration / DAO — 15 tests ]  Room in-memory: HabitDao, HabitEntryDao, MigrationTest                     
  [ Unit — 33 tests              ]  UseCases, ViewModels, Mappers, Extensions                                  
   
  ## CI/CD                                                                                                     
                                                                                                             
  GitHub Actions pipeline — three jobs:

  test → build → ui-tests

  - `test`: Detekt static analysis + unit tests                                                                
  - `build`: assembleDebug + APK artifact upload
  - `ui-tests`: Android emulator (api-33, x86_64, KVM) + Kaspresso + ADB server                                
                                                                                                             
  ## Build

  ```bash                                                                                                      
  # Run unit tests
  ./gradlew test                                                                                               
                                                                                                             
  # Build debug APK
  ./gradlew assembleDebug

  # Run instrumented tests (requires emulator or device)
  ./gradlew connectedAndroidTest

  # Static analysis
  ./gradlew detekt

  Requirements: JDK 17, Android SDK, Min SDK 26                                                                
   
  Key Decisions                                                                                                
                                                                                                             
  - Offline-first: Room is the source of truth; network errors are logged, never shown to user
  - Conflict resolution: updatedAt: LocalDateTime — Last Write Wins
  - Background sync: PeriodicWorkRequest every 15 min with CONNECTED constraint and exponential backoff        
  - Supabase filters: all PATCH/DELETE use eq.${id} format required by PostgREST
  - Result wrapper: HabitResult<T> (Success/Error) — no try/catch in ViewModels  
