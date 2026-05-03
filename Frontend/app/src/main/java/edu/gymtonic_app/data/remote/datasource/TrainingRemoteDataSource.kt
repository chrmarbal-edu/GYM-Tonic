package edu.gymtonic_app.data.remote.datasource

import android.util.Log
import edu.gymtonic_app.data.remote.model.training.TrainingCategoryDto
import edu.gymtonic_app.data.remote.model.training.TrainingRoutineDto
import edu.gymtonic_app.data.remote.services.RetrofitClient

class TrainingRemoteDataSource {
    private val tag = TrainingRemoteDataSource::class.java.simpleName
    private val api = RetrofitClient.apiService

    suspend fun getTrainingCategories(): List<TrainingCategoryDto> {
        return try {
            val response = api.getRoutineCategories()
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Respuesta vacia del servidor")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error categories: ${response.code()} ${response.message()} | $errorBody")
                buildFallbackCategories()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error categories exception: ${e.message}")
            buildFallbackCategories()
        }
    }

    private fun buildFallbackCategories(): List<TrainingCategoryDto> {
        return listOf(
            TrainingCategoryDto(
                id = "recent",
                title = "Recientes",
                routines = listOf(
                    TrainingRoutineDto("back", "Espalda", "espalda"),
                    TrainingRoutineDto("fullbody", "Full Body", "fullbody"),
                    TrainingRoutineDto("push", "Empujes", "pushup")
                )
            ),
            TrainingCategoryDto(
                id = "beginners",
                title = "Para Principiantes",
                routines = listOf(
                    TrainingRoutineDto("stretch", "Estiramientos", "estiramientos"),
                    TrainingRoutineDto("arm", "Brazo", "brazo"),
                    TrainingRoutineDto("calves", "Gemelos", "pierna")
                )
            ),
            TrainingCategoryDto(
                id = "muscle_groups",
                title = "Por Grupo Muscular",
                routines = listOf(
                    TrainingRoutineDto("calves", "Gemelos", "pierna"),
                    TrainingRoutineDto("arm", "Brazo", "brazo"),
                    TrainingRoutineDto("back", "Espalda", "espalda")
                )
            ),
            TrainingCategoryDto(
                id = "recommended",
                title = "Recomendados",
                routines = listOf(
                    TrainingRoutineDto("fullbody", "Full Body", "fullbody"),
                    TrainingRoutineDto("push", "Empujes", "pushup"),
                    TrainingRoutineDto("stretch", "Estiramientos", "estiramientos")
                )
            )
        )
    }
}

