package edu.gymtonic_app.data.remote.datasource

import android.util.Log
import edu.gymtonic_app.data.remote.model.exercise.ExerciseDetailDto
import edu.gymtonic_app.data.remote.services.RetrofitClient

class ExerciseRemoteDataSource {
    private val tag = ExerciseRemoteDataSource::class.java.simpleName
    private val api = RetrofitClient.apiService

    suspend fun getExerciseById(exerciseId: String): ExerciseDetailDto {
        // PRIMARY: consumo real del backend (/exercises/{id}).
        return try {
            val response = api.getExerciseById(exerciseId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Respuesta vacia del servidor")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error exercise detail: ${response.code()} ${response.message()} | $errorBody")
                // FALLBACK TEMPORAL: solo para transicion segura si la API falla.
                buildFallbackExercise(exerciseId)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error exercise detail exception: ${e.message}")
            // FALLBACK TEMPORAL: solo para transicion segura si la API falla.
            buildFallbackExercise(exerciseId)
        }
    }

    // FALLBACK TEMPORAL: eliminar al completar la migracion al backend real.
    private fun buildFallbackExercise(exerciseId: String): ExerciseDetailDto {
        val normalized = exerciseId.lowercase()
        val inferredName = when {
            normalized.contains("estocadas") -> "ESTOCADAS"
            normalized.contains("press") -> "PRESS BANCA"
            normalized.contains("pull") -> "PULL OVER"
            normalized.contains("remo") -> "REMO"
            normalized.contains("sentadilla") -> "SENTADILLA"
            normalized.contains("peso") -> "PESO MUERTO"
            else -> "EJERCICIO"
        }

        val inferredImageKey = when {
            normalized.contains("estocadas") -> "estocadas"
            normalized.contains("press") -> "pressbanca"
            normalized.contains("pull") -> "pullover"
            normalized.contains("remo") -> "remo"
            normalized.contains("sentadilla") -> "sentadilla"
            normalized.contains("peso") -> "pesomuerto"
            else -> "fullbody"
        }

        return ExerciseDetailDto(
            id = exerciseId,
            name = inferredName,
            durationSeconds = 15,
            imageKey = inferredImageKey,
            instructions = listOf(
                "Manten tecnica controlada durante toda la serie.",
                "Respira de forma constante y evita compensaciones.",
                "Ajusta la carga para completar repeticiones con buena forma."
            )
        )
    }
}
