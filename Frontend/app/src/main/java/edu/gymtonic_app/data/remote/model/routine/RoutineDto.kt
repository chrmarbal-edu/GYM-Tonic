package edu.gymtonic_app.data.remote.model.routine

import com.google.gson.annotations.SerializedName

data class RoutineDto(
    @SerializedName("routine_id")
    val routineId: String,
    @SerializedName("routine_name")
    val routineName: String,
    @SerializedName("image_key")
    val imageKey: String? = null
)

data class RoutineExerciseDto(
    @SerializedName("exercise_id")
    val exerciseId: String? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("reps")
    val reps: String,
    @SerializedName("image_key")
    val imageKey: String? = null
)

data class RoutineDetailDto(
    @SerializedName("routine_id")
    val routineId: String,
    @SerializedName("routine_name")
    val routineName: String,
    @SerializedName("exercises")
    val exercises: List<RoutineExerciseDto>? = null
) {
    // Asegura lista vacía si exercises es null
    fun safeExercises(): List<RoutineExerciseDto> = exercises ?: emptyList()
}
