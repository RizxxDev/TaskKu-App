package com.example.taskku.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.taskku.domain.model.Attachment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

object FileStorageHelper {

    private const val MAX_PHOTO_BYTES = 10 * 1024 * 1024L   // 10 MB
    private const val MAX_VIDEO_BYTES = 50 * 1024 * 1024L   // 50 MB
    private const val MAX_DOC_BYTES = 25 * 1024 * 1024L     // 25 MB

    fun saveUriToTempFile(context: Context, uri: Uri): Pair<Attachment?, String?> {
        try {
            var fileName = "attachment_"
            var fileSize = 0L

            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex) ?: fileName
                    }
                    if (sizeIndex != -1) {
                        fileSize = cursor.getLong(sizeIndex)
                    }
                }
            }

            val mimeType = context.contentResolver.getType(uri) ?: ""
            val extension = fileName.substringAfterLast(".", "").lowercase()
            val fileType = when {
                mimeType.startsWith("image/") || extension in listOf("jpg", "jpeg", "png", "webp") -> "photo"
                mimeType.startsWith("video/") || extension in listOf("mp4", "3gp") -> "video"
                else -> "document"
            }

            // Check size limits
            val maxAllowed = when (fileType) {
                "photo" -> MAX_PHOTO_BYTES
                "video" -> MAX_VIDEO_BYTES
                else -> MAX_DOC_BYTES
            }

            if (fileSize > maxAllowed) {
                val maxMb = maxAllowed / (1024 * 1024)
                return Pair(null, "Ukuran file '$fileName' melebihi batas maksimal ($maxMb MB)")
            }

            val tempDir = File(context.cacheDir, "temp_attachments").apply {
                if (!exists()) mkdirs()
            }

            val safeFileName = "temp_${System.currentTimeMillis()}_$fileName"
            val tempFile = File(tempDir, safeFileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            val actualSize = tempFile.length()
            if (actualSize > maxAllowed) {
                tempFile.delete()
                val maxMb = maxAllowed / (1024 * 1024)
                return Pair(null, "Ukuran file '$fileName' melebihi batas maksimal ($maxMb MB)")
            }

            val attachment = Attachment(
                taskId = 0,
                fileName = fileName,
                filePath = tempFile.absolutePath,
                fileType = fileType,
                fileSize = actualSize
            )

            return Pair(attachment, null)
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(null, "Gagal menyimpan file: ${e.message}")
        }
    }

    fun commitAttachment(context: Context, attachment: Attachment): Attachment {
        try {
            val file = File(attachment.filePath)
            val tempDir = File(context.cacheDir, "temp_attachments")
            val isInTemp = try {
                file.canonicalPath.startsWith(tempDir.canonicalPath)
            } catch (e: Exception) {
                file.absolutePath.contains("temp_attachments")
            }

            if (isInTemp && file.exists()) {
                val permanentDir = File(context.filesDir, "attachments").apply {
                    if (!exists()) mkdirs()
                }
                val permanentFileName = "${System.currentTimeMillis()}_${attachment.fileName}"
                val destinationFile = File(permanentDir, permanentFileName)

                val moved = file.renameTo(destinationFile)
                if (!moved) {
                    file.inputStream().use { input ->
                        destinationFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    file.delete()
                }
                return attachment.copy(filePath = destinationFile.absolutePath, fileSize = destinationFile.length())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return attachment
    }

    fun deleteTempFile(context: Context, attachment: Attachment) {
        try {
            val file = File(attachment.filePath)
            val tempDir = File(context.cacheDir, "temp_attachments")
            val isInTemp = try {
                file.canonicalPath.startsWith(tempDir.canonicalPath)
            } catch (e: Exception) {
                file.absolutePath.contains("temp_attachments")
            }
            if (isInTemp && file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveUriToFile(context: Context, uri: Uri): Pair<Attachment?, String?> {
        val (tempAttachment, error) = saveUriToTempFile(context, uri)
        if (tempAttachment != null) {
            val committed = commitAttachment(context, tempAttachment)
            return Pair(committed, null)
        }
        return Pair(null, error)
    }

    fun openAttachment(context: Context, attachment: Attachment) {
        val file = File(attachment.filePath)
        if (!file.exists()) {
            Toast.makeText(context, "File tidak ditemukan di penyimpanan", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val extension = file.extension.lowercase()
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(intent, "Buka dengan").apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Tidak ada aplikasi untuk membuka file ini", Toast.LENGTH_SHORT).show()
        }
    }

    fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> String.format(Locale.getDefault(), "%.1f MB", bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> String.format(Locale.getDefault(), "%.1f KB", bytes / 1024.0)
            else -> "$bytes B"
        }
    }
}
