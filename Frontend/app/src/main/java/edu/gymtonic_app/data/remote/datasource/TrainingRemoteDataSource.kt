package edu.gymtonic_app.data.remote.datasource

import android.util.Log
import edu.gymtonic_app.data.remote.model.training.TrainingCategoryDto
import edu.gymtonic_app.data.remote.model.training.TrainingRoutineDto
import edu.gymtonic_app.data.remote.services.RetrofitClient

class TrainingRemoteDataSource {
    private val tag = TrainingRemoteDataSource::class.java.simpleName
    private val api = RetrofitClient.apiService

    suspend fun getTrainingCategories(): List<TrainingCategoryDto> {
        // PRIMARY: consumo real del backend (/routines/categories).
        return try {
            val response = api.getRoutineCategories()
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Respuesta vacia del servidor")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error categories: ${response.code()} ${response.message()} | $errorBody")
                // FALLBACK TEMPORAL: solo para transicion segura si la API falla.
                buildFallbackCategories()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error categories exception: ${e.message}")
            // FALLBACK TEMPORAL: solo para transicion segura si la API falla.
            buildFallbackCategories()
        }
    }

    // FALLBACK TEMPORAL: eliminar al completar la migracion al backend real.
    private fun buildFallbackCategories(): List<TrainingCategoryDto> {
        return listOf(
            TrainingCategoryDto(
                id = "recent",
                title = "Recientes",
                routines = listOf(
                    TrainingRoutineDto("5", "Flexibilidad y Movilidad", "sunsalute"),
                    TrainingRoutineDto("4", "Piernas y Gluteos", "squat"),
                    TrainingRoutineDto("3", "Cardio Quema Grasa", "running")
                )
            ),
            TrainingCategoryDto(
                id = "beginners",
                title = "Para Principiantes",
                routines = listOf(
                    TrainingRoutineDto("1", "Full Body Principiante", "squat")
                )
            ),
            TrainingCategoryDto(
                id = "muscle_groups",
                title = "Por Grupo Muscular",
                routines = listOf(
                    TrainingRoutineDto("2", "Tren Superior Avanzado", "row"),
                    TrainingRoutineDto("1", "Full Body Principiante", "squat"),
                    TrainingRoutineDto("4", "Piernas y Gluteos", "squat")
                )
            ),
            TrainingCategoryDto(
                id = "recommended",
                title = "Recomendados",
                routines = listOf(
                    TrainingRoutineDto("3", "Cardio Quema Grasa", "running"),
                    TrainingRoutineDto("2", "Tren Superior Avanzado", "row"),
                    TrainingRoutineDto("1", "Full Body Principiante", "squat")
                )
            )
        )
    }
}

