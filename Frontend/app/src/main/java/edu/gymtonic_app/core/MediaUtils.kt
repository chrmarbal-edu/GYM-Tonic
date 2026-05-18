package edu.gymtonic_app.core

import edu.gymtonic_app.BuildConfig
import java.net.URLEncoder

object MediaUtils {
    
    fun resolveBackendMediaUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        if (path.startsWith("http")) return path
        
        val base = BuildConfig.BACKEND_BASE_URL.trim().trimEnd('/')
        val normalizedPath = path.trim().replace("\\", "/")
        val key = if (normalizedPath.startsWith("/")) normalizedPath else "/$normalizedPath"
        
        return try {
            val encodedPath = key.split("/").joinToString("/") { segment ->
                URLEncoder.encode(segment, "UTF-8").replace("+", "%20")
            }
            "$base$encodedPath"
        } catch (e: Exception) {
            "$base$key"
        }
    }

    fun resolveUserPictureUrl(path: String?): String {
        val defaultUrl = "${BuildConfig.BACKEND_BASE_URL.trimEnd('/')}/images/users/default/user.jpg"
        if (path.isNullOrBlank() || path == "default") return defaultUrl
        if (path.startsWith("http")) return path
        
        return resolveBackendMediaUrl(path) ?: defaultUrl
    }
}
