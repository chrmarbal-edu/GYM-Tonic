package edu.gymtonic_app.data.remote.model.routine

import com.google.gson.annotations.SerializedName

data class RoutineDto(
    @SerializedName("routineId")
    val routineId: String,
    @SerializedName("routineName")
    val routineName: String,
    @SerializedName("imageKey")
    val imageKey: String? = null
)

data class RoutineExerciseDto(
    @SerializedName("exerciseId")
    val exerciseId: String? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("reps")
    val reps: String,
    @SerializedName("imageKey")
    val imageKey: String? = null
)

data class RoutineDetailDto(
    @SerializedName("routineId")
    val routineId: String,
    @SerializedName("routineName")
    val routineName: String,
    @SerializedName("exercises")
    val exercises: List<RoutineExerciseDto> = emptyList()
)
