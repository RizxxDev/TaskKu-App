package com.example.taskku.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class FileStorageHelperTest {

    @Test
    fun formatFileSize_returnsCorrectUnitsForBytes() {
        assertEquals("0 B", FileStorageHelper.formatFileSize(0))
        assertEquals("500 B", FileStorageHelper.formatFileSize(500))
        assertEquals("1023 B", FileStorageHelper.formatFileSize(1023))
    }

    @Test
    fun formatFileSize_returnsCorrectUnitsForKilobytes() {
        Locale.setDefault(Locale.US)
        assertEquals("1.0 KB", FileStorageHelper.formatFileSize(1024))
        assertEquals("1.5 KB", FileStorageHelper.formatFileSize(1536))
        assertEquals("100.0 KB", FileStorageHelper.formatFileSize(102400))
    }

    @Test
    fun formatFileSize_returnsCorrectUnitsForMegabytes() {
        Locale.setDefault(Locale.US)
        assertEquals("1.0 MB", FileStorageHelper.formatFileSize(1024 * 1024))
        assertEquals("2.5 MB", FileStorageHelper.formatFileSize((2.5 * 1024 * 1024).toLong()))
        assertEquals("50.0 MB", FileStorageHelper.formatFileSize(50 * 1024 * 1024))
    }
}
