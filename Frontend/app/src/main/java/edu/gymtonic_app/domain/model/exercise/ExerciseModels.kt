package edu.gymtonic_app.domain.model.exercise

data class ExerciseDetail(
    val id: String,
    val name: String,
    val durationSeconds: Int,
    val imageKey: String,
    val instructions: List<String>
)

