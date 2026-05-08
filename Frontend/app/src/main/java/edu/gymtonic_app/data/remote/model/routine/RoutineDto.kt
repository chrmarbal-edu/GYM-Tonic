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
    @SerializedName("exercise_name")
    val exerciseName: String? = null,
    @SerializedName("exercise_description")
    val exerciseDescription: String? = null,
    @SerializedName("exercise_type")
    val exerciseType: Int? = null,
    @SerializedName("exercise_video")
    val exerciseVideo: String? = null,
    @SerializedName("exercise_image")
    val exerciseImage: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("reps")
    val reps: String? = null,
    @SerializedName("image_key")
    val imageKey: String? = null
) {
    fun resolvedName(): String = exerciseName ?: name ?: "EJERCICIO"

    fun resolvedReps(): String {
        if (!reps.isNullOrBlank()) {
            return reps
        }

        return when (exerciseType) {
            1 -> "x20"
            2 -> "x30s"
            else -> "x12"
        }
    }

    fun resolvedImageKey(): String? {
        if (!imageKey.isNullOrBlank()) {
            return imageKey
        }

        if (!exerciseImage.isNullOrBlank()) {
            return exerciseImage.substringBeforeLast(".")
        }

        return null
    }
}

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
