# Graph Report - TaskKu  (2026-09-19)

## Corpus Check
- 106 files · ~40,741 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 24 file(s) not represented in the graph (top: .xml 15, (none) 3, .properties 2)

## Summary
- 863 nodes · 1708 edges · 50 communities (27 shown, 23 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 58 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `2187152a`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- SettingsViewModel
- TaskRepository
- CalendarScreen.kt
- Subject
- AppDatabase
- TaskKuWidget.kt
- TaskFormViewModel
- DashboardScreen.kt
- AdaptiveLayout.kt
- NotificationScheduler
- AppContainer
- TaskDao
- DataRepository
- FakeTaskRepository
- TaskListViewModelTest
- 🌟 Fitur Unggulan
- Status
- TimetableItem
- SortOption
- TaskRepositoryImpl
- DeadlineInfo
- Task
- 2. Guardrails & Aturan Kritis Pengembangan
- MemberEntity
- NotificationSchedulerTest
- TaskListViewModel
- Difficulty
- Attachment
- SettingsViewModelTest
- DeadlineFormatterTest
- SubtaskDao
- StatusRepositoryImpl
- AppPreferences
- FileStorageHelperTest
- gradlew
- rules/graphify.md
- workflows/graphify.md
- CLAUDE.md
- ic_launcher.webp (Launcher Icon)
- DashboardViewModelTest
- TaskDetailViewModel
- ExportImportTest
- NavigationTabTest
- MainScreen.kt
- TaskDetailViewModelTest
- AdaptiveLayoutTest

## God Nodes (most connected - your core abstractions)
1. `Task` - 70 edges
2. `TimetableItem` - 44 edges
3. `TaskFormViewModel` - 39 edges
4. `FakeTaskRepository` - 32 edges
5. `TaskRepository` - 29 edges
6. `Subject` - 24 edges
7. `Status` - 23 edges
8. `TaskRepositoryImpl` - 21 edges
9. `TaskListViewModel` - 21 edges
10. `SettingsViewModel` - 20 edges

## Surprising Connections (you probably didn't know these)
- `MainNavigation()` --calls--> `TaskDetail`  [INFERRED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/NavigationKeys.kt
- `MainNavigation()` --calls--> `TaskForm`  [INFERRED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/NavigationKeys.kt
- `SubjectFilterBar()` --calls--> `SubjectChip()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/SubjectFilterBar.kt → app/src/main/java/com/example/taskku/ui/components/SubjectChip.kt
- `TimetableScreen()` --calls--> `calculateNextMeetingDate()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/timetable/TimetableScreen.kt → app/src/main/java/com/example/taskku/ui/timetable/TimetableViewModel.kt
- `MainNavigation()` --calls--> `MainScreen()`  [EXTRACTED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/ui/main/MainScreen.kt

## Import Cycles
- None detected.

## Communities (50 total, 23 thin omitted)

### Community 0 - "SettingsViewModel"
Cohesion: 0.10
Nodes (10): Error, Idle, InProgress, StateFlow, T, ViewModel, OperationResult, SettingsUiState (+2 more)

### Community 2 - "CalendarScreen.kt"
Cohesion: 0.12
Nodes (21): CalendarCellData, CalendarEmptyCard(), CalendarGrid(), CalendarGridInfo, CalendarScreen(), DayCell(), Color, Modifier (+13 more)

### Community 3 - "Subject"
Cohesion: 0.09
Nodes (8): Flow, SubjectDao, SubjectEntity, Flow, SubjectRepository, SubjectRepositoryImpl, Subject, FakeSubjectRepository

### Community 4 - "AppDatabase"
Cohesion: 0.07
Nodes (12): AttachmentDao, Flow, StatusDao, AppDatabase, AppDatabaseCallback, Context, AttachmentEntity, StatusEntity (+4 more)

### Community 5 - "TaskKuWidget.kt"
Cohesion: 0.13
Nodes (19): ActionCallback, ActionParameters, BootReceiver, BroadcastReceiver, Context, Intent, formatWidgetDeadline(), Context (+11 more)

### Community 6 - "TaskFormViewModel"
Cohesion: 0.07
Nodes (8): android, FormState, Context, StateFlow, ViewModel, SubtaskFormItem, TaskFormViewModel, TaskFormViewModelTest

### Community 7 - "DashboardScreen.kt"
Cohesion: 0.05
Nodes (62): androidx, TaskTag, KUIS, PR, PRAKTIKUM, PROYEK, MainNavigation(), DeadlineText() (+54 more)

### Community 8 - "AdaptiveLayout.kt"
Cohesion: 0.20
Nodes (11): isCompactHeight(), rememberWindowHeightSizeClass(), rememberWindowWidthSizeClass(), WindowHeightSizeClass, COMPACT, EXPANDED, MEDIUM, WindowWidthSizeClass (+3 more)

### Community 9 - "NotificationScheduler"
Cohesion: 0.19
Nodes (8): AlarmManager, ReminderOffset, ON_DEADLINE, ONE_DAY_19_00, ONE_HOUR_BEFORE, THREE_HOURS_BEFORE, Context, NotificationScheduler

### Community 10 - "AppContainer"
Cohesion: 0.11
Nodes (14): MainScreenTest, AppContainer, com, Intent, MainActivity, BroadcastReceiver, Context, Intent (+6 more)

### Community 11 - "TaskDao"
Cohesion: 0.17
Nodes (6): Flow, StatusCount, TaskDao, TaskSummaryEntity, TaskWithDetails, TaskEntity

### Community 12 - "DataRepository"
Cohesion: 0.19
Nodes (13): DataRepository, DefaultDataRepository, Flow, Error, StateFlow, ViewModel, Loading, MainScreenUiState (+5 more)

### Community 15 - "🌟 Fitur Unggulan"
Cohesion: 0.10
Nodes (20): 1. 📋 Manajemen Tugas Lengkap (Individu & Kelompok), 2. 🏷️ Kategori / Mata Kuliah & Status Kustom, 3. 📎 Lampiran File & Gambar, 4. 📊 Dashboard & Statistik Interaktif, 5. 📅 Tampilan Kalender (Calendar View), 6. 🔍 Pencarian Cepat, Filter & Pengurutan Multi-Kriteria, 7. 📱 Home Screen Widget (Jetpack Glance), 8. ⏰ Pengingat & Notifikasi Akurat (+12 more)

### Community 16 - "Status"
Cohesion: 0.18
Nodes (4): Flow, StatusRepository, Status, FakeStatusRepository

### Community 17 - "TimetableItem"
Cohesion: 0.06
Nodes (19): Flow, TimetableDao, TimetableEntity, Flow, TimetableRepository, TimetableRepositoryImpl, parseMinutes(), SchoolDay (+11 more)

### Community 18 - "SortOption"
Cohesion: 0.24
Nodes (7): SortOption, CREATED_DATE, DEADLINE, DIFFICULTY, SUBJECT, Modifier, SortOptionBar()

### Community 20 - "DeadlineInfo"
Cohesion: 0.18
Nodes (10): DeadlineFormatter, DeadlineInfo, DeadlineStatus, DUE_TODAY, DUE_TODAY_OVERDUE, DUE_TOMORROW, OVERDUE, UPCOMING (+2 more)

### Community 21 - "Task"
Cohesion: 0.17
Nodes (4): Member, Subtask, Task, TaskTest

### Community 22 - "2. Guardrails & Aturan Kritis Pengembangan"
Cohesion: 0.20
Nodes (9): 1. Arsitektur & Lingkungan Proyek, 2. Guardrails & Aturan Kritis Pengembangan, A. Preservasi Timestamp Pembuatan (`createdAt`), Aturan Pengembangan TaskKu (TaskKu Development Guidelines), B. Siklus Hidup File Lampiran Fisik (Storage Lifecycle), C. Izin Runtime & Notifikasi (Android 13+ / API 33+), D. Navigasi & BackStack (Jetpack Navigation 3), E. Status Tugas Dinamis (Hindari Hardcoded "Selesai") (+1 more)

### Community 25 - "TaskListViewModel"
Cohesion: 0.14
Nodes (11): Modifier, SubjectFilterBar(), SubjectWithCount, FilteredData, FilterParams, Flow, StateFlow, T (+3 more)

### Community 26 - "Difficulty"
Cohesion: 0.08
Nodes (9): Difficulty, MUDAH, SEDANG, SULIT, TaskType, KELOMPOK, PRIBADI, CalendarViewModelTest (+1 more)

### Community 27 - "Attachment"
Cohesion: 0.38
Nodes (4): Attachment, FileStorageHelper, Context, Uri

### Community 28 - "SettingsViewModelTest"
Cohesion: 0.20
Nodes (3): ExportImportManager, SettingsViewModelTest, Result

### Community 32 - "AppPreferences"
Cohesion: 0.16
Nodes (9): AppPreferences, StateFlow, ThemeMode, DARK, LIGHT, SYSTEM, SortDirection, ASC (+1 more)

### Community 34 - "gradlew"
Cohesion: 0.70
Nodes (4): gradlew script, die(), save(), warn()

### Community 44 - "TaskDetailViewModel"
Cohesion: 0.24
Nodes (5): StateFlow, T, ViewModel, TaskDetailViewModel, UiState

### Community 47 - "MainScreen.kt"
Cohesion: 0.07
Nodes (37): Main, TaskDetail, TaskForm, CalendarViewModelFactory, Factory, DashboardViewModel, DashboardViewModelFactory, Factory (+29 more)

## Knowledge Gaps
- **86 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `MUDAH`, `SEDANG` (+81 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 275 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **23 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Task` connect `Task` to `AppPreferences`, `TaskRepository`, `CalendarScreen.kt`, `TaskKuWidget.kt`, `TaskFormViewModel`, `DashboardScreen.kt`, `NotificationScheduler`, `DashboardViewModelTest`, `TaskDetailViewModel`, `FakeTaskRepository`, `TaskListViewModelTest`, `MainScreen.kt`, `TaskRepositoryImpl`, `NotificationSchedulerTest`, `TaskListViewModel`, `Difficulty`?**
  _High betweenness centrality (0.203) - this node is a cross-community bridge._
- **Why does `TimetableItem` connect `TimetableItem` to `TaskKuWidget.kt`, `MainScreen.kt`, `DashboardScreen.kt`?**
  _High betweenness centrality (0.069) - this node is a cross-community bridge._
- **Why does `AppDatabase` connect `AppDatabase` to `Status`, `TaskRepository`, `SubtaskDao`, `MemberEntity`?**
  _High betweenness centrality (0.064) - this node is a cross-community bridge._
- **Are the 6 inferred relationships involving `Task` (e.g. with `.importData()` and `.saveTask()`) actually correct?**
  _`Task` has 6 INFERRED edges - model-reasoned connections that need verification._
- **Are the 8 inferred relationships involving `TaskFormViewModel` (e.g. with `.editExistingTask_loadsAndUpdates()` and `.groupTask_memberAndSubtaskManagement()`) actually correct?**
  _`TaskFormViewModel` has 8 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _86 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `SettingsViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.09782608695652174 - nodes in this community are weakly interconnected._