# Graph Report - TaskKu  (2026-09-18)

## Corpus Check
- 100 files · ~35,525 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 23 file(s) not represented in the graph (top: .xml 15, (none) 3, .properties 2)

## Summary
- 776 nodes · 1545 edges · 49 communities (27 shown, 22 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 52 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `f863c992`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- SettingsViewModel
- Attachment
- MainScreen.kt
- Subject
- AppDatabase
- TaskKuWidget.kt
- TaskFormViewModel
- Composable
- CalendarViewModel
- NotificationScheduler
- MainActivity.kt
- TaskDao
- DataRepository
- FakeTaskRepository
- SortOption
- 🌟 Fitur Unggulan
- Status
- TimetableItem
- SubtaskDao
- TaskRepository
- TimetableDao
- TaskListViewModelTest
- 2. Guardrails & Aturan Kritis Pengembangan
- TaskListViewModel
- NotificationSchedulerTest
- TaskRepositoryImpl
- Difficulty
- MemberEntity
- AppPreferences
- TimetableRepository
- CalendarViewModelTest
- DashboardViewModelTest
- TimetableRepositoryImpl
- FileStorageHelperTest
- gradlew
- rules/graphify.md
- workflows/graphify.md
- CLAUDE.md
- ic_launcher.webp (Launcher Icon)
- SchoolDay
- TimetableViewModel
- Task
- TaskListScreen
- ExportImportTest
- TimetableScreen

## God Nodes (most connected - your core abstractions)
1. `Task` - 66 edges
2. `TaskFormViewModel` - 38 edges
3. `TimetableItem` - 37 edges
4. `FakeTaskRepository` - 32 edges
5. `TaskRepository` - 28 edges
6. `Subject` - 24 edges
7. `Status` - 23 edges
8. `TaskRepositoryImpl` - 20 edges
9. `SettingsViewModel` - 20 edges
10. `TaskListViewModel` - 20 edges

## Surprising Connections (you probably didn't know these)
- `SubjectFilterBar()` --calls--> `SubjectChip()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/SubjectFilterBar.kt → app/src/main/java/com/example/taskku/ui/components/SubjectChip.kt
- `TimetableScreen()` --calls--> `calculateNextMeetingDate()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/timetable/TimetableScreen.kt → app/src/main/java/com/example/taskku/ui/timetable/TimetableViewModel.kt
- `MainNavigation()` --calls--> `TaskDetail`  [INFERRED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/NavigationKeys.kt
- `MainNavigation()` --calls--> `TaskForm`  [INFERRED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/NavigationKeys.kt
- `MainNavigation()` --calls--> `TaskDetailScreen()`  [EXTRACTED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/ui/taskdetail/TaskDetailScreen.kt

## Import Cycles
- None detected.

## Communities (49 total, 22 thin omitted)

### Community 0 - "SettingsViewModel"
Cohesion: 0.08
Nodes (19): androidx, AddStatusDialog(), AddSubjectDialog(), EditStatusDialog(), Modifier, PresetColor, SectionCard(), SettingsScreen() (+11 more)

### Community 1 - "Attachment"
Cohesion: 0.22
Nodes (3): Attachment, Member, Subtask

### Community 2 - "MainScreen.kt"
Cohesion: 0.06
Nodes (38): MainNavigation(), Main, TaskDetail, TaskForm, CalendarViewModelFactory, Factory, DashboardViewModel, DashboardViewModelFactory (+30 more)

### Community 3 - "Subject"
Cohesion: 0.08
Nodes (8): Flow, SubjectDao, SubjectEntity, Flow, SubjectRepository, SubjectRepositoryImpl, Subject, FakeSubjectRepository

### Community 4 - "AppDatabase"
Cohesion: 0.10
Nodes (9): AttachmentDao, AppDatabase, AppDatabaseCallback, Context, AttachmentEntity, MigrationTest, Callback, RoomDatabase (+1 more)

### Community 5 - "TaskKuWidget.kt"
Cohesion: 0.13
Nodes (19): ActionCallback, ActionParameters, BootReceiver, BroadcastReceiver, Context, Intent, formatWidgetDeadline(), Context (+11 more)

### Community 6 - "TaskFormViewModel"
Cohesion: 0.08
Nodes (7): FormState, StateFlow, T, ViewModel, SubtaskFormItem, TaskFormViewModel, TaskFormViewModelTest

### Community 7 - "Composable"
Cohesion: 0.07
Nodes (35): TaskTag, KUIS, PR, PRAKTIKUM, PROYEK, DeadlineText(), Modifier, DifficultyBadge() (+27 more)

### Community 8 - "CalendarViewModel"
Cohesion: 0.12
Nodes (18): CalendarCellData, CalendarGrid(), CalendarGridInfo, CalendarScreen(), DayCell(), Color, Modifier, LegendItem() (+10 more)

### Community 9 - "NotificationScheduler"
Cohesion: 0.19
Nodes (8): AlarmManager, ReminderOffset, ON_DEADLINE, ONE_DAY_19_00, ONE_HOUR_BEFORE, THREE_HOURS_BEFORE, Context, NotificationScheduler

### Community 10 - "MainActivity.kt"
Cohesion: 0.15
Nodes (10): MainScreenTest, Intent, MainActivity, BroadcastReceiver, Context, Intent, NotificationReceiver, TaskKuTheme() (+2 more)

### Community 11 - "TaskDao"
Cohesion: 0.18
Nodes (5): Flow, StatusCount, TaskDao, TaskWithDetails, TaskEntity

### Community 12 - "DataRepository"
Cohesion: 0.19
Nodes (13): DataRepository, DefaultDataRepository, Flow, Error, StateFlow, ViewModel, Loading, MainScreenUiState (+5 more)

### Community 14 - "SortOption"
Cohesion: 0.13
Nodes (10): SortDirection, ASC, DESC, SortOption, CREATED_DATE, DEADLINE, DIFFICULTY, SUBJECT (+2 more)

### Community 15 - "🌟 Fitur Unggulan"
Cohesion: 0.10
Nodes (20): 1. 📋 Manajemen Tugas Lengkap (Individu & Kelompok), 2. 🏷️ Kategori / Mata Kuliah & Status Kustom, 3. 📎 Lampiran File & Gambar, 4. 📊 Dashboard & Statistik Interaktif, 5. 📅 Tampilan Kalender (Calendar View), 6. 🔍 Pencarian Cepat, Filter & Pengurutan Multi-Kriteria, 7. 📱 Home Screen Widget (Jetpack Glance), 8. ⏰ Pengingat & Notifikasi Akurat (+12 more)

### Community 16 - "Status"
Cohesion: 0.08
Nodes (12): Flow, StatusDao, StatusEntity, Flow, StatusRepository, StatusRepositoryImpl, AppContainer, com (+4 more)

### Community 17 - "TimetableItem"
Cohesion: 0.16
Nodes (3): TimetableItem, FakeTimetableRepository, TimetableViewModelTest

### Community 20 - "TimetableDao"
Cohesion: 0.27
Nodes (3): Flow, TimetableDao, TimetableEntity

### Community 22 - "2. Guardrails & Aturan Kritis Pengembangan"
Cohesion: 0.20
Nodes (9): 1. Arsitektur & Lingkungan Proyek, 2. Guardrails & Aturan Kritis Pengembangan, A. Preservasi Timestamp Pembuatan (`createdAt`), Aturan Pengembangan TaskKu (TaskKu Development Guidelines), B. Siklus Hidup File Lampiran Fisik (Storage Lifecycle), C. Izin Runtime & Notifikasi (Android 13+ / API 33+), D. Navigasi & BackStack (Jetpack Navigation 3), E. Status Tugas Dinamis (Hindari Hardcoded "Selesai") (+1 more)

### Community 23 - "TaskListViewModel"
Cohesion: 0.18
Nodes (7): FilterParams, Flow, StateFlow, T, ViewModel, TaskListViewModel, UiState

### Community 26 - "Difficulty"
Cohesion: 0.10
Nodes (8): Difficulty, MUDAH, SEDANG, SULIT, TaskType, KELOMPOK, PRIBADI, WidgetLogicTest

### Community 28 - "AppPreferences"
Cohesion: 0.14
Nodes (9): AppPreferences, StateFlow, ThemeMode, DARK, LIGHT, SYSTEM, ExportImportManager, SettingsViewModelTest (+1 more)

### Community 34 - "gradlew"
Cohesion: 0.70
Nodes (4): gradlew script, die(), save(), warn()

### Community 43 - "SchoolDay"
Cohesion: 0.20
Nodes (8): SchoolDay, JUMAT, KAMIS, MINGGU, RABU, SABTU, SELASA, SENIN

### Community 44 - "TimetableViewModel"
Cohesion: 0.22
Nodes (5): StateFlow, T, ViewModel, TimetableViewModel, UiState

### Community 46 - "TaskListScreen"
Cohesion: 0.39
Nodes (5): Modifier, SubjectFilterBar(), SubjectWithCount, Modifier, TaskListScreen()

### Community 48 - "TimetableScreen"
Cohesion: 0.80
Nodes (4): Modifier, TimetableAddEditDialog(), TimetableScreen(), TimetableSlotCard()

## Knowledge Gaps
- **74 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `MUDAH`, `SEDANG` (+69 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 242 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **22 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Task` connect `Task` to `Attachment`, `MainScreen.kt`, `TaskKuWidget.kt`, `TaskFormViewModel`, `Composable`, `CalendarViewModel`, `NotificationScheduler`, `FakeTaskRepository`, `SortOption`, `TaskRepository`, `TaskListViewModelTest`, `TaskListViewModel`, `NotificationSchedulerTest`, `TaskRepositoryImpl`, `Difficulty`, `CalendarViewModelTest`, `DashboardViewModelTest`?**
  _High betweenness centrality (0.211) - this node is a cross-community bridge._
- **Why does `AppDatabase` connect `AppDatabase` to `Status`, `TaskRepository`, `SubtaskDao`, `MemberEntity`?**
  _High betweenness centrality (0.074) - this node is a cross-community bridge._
- **Why does `TaskRepository` connect `TaskRepository` to `Attachment`, `MainScreen.kt`, `TaskFormViewModel`, `CalendarViewModel`, `NotificationScheduler`, `FakeTaskRepository`, `Status`, `TaskListViewModel`, `TaskRepositoryImpl`, `AppPreferences`?**
  _High betweenness centrality (0.067) - this node is a cross-community bridge._
- **Are the 6 inferred relationships involving `Task` (e.g. with `.importData()` and `.saveTask()`) actually correct?**
  _`Task` has 6 INFERRED edges - model-reasoned connections that need verification._
- **Are the 8 inferred relationships involving `TaskFormViewModel` (e.g. with `.editExistingTask_loadsAndUpdates()` and `.groupTask_memberAndSubtaskManagement()`) actually correct?**
  _`TaskFormViewModel` has 8 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _74 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `SettingsViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.08143939393939394 - nodes in this community are weakly interconnected._