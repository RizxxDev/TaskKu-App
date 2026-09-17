# Aturan Pengembangan TaskKu (TaskKu Development Guidelines)

## 1. Arsitektur & Lingkungan Proyek
- **Arsitektur:** MVVM + Clean Architecture / Repository Pattern.
- **UI Framework:** Jetpack Compose + Material 3, Edge-to-Edge.
- **Navigasi:** Jetpack Navigation 3 dengan type-safe `NavKey` (`@Serializable`).
- **Database:** Room 2.x dengan SQLite (`AppDatabase`).
- **Target Platform:** Target/Compile SDK 36 (Android 16), Min SDK 26 (Android 8.0 Oreo), Java 17, Kotlin 2.x.
- **Dependency Injection:** Manual App Container (`AppContainer` di `TaskKuApplication`).

## 2. Guardrails & Aturan Kritis Pengembangan

### A. Preservasi Timestamp Pembuatan (`createdAt`)
- Saat melakukan edit atau pembaruan entitas tugas (`TaskEntity`), nilai `createdAt` dari entitas asli **wajib dipertahankan**. Dilarang menimpa `createdAt` dengan waktu saat ini (`System.currentTimeMillis()`) pada mode edit.

### B. Siklus Hidup File Lampiran Fisik (Storage Lifecycle)
- Penghapusan tugas (`deleteTaskById` / `deleteTasksByIds`) atau penghapusan lampiran wajib menghapus file fisik di storage internal (`context.filesDir/attachments/`) selain menghapus baris tabel di SQLite.
- File yang baru dipilih dari SAF picker tidak boleh langsung disalin ke folder permanen sebelum pengguna menekan tombol "Simpan".

### C. Izin Runtime & Notifikasi (Android 13+ / API 33+)
- Izin `android.permission.POST_NOTIFICATIONS` wajib diminta di runtime UI sebelum mengandalkan alarm deadline atau notifikasi harian.
- Gunakan `SCHEDULE_EXACT_ALARM` dan sediakan mekanisme fallback jika pengguna menolak atau mencabut izin exact alarm.
- Hindari deklarasi izin media yang tidak terpakai (`READ_MEDIA_IMAGES`, dll.) jika interaksi file murni melalui SAF picker (`ActivityResultContracts.GetMultipleContents`).

### D. Navigasi & BackStack (Jetpack Navigation 3)
- Pada root navigator (`MainNavigation`), penanganan tombol Back fisik (`onBack`) wajib memvalidasi `backStack.size > 1` sebelum memanggil `removeLastOrNull()`. Jika `backStack.size == 1`, arahkan ke `finish()` activity agar tidak menyebabkan layar kosong (*blank screen*).

### E. Status Tugas Dinamis (Hindari Hardcoded "Selesai")
- Hindari perbandingan string literal kaku `statusName.equals("Selesai")` untuk menentukan apakah suatu tugas telah selesai. Gunakan properti domain/model `isCompleted: Boolean` agar aplikasi mendukung penambahan status kustom oleh pengguna.

### F. Imutabilitas State Jetpack Compose
- Semua model UI State wajib bersifat immutable. Hindari penggunaan tipe Java mutable seperti `java.util.Calendar` langsung di dalam kelas beranotasi `@Immutable`. Gunakan `java.time.LocalDate`, `Instant`, atau epoch timestamp (`Long`).
