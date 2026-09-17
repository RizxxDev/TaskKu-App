# TaskKu 📝

[![Android Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-blue.svg)](https://developer.android.com/about/versions/16)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-orange.svg)](https://developer.android.com/about/versions/oreo)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2B%20Material%203-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Navigation](https://img.shields.io/badge/Navigation-Jetpack%20Navigation%203-brightgreen)](https://developer.android.com/guide/navigation)
[![Database](https://img.shields.io/badge/Database-Room%202.x-yellowgreen)](https://developer.android.com/training/data-storage/room)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**TaskKu** adalah aplikasi manajemen tugas modern untuk Android yang dirancang untuk membantu mahasiswa, pelajar, dan profesional mengelola tugas pribadi maupun kelompok secara terstruktur, efisien, dan menyenangkan. Dibangun dengan pendekatan **Modern Android Development (MAD)** menggunakan 100% **Jetpack Compose**, **Material 3**, **Jetpack Navigation 3**, **Room Database**, serta arsitektur **MVVM + Clean Architecture**.

---

## 🌟 Fitur Unggulan

### 1. 📋 Manajemen Tugas Lengkap (Individu & Kelompok)
- **Tipe Tugas:** Dukungan fleksibel untuk tugas individu maupun tugas kelompok (*group project*).
- **Manajemen Anggota:** Tambahkan dan kelola nama anggota kelompok beserta pembagian tugasnya.
- **Subtask & Checklist:** Pecah tugas besar menjadi beberapa subtask interaktif dengan indikator progres otomatis.
- **Tingkat Kesulitan:** Label prioritas kesulitan (Mudah, Sedang, Sulit) untuk memudahkan penyusunan prioritas kerja.

### 2. 🏷️ Kategori / Mata Kuliah & Status Kustom
- **Mata Kuliah / Subjek:** Buat dan kelompokkan tugas berdasarkan mata kuliah atau proyek dengan penanda warna (*color-coded*).
- **Status Dinamis:** Kustomisasi status tugas (misal: *Belum Dimulai*, *Sedang Dikerjakan*, *Selesai*) tanpa batasan kaku.

### 3. 📎 Lampiran File & Gambar
- Lampirkan referensi tugas, instruksi PDF, atau gambar secara aman.
- Pratinjau gambar terintegrasi menggunakan **Coil**.
- Manajemen siklus hidup file internal mandiri via `FileProvider` untuk keamanan data.

### 4. 📊 Dashboard & Statistik Interaktif
- Ringkasan komprehensif: total tugas, tugas selesai, tugas tertunda (*pending*), dan tugas mendekati batas waktu (*urgent/overdue*).
- Visualisasi distribusi tugas per mata kuliah.

### 5. 📅 Tampilan Kalender (Calendar View)
- Jadwal deadline divisualisasikan dalam kalender bulanan dan harian.
- Navigasi cepat untuk melihat tugas pada tanggal tertentu.

### 6. 🔍 Pencarian Cepat, Filter & Pengurutan Multi-Kriteria
- Pencarian instan berdasarkan judul dan deskripsi tugas.
- Filter cepat berdasarkan mata kuliah, status, tingkat kesulitan, dan tipe tugas.
- Pengurutan berdasarkan tanggal deadline terdekat, prioritas, atau tanggal pembuatan.

### 7. 📱 Home Screen Widget (Jetpack Glance)
- Pantau tugas mendesak untuk "Hari Ini" dan "Besok" langsung dari layar beranda (*Home Screen*) tanpa perlu membuka aplikasi.
- Tampilan responsif berbasis **Material 3 Glance AppWidget**.

### 8. ⏰ Pengingat & Notifikasi Akurat
- Pengingat deadline otomatis menggunakan `AlarmManager.setExactAndAllowWhileIdle`.
- Pilihan waktu pengingat fleksibel (tepat saat deadline, 1 jam sebelum, 1 hari sebelum, dll.).
- `BootReceiver` otomatis menjadwalkan ulang seluruh alarm saat perangkat dinyalakan kembali (*reboot*).

### 9. 💾 Ekspor & Impor Cadangan Data (JSON)
- Cadangkan (*backup*) seluruh data tugas, kategori, dan status ke dalam format JSON.
- Pulihkan (*restore*) data kapan saja dengan validasi integritas skema.

---

## 🏗️ Arsitektur & Teknologi

TaskKu mengimplementasikan standar arsitektur **MVVM (Model-View-ViewModel)** dengan pemisahan lapisan (**Clean Architecture**) untuk kode yang modular, mudah diuji (*testable*), dan mudah dirawat:

```
app/
├── data/
│   ├── local/              # Room Database, DAO, Entity SQLite
│   ├── preferences/        # Pengaturan lokal & SharedPreferences
│   └── repository/         # Implementasi Data Repository (Single Source of Truth)
├── domain/
│   └── model/              # Domain Models murni bebas dependensi Android UI
├── di/
│   └── AppContainer.kt     # Manual Dependency Injection (Zero-reflection, cepat & ringan)
├── export/                 # Manajer Ekspor/Impor JSON
├── notification/           # AlarmManager Scheduler, Notification & Boot Receivers
├── theme/                  # Desain Material 3: Typography, Colors, Theme
├── ui/                     # Jetpack Compose UI & ViewModels
│   ├── calendar/           # Layar Kalender & ViewModel
│   ├── components/         # Reusable UI Components (Cards, Chips, Badges, FilterBar)
│   ├── dashboard/          # Layar Dashboard ringkasan tugas
│   ├── main/               # Bottom Navigation Bar & Container Navigasi Utama
│   ├── settings/           # Layar Pengaturan, Kelola Mata Kuliah & Backup
│   ├── taskdetail/         # Layar Rincian Tugas & Aksi
│   ├── taskform/           # Form Tambah & Edit Tugas
│   └── tasklist/           # Layar Daftar Tugas, Filter, & Search
├── util/                   # Utility helpers (Penyimpanan file, DateTime formatters)
└── widget/                 # Jetpack Glance Home Screen Widget
```

### 💻 Tech Stack & Pustaka Utama

| Komponen | Pustaka / Teknologi |
| :--- | :--- |
| **Bahasa Pemrograman** | [Kotlin 2.x](https://kotlinlang.org/) |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) (BOM) |
| **Design System** | [Material Design 3 (M3)](https://m3.material.io/) |
| **Navigasi** | [Jetpack Navigation 3](https://developer.android.com/guide/navigation) (Type-safe NavKey `@Serializable`) |
| **Database** | [Room Database 2.x](https://developer.android.com/training/data-storage/room) dengan KSP |
| **Home Screen Widget** | [Jetpack Glance AppWidget](https://developer.android.com/jetpack/compose/glance) |
| **Pemuatan Gambar** | [Coil Compose](https://coil-kt.github.io/coil/compose/) |
| **Asinkron & Reactive** | [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [StateFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/) |
| **Serialisasi** | [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) |
| **Testing** | JUnit 4, Kotlinx Coroutines Test, Room Migration Test |

---

## 🚀 Memulai (Getting Started)

### Prasyarat
- **Android Studio** Ladybug (2024.2+) atau versi lebih baru.
- **JDK 17** (disertakan secara default di Android Studio).
- **Android SDK** API Level 36 (Android 16).
- Perangkat Android fisik atau Emulator dengan **Android 8.0 (API 26)** ke atas.

### Langkah Instalasi

1. **Clone repositori:**
   ```bash
   git clone https://github.com/RizxxDev/TaskKu-App.git
   cd TaskKu-App
   ```

2. **Buka di Android Studio:**
   - Pilih **Open** lalu arahkan ke folder `TaskKu-App`.
   - Tunggu proses Gradle Sync selesai hingga dependensi berhasil diunduh.

3. **Jalankan Aplikasi:**
   - Pilih konfigurasi `app`.
   - Pilih perangkat target (Emulator atau USB Debugging).
   - Klik tombol **Run** (`Shift + F10`) atau jalankan via CLI:
     ```bash
     ./gradlew assembleDebug
     ```

---

## 🧪 Pengujian (Testing)

Proyek ini dilengkapi rangkaian unit test komprehensif untuk memastikan keandalan logika bisnis:

```bash
# Menjalankan seluruh Unit Test
./gradlew test
```

Cakupan pengujian meliputi:
- **ViewModel Unit Tests:** `DashboardViewModelTest`, `TaskListViewModelTest`, `TaskFormViewModelTest`, `TaskDetailViewModelTest`, `CalendarViewModelTest`, `SettingsViewModelTest`.
- **Database & Migrasi:** `MigrationTest` memverifikasi integritas skema Room saat migrasi versi.
- **Ekspor & Impor:** `ExportImportTest` menguji validasi format JSON dan pemulihan data.
- **Manajemen File:** `FileStorageHelperTest` menguji penyimpanan lampiran fisik dan pembersihan cache.
- **Notifikasi:** `NotificationSchedulerTest` memastikan penjadwalan alarm deadline akurat.
- **Widget:** `WidgetLogicTest` memverifikasi filter data untuk widget hari ini & besok.

---

## 🛡️ Kebijakan Privasi & Izin (Permissions)

TaskKu sangat menghargai privasi pengguna:
- **Data Lokal Sepenuhnya:** Semua catatan tugas, lampiran, dan pengaturan disimpan secara lokal di perangkat pengguna.
- **Izin yang Digunakan:**
  - `POST_NOTIFICATIONS` *(Android 13+)*: Untuk menampilkan notifikasi pengingat tenggat waktu tugas.
  - `SCHEDULE_EXACT_ALARM`: Untuk memicu alarm pengingat tepat pada waktu yang ditentukan pengguna.
  - `RECEIVE_BOOT_COMPLETED`: Untuk menjadwalkan ulang alarm pengingat secara otomatis setelah ponsel dinyalakan ulang.

---

## 👨‍💻 Kontributor

Dikembangkan oleh **[RizxxDev](https://github.com/RizxxDev)**.

Kontribusi, kritik, dan saran selalu disambut baik! Silakan buat *Issue* baru atau kirimkan *Pull Request*.

---

## 📄 Lisensi

Proyek ini dilisensikan di bawah lisensi **MIT License** - lihat file [LICENSE](LICENSE) untuk detail lengkap.
