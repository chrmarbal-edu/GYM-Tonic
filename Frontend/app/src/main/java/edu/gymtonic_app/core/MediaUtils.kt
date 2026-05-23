package edu.gymtonic_app.core

import android.util.Log
import edu.gymtonic_app.BuildConfig
import java.net.URLEncoder

object MediaUtils {
    
    fun resolveBackendMediaUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        val trimmed = path.trim()
        
        // Si ya es una URL absoluta, la devolvemos tal cual
        if (trimmed.startsWith("http") || trimmed.startsWith("//")) {
            return if (trimmed.startsWith("//")) "https:$trimmed" else trimmed
        }

        // Si es una ruta local del dispositivo (cache o almacenamiento)
        if (trimmed.startsWith("/") && (trimmed.contains("/data/") || trimmed.contains("/storage/") || trimmed.contains("/cache/"))) {
            return trimmed
        }

        // Si contiene dominios conocidos de OAuth, es una URL absoluta aunque le falte el protocolo
        if (trimmed.contains("googleusercontent.com") || trimmed.contains("facebook.com") || trimmed.contains("fbcdn.net")) {
            return "https://$trimmed"
        }
        
        val base = BuildConfig.BACKEND_BASE_URL.trim().trimEnd('/')
        val normalizedPath = trimmed.replace("\\", "/")
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
        if (path.isNullOrBlank() || path == "default") {
            return defaultUrl
        }
        
        val trimmed = path.trim()
        
        val resolved = when {
            // Caso 1: URL absoluta (Google, Facebook, o backend ya completo)
            // PRIORIDAD ALTA: Si es una URL, la usamos directamente (si hay internet, Coil la cargará)
            trimmed.startsWith("http") || trimmed.startsWith("//") -> {
                if (trimmed.startsWith("//")) "https:$trimmed" else trimmed
            }
            // Caso 2: Dominios OAuth conocidos sin protocolo
            trimmed.contains("googleusercontent.com") || trimmed.contains("facebook.com") || trimmed.contains("fbcdn.net") -> {
                "https://$trimmed"
            }
            // Caso 3: Ruta local absoluta (ya cacheada en el dispositivo)
            trimmed.startsWith("/") && (trimmed.contains("/data/") || trimmed.contains("/storage/") || trimmed.contains("/cache/")) -> {
                trimmed
            }
            // Caso 4: Ruta relativa de nuestro backend
            else -> {
                resolveBackendMediaUrl(path) ?: defaultUrl
            }
        }
        
        Log.d("MediaUtils", "Resolving user picture: [INPUT: $path] -> [OUTPUT: $resolved]")
        return resolved
    }
}
