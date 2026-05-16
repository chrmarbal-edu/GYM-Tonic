package edu.gymtonic_app.data.remote.remoteModel.routine

import com.google.gson.annotations.SerializedName

data class RoutineDto(
    @SerializedName("routine_id", alternate = ["id", "routineId"])
    val routine_id: Int,
    @SerializedName("routine_name", alternate = ["name", "routineName"])
    val routine_name: String? = null,
    @SerializedName("routine_image", alternate = ["image", "routineImage", "routine_image_url"])
    val routine_image: String? = null
)

data class RoutineExerciseDto(
    @SerializedName("exercise_id", alternate = ["id", "exerciseId"])
    val exercise_id: Int,
    @SerializedName("exercise_name", alternate = ["name", "exerciseName"])
    val exercise_name: String? = null,
    @SerializedName("exercise_description", alternate = ["description", "exerciseDescription"])
    val exercise_description: String? = null,
    @SerializedName("exercise_type", alternate = ["type", "exerciseType"])
    val exercise_type: Int = 0,
    @SerializedName("exercise_video", alternate = ["video", "exerciseVideo"])
    val exercise_video: String? = null,
    @SerializedName("exercise_image", alternate = ["image", "exerciseImage", "exercise_image_url", "image_key"])
    val exercise_image: String? = null,
    val reps: String? = null
)

data class RoutineDetailDto(
    @SerializedName("routine_id", alternate = ["id", "routineId"])
    val routine_id: Int,
    @SerializedName("routine_name", alternate = ["name", "routineName"])
    val routine_name: String? = null,
    @SerializedName("routine_image", alternate = ["image", "routineImage", "routine_image_url"])
    val routine_image: String? = null,
    val exercises: List<RoutineExerciseDto>? = null
) {
    fun safeExercises(): List<RoutineExerciseDto> = exercises ?: emptyList()
}