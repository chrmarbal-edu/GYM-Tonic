package edu.gymtonic_app.data.remote.remoteModel.training

import com.google.gson.annotations.SerializedName

data class TrainingRoutineDto(
    @SerializedName("routine_id", alternate = ["id", "routineId"])
    val routine_id: Int,
    @SerializedName("routine_name", alternate = ["name", "routineName"])
    val routine_name: String? = null,
    @SerializedName("routine_image", alternate = ["image", "routineImage", "img", "routine_img", "imageUrl", "image_url", "routine_image_url"])
    val routine_image: String? = null
)

data class TrainingCategoryDto(
    val id: String,
    val title: String,
    val routines: List<TrainingRoutineDto>
)
