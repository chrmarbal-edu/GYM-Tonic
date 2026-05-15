package edu.gymtonic_app.data.remote.remoteModel.group

import com.google.gson.annotations.SerializedName

data class GroupDto(
    @SerializedName("group_id")
    val group_id: Int,
    @SerializedName("group_name")
    val group_name: String? = null,
    @SerializedName("group_description")
    val group_description: String? = null,
    @SerializedName("group_image")
    val group_image: String? = null,
    @SerializedName("group_points")
    val group_points: Int = 0,
    @SerializedName("group_creator_id")
    val group_creator_id: Int = 0
)
