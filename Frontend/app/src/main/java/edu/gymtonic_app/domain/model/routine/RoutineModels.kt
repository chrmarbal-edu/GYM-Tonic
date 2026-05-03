package edu.gymtonic_app.domain.model.routine

data class RoutineExercise(
    val id: String,
    val name: String,
    val reps: String,
    val imageKey: String?
)

data class RoutineDetail(
    val id: String,
    val title: String,
    val exercises: List<RoutineExercise>
)

