# Graph Report - TaskKu  (2026-09-19)

## Corpus Check
- 101 files · ~37,180 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 24 file(s) not represented in the graph (top: .xml 15, (none) 3, .properties 2)

## Summary
- 794 nodes · 1587 edges · 49 communities (27 shown, 22 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 56 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `f6ed4318`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- SettingsViewModel
- Subtask
- MainScreen.kt
- Subject
- AppDatabase
- TaskKuWidget.kt
- TaskFormViewModel
- TaskTag
- CalendarScreen.kt
- NotificationScheduler
- AppContainer
- TaskDao
- DataRepository
- FakeTaskRepository
- TaskDetailViewModel
- 🌟 Fitur Unggulan
- Status
- TimetableItem
- SubtaskDao
- TaskRepository
- Task
- TaskDetailViewModelTest
- 2. Guardrails & Aturan Kritis Pengembangan
- MemberEntity
- NotificationSchedulerTest
- TaskListViewModel
- Difficulty
- Attachment
- SettingsViewModelTest
- TaskDetailScreen.kt
- CalendarViewModelTest
- DashboardViewModelTest
- DashboardScreen.kt
- FileStorageHelperTest
- gradlew
- rules/graphify.md
- workflows/graphify.md
- CLAUDE.md
- ic_launcher.webp (Launcher Icon)
- SettingsScreen.kt
- TaskCard
- Composable
- StatusRepositoryImpl
- ExportImportTest
- TaskRepositoryImpl

## God Nodes (most connected - your core abstractions)
1. `Task` - 66 edges
2. `TimetableItem` - 41 edges
3. `TaskFormViewModel` - 39 edges
4. `FakeTaskRepository` - 32 edges
5. `TaskRepository` - 28 edges
6. `Subject` - 24 edges
7. `Status` - 23 edges
8. `TaskListViewModel` - 21 edges
9. `TaskRepositoryImpl` - 20 edges
10. `SettingsViewModel` - 20 edges

## Surprising Connections (you probably didn't know these)
- `MainNavigation()` --calls--> `TaskDetail`  [INFERRED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/NavigationKeys.kt
- `MainNavigation()` --calls--> `TaskForm`  [INFERRED]
  app/src/main/java/com/example/taskku/Navigation.kt → app/src/main/java/com/example/taskku/NavigationKeys.kt
- `TaskCard()` --calls--> `DeadlineText()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/TaskCard.kt → app/src/main/java/com/example/taskku/ui/components/DeadlineText.kt
- `TaskCard()` --calls--> `DifficultyBadge()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/TaskCard.kt → app/src/main/java/com/example/taskku/ui/components/DifficultyBadge.kt
- `TaskCard()` --calls--> `StatusBadge()`  [INFERRED]
  app/src/main/java/com/example/taskku/ui/components/TaskCard.kt → app/src/main/java/com/example/taskku/ui/components/StatusBadge.kt

## Import Cycles
- None detected.

## Communities (49 total, 22 thin omitted)

### Community 0 - "SettingsViewModel"
Cohesion: 0.10
Nodes (10): Error, Idle, InProgress, StateFlow, T, ViewModel, OperationResult, SettingsUiState (+2 more)

### Community 2 - "MainScreen.kt"
Cohesion: 0.05
Nodes (42): Main, TaskDetail, TaskForm, CalendarViewModel, CalendarViewModelFactory, Factory, Flow, StateFlow (+34 more)

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

### Community 7 - "TaskTag"
Cohesion: 0.17
Nodes (11): TaskTag, KUIS, PR, PRAKTIKUM, PROYEK, getTagIcon(), ImageVector, Modifier (+3 more)

### Community 8 - "CalendarScreen.kt"
Cohesion: 0.38
Nodes (10): CalendarCellData, CalendarGrid(), CalendarGridInfo, CalendarScreen(), DayCell(), Color, Modifier, YearMonth (+2 more)

### Community 9 - "NotificationScheduler"
Cohesion: 0.19
Nodes (8): AlarmManager, ReminderOffset, ON_DEADLINE, ONE_DAY_19_00, ONE_HOUR_BEFORE, THREE_HOURS_BEFORE, Context, NotificationScheduler

### Community 10 - "AppContainer"
Cohesion: 0.11
Nodes (14): MainScreenTest, AppContainer, com, Intent, MainActivity, BroadcastReceiver, Context, Intent (+6 more)

### Community 11 - "TaskDao"
Cohesion: 0.18
Nodes (5): Flow, StatusCount, TaskDao, TaskWithDetails, TaskEntity

### Community 12 - "DataRepository"
Cohesion: 0.19
Nodes (13): DataRepository, DefaultDataRepository, Flow, Error, StateFlow, ViewModel, Loading, MainScreenUiState (+5 more)

### Community 14 - "TaskDetailViewModel"
Cohesion: 0.15
Nodes (11): MainNavigation(), Factory, StateFlow, T, ViewModel, TaskDetailViewModel, TaskDetailViewModelFactory, UiState (+3 more)

### Community 15 - "🌟 Fitur Unggulan"
Cohesion: 0.10
Nodes (20): 1. 📋 Manajemen Tugas Lengkap (Individu & Kelompok), 2. 🏷️ Kategori / Mata Kuliah & Status Kustom, 3. 📎 Lampiran File & Gambar, 4. 📊 Dashboard & Statistik Interaktif, 5. 📅 Tampilan Kalender (Calendar View), 6. 🔍 Pencarian Cepat, Filter & Pengurutan Multi-Kriteria, 7. 📱 Home Screen Widget (Jetpack Glance), 8. ⏰ Pengingat & Notifikasi Akurat (+12 more)

### Community 16 - "Status"
Cohesion: 0.20
Nodes (4): Flow, StatusRepository, Status, FakeStatusRepository

### Community 17 - "TimetableItem"
Cohesion: 0.05
Nodes (22): Flow, TimetableDao, TimetableEntity, Flow, TimetableRepository, TimetableRepositoryImpl, SchoolDay, JUMAT (+14 more)

### Community 22 - "2. Guardrails & Aturan Kritis Pengembangan"
Cohesion: 0.20
Nodes (9): 1. Arsitektur & Lingkungan Proyek, 2. Guardrails & Aturan Kritis Pengembangan, A. Preservasi Timestamp Pembuatan (`createdAt`), Aturan Pengembangan TaskKu (TaskKu Development Guidelines), B. Siklus Hidup File Lampiran Fisik (Storage Lifecycle), C. Izin Runtime & Notifikasi (Android 13+ / API 33+), D. Navigasi & BackStack (Jetpack Navigation 3), E. Status Tugas Dinamis (Hindari Hardcoded "Selesai") (+1 more)

### Community 25 - "TaskListViewModel"
Cohesion: 0.05
Nodes (24): AppPreferences, StateFlow, ThemeMode, DARK, LIGHT, SYSTEM, SortDirection, ASC (+16 more)

### Community 26 - "Difficulty"
Cohesion: 0.10
Nodes (8): Difficulty, MUDAH, SEDANG, SULIT, TaskType, KELOMPOK, PRIBADI, WidgetLogicTest

### Community 27 - "Attachment"
Cohesion: 0.38
Nodes (4): Attachment, FileStorageHelper, Context, Uri

### Community 28 - "SettingsViewModelTest"
Cohesion: 0.20
Nodes (3): ExportImportManager, SettingsViewModelTest, Result

### Community 29 - "TaskDetailScreen.kt"
Cohesion: 0.26
Nodes (10): DifficultyBadge(), com, Modifier, Color, Modifier, parseStatusColor(), StatusBadge(), Modifier (+2 more)

### Community 32 - "DashboardScreen.kt"
Cohesion: 0.40
Nodes (8): DeadlineText(), Modifier, DashboardScreen(), Color, ImageVector, Modifier, StatCard(), UrgentTaskCard()

### Community 34 - "gradlew"
Cohesion: 0.70
Nodes (4): gradlew script, die(), save(), warn()

### Community 43 - "SettingsScreen.kt"
Cohesion: 0.36
Nodes (9): androidx, AddStatusDialog(), AddSubjectDialog(), EditStatusDialog(), Modifier, PresetColor, SectionCard(), SettingsScreen() (+1 more)

### Community 44 - "TaskCard"
Cohesion: 0.33
Nodes (7): EmptyState(), ImageVector, Modifier, Modifier, TaskCard(), Modifier, TaskListScreen()

### Community 45 - "Composable"
Cohesion: 0.33
Nodes (6): Modifier, SubjectChip(), Modifier, SubjectFilterBar(), SubjectWithCount, Composable

## Knowledge Gaps
- **75 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `MUDAH`, `SEDANG` (+70 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 246 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **22 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Task` connect `Task` to `DashboardScreen.kt`, `Subtask`, `MainScreen.kt`, `TaskKuWidget.kt`, `TaskFormViewModel`, `CalendarScreen.kt`, `NotificationScheduler`, `TaskCard`, `FakeTaskRepository`, `TaskDetailViewModel`, `TaskRepositoryImpl`, `TaskRepository`, `TaskDetailViewModelTest`, `NotificationSchedulerTest`, `TaskListViewModel`, `Difficulty`, `CalendarViewModelTest`, `DashboardViewModelTest`?**
  _High betweenness centrality (0.209) - this node is a cross-community bridge._
- **Why does `AppDatabase` connect `AppDatabase` to `Status`, `TaskRepositoryImpl`, `SubtaskDao`, `TaskRepository`, `MemberEntity`?**
  _High betweenness centrality (0.072) - this node is a cross-community bridge._
- **Why does `TaskRepository` connect `TaskRepository` to `Subtask`, `MainScreen.kt`, `TaskFormViewModel`, `NotificationScheduler`, `AppContainer`, `FakeTaskRepository`, `TaskDetailViewModel`, `TaskRepositoryImpl`, `TaskListViewModel`, `SettingsViewModelTest`?**
  _High betweenness centrality (0.067) - this node is a cross-community bridge._
- **Are the 6 inferred relationships involving `Task` (e.g. with `.importData()` and `.saveTask()`) actually correct?**
  _`Task` has 6 INFERRED edges - model-reasoned connections that need verification._
- **Are the 8 inferred relationships involving `TaskFormViewModel` (e.g. with `.editExistingTask_loadsAndUpdates()` and `.groupTask_memberAndSubtaskManagement()`) actually correct?**
  _`TaskFormViewModel` has 8 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _75 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `SettingsViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.10276679841897234 - nodes in this community are weakly interconnected._