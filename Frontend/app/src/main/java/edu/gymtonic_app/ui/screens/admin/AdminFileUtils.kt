package edu.gymtonic_app.ui.screens.admin

import android.content.Context
import android.net.Uri
import java.io.File

fun uriToUploadFile(context: Context, uri: Uri, prefix: String): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val extension = context.contentResolver.getType(uri)?.let { mime ->
            when {
                mime.contains("png") -> ".png"
                mime.contains("webp") -> ".webp"
                mime.contains("gif") -> ".gif"
                mime.contains("jpeg") || mime.contains("jpg") -> ".jpg"
                mime.contains("mp4") -> ".mp4"
                mime.contains("webm") -> ".webm"
                mime.contains("quicktime") -> ".mov"
                else -> ""
            }
        }.orEmpty().ifBlank { ".bin" }
        val temp = File.createTempFile(prefix, extension, context.cacheDir)
        temp.outputStream().use { output -> inputStream.copyTo(output) }
        inputStream.close()
        temp
    } catch (_: Exception) {
        null
    }
}
