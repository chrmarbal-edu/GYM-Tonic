package edu.gymtonic_app.data.remote.datasource

import edu.gymtonic_app.data.remote.model.training.TrainingCategoryDto
import edu.gymtonic_app.data.remote.model.training.TrainingRoutineDto

class TrainingRemoteDataSource {
    suspend fun getTrainingCategories(): List<TrainingCategoryDto> {
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

