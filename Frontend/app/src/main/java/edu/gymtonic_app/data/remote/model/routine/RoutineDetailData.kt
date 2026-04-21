package edu.gymtonic_app.data.remote.model.routine

data class RoutineExerciseData(
    val id: String,
    val name: String,
    val reps: String,
    val imageKey: String?
)

data class RoutineDetailData(
    val id: String,
    val title: String,
    val exercises: List<RoutineExerciseData>
)

