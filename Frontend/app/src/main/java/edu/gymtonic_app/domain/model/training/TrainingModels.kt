package edu.gymtonic_app.domain.model.training

data class TrainingRoutine(
    val id: String,
    val title: String,
    val imageKey: String
)

data class TrainingCategory(
    val id: String,
    val title: String,
    val routines: List<TrainingRoutine>
)

