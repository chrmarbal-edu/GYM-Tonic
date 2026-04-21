package edu.gymtonic_app.data.remote.model.exercise

data class ExerciseDetailDto(
    val id: String,
    val name: String,
    val durationSeconds: Int,
    val imageKey: String,
    val instructions: List<String>
)

