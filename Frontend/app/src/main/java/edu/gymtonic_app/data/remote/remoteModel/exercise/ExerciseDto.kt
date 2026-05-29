package edu.gymtonic_app.data.remote.remoteModel.exercise

import com.google.gson.annotations.SerializedName

data class ExerciseDto(
    @SerializedName("exercise_id", alternate = ["id", "exerciseId"])
    val exercise_id: Int,
    @SerializedName("exercise_name", alternate = ["name", "exerciseName"])
    val exercise_name: String? = null,
    @SerializedName("exercise_description", alternate = ["description", "exerciseDescription"])
    val exercise_description: String? = null,
    @SerializedName("exercise_type", alternate = ["type", "exerciseType"])
    val exercise_type: Int,
    @SerializedName("exercise_video", alternate = ["video", "exerciseVideo"])
    val exercise_video: String? = null,
    @SerializedName("exercise_image", alternate = ["image", "exerciseImage", "img", "exercise_img", "imageUrl", "image_url", "exercise_image_url"])
    val exercise_image: String? = null
)

data class ExerciseRequest(
    val name: String,
    val description: String,
    val type: Int,
    val video: String? = null,
    val image: String? = null
)
