package edu.gymtonic_app.data.remote.model.exercise

import com.google.gson.annotations.SerializedName

data class ExerciseDetailDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("exercise_id")
    val exerciseId: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("exercise_name")
    val exerciseName: String? = null,
    @SerializedName("duration_seconds")
    val durationSeconds: Int? = null,
    @SerializedName("exercise_type")
    val exerciseType: Int? = null,
    @SerializedName("image_key")
    val imageKey: String? = null,
    @SerializedName("exercise_image")
    val exerciseImage: String? = null,
    @SerializedName("instructions")
    val instructions: List<String>? = null,
    @SerializedName("exercise_description")
    val exerciseDescription: String? = null,
    @SerializedName("exercise_video")
    val exerciseVideo: String? = null
)

