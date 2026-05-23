package edu.gymtonic_app.data.remote.remoteModel.training

import com.google.gson.annotations.SerializedName

data class TrainingRoutineDto(
    @SerializedName("routine_id", alternate = ["id", "routineId"])
    val routine_id: Int = 0,
    @SerializedName("routine_name", alternate = ["name", "routineName", "title"])
    val routine_name: String? = null,
    @SerializedName("routine_image", alternate = ["image", "routineImage", "img", "imageUrl", "image_url", "routine_image_url"])
    val routine_image: String? = null,
    @SerializedName("routine_creator_id")
    val routine_creator_id: Int? = null,
    @SerializedName("routine_groupid")
    val routine_groupid: Int? = null
) {
    fun displayName(): String = routine_name?.takeIf { it.isNotBlank() } ?: "Rutina"
}

data class TrainingCategoryDto(
    val id: String,
    val title: String,
    val routines: List<TrainingRoutineDto>
)
