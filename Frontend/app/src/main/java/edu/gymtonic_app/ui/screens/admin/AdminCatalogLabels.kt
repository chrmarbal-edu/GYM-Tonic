package edu.gymtonic_app.ui.screens.admin

import edu.gymtonic_app.BuildConfig

fun resolveBackendMediaUrl(path: String?): String? {
    if (path.isNullOrBlank()) return null
    if (path.startsWith("http")) return path
    
    val base = BuildConfig.BACKEND_BASE_URL.trim().trimEnd('/')
    // Normalizamos el path: quitar espacios al inicio/final, asegurar barra inicial, 
    // y cambiar backslashes por slashes de URL
    val normalizedPath = path.trim().replace("\\", "/")
    val key = if (normalizedPath.startsWith("/")) normalizedPath else "/$normalizedPath"
    
    // Codificamos caracteres especiales (como acentos o espacios) que puedan venir en el nombre del archivo
    // Usamos una aproximación segura para no romper las barras inclinadas /
    val encodedPath = key.split("/").joinToString("/") { segment ->
        java.net.URLEncoder.encode(segment, "UTF-8").replace("+", "%20")
    }
    
    return "$base$encodedPath"
}

fun exerciseTypeLabel(type: Int): String = when (type) {
    0 -> "Cardio"
    1 -> "Pectorales"
    2 -> "Espalda"
    3 -> "Biceps / Muñecas"
    4 -> "Triceps"
    5 -> "Cuadriceps"
    6 -> "Femorales / Isquios"
    7 -> "Hombros"
    8 -> "Gemelos"
    9 -> "Abdominales"
    10 -> "Full Body"
    else -> "Tipo $type"
}

val exerciseTypeOptions: List<Pair<Int, String>> = listOf(
    0 to "Cardio",
    1 to "Pectorales",
    2 to "Espalda",
    3 to "Biceps / Muñecas",
    4 to "Triceps",
    5 to "Cuadriceps",
    6 to "Femorales / Isquios",
    7 to "Hombros",
    8 to "Gemelos",
    9 to "Abdominales",
    10 to "Full Body"
)

fun missionObjectiveLabel(objective: Int): String = when (objective) {
    0 -> "Mantenimiento"
    1 -> "Pérdida de Peso"
    2 -> "Ganancia Muscular"
    3 -> "Rendimiento / Resistencia"
    else -> "Objetivo $objective"
}

val missionObjectiveOptions: List<Pair<Int, String>> = listOf(
    0 to "Mantenimiento",
    1 to "Pérdida de Peso",
    2 to "Ganancia Muscular",
    3 to "Rendimiento / Resistencia"
)

fun missionTypeLabel(type: Int): String = when (type) {
    0 -> "Diaria"
    1 -> "Semanal"
    2 -> "Mensual"
    else -> "Tipo $type"
}

val missionTypeOptions: List<Pair<Int, String>> = listOf(
    0 to "Diaria",
    1 to "Semanal",
    2 to "Mensual"
)

fun groupRoleLabel(range: Int): String = when (range) {
    0 -> "Miembro"
    1 -> "Moderador"
    2 -> "Líder"
    else -> "Rol $range"
}

fun oauthProviderLabel(oauth: String?): String? {
    if (oauth.isNullOrBlank()) return null
    return when (oauth.lowercase()) {
        "google" -> "Google"
        "facebook" -> "Facebook"
        else -> oauth.replaceFirstChar { it.uppercase() }
    }
}
