package edu.gymtonic_app.data.util

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

object MediaCacheManager {
    private val client = OkHttpClient()

    suspend fun downloadAndCache(context: Context, url: String?): String? {
        if (url == null || url.isBlank() || !url.startsWith("http")) return url

        return withContext(Dispatchers.IO) {
            try {
                // Generar un nombre de archivo único basado en la URL para evitar colisiones
                val fileName = url.hashCode().toString() + "_" + url.substringAfterLast("/", "file.bin")
                val directory = File(context.cacheDir, "media_cache")
                if (!directory.exists()) directory.mkdirs()

                val destinationFile = File(directory, fileName)

                if (destinationFile.exists() && destinationFile.length() > 0) {
                    return@withContext destinationFile.absolutePath
                }

                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext url

                    response.body?.let { body ->
                        FileOutputStream(destinationFile).use { output ->
                            body.byteStream().copyTo(output)
                        }
                        return@withContext destinationFile.absolutePath
                    }
                }
                url
            } catch (e: Exception) {
                Log.e("MediaCacheManager", "Error downloading $url", e)
                url
            }
        }
    }
}
