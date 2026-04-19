package edu.gymtonic_app.data.remote.model

data class TrainingRoutineDto(
    val id: String,
    val title: String,
    val imageKey: String
)

data class TrainingCategoryDto(
    val id: String,
    val title: String,
    val routines: List<TrainingRoutineDto>
)

