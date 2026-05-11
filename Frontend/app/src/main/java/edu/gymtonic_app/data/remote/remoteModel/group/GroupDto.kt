package edu.gymtonic_app.data.remote.remoteModel.group

import com.google.gson.annotations.SerializedName

data class GroupDto(
    @SerializedName("group_id")
    val groupId: Int,

    @SerializedName("group_name")
    val groupName: String,

    @SerializedName("group_description")
    val groupDescription: String? = null,

    @SerializedName("group_image")
    val groupImage: String? = null,

    @SerializedName("group_points")
    val groupPoints: Int? = null,

    @SerializedName("group_creator_id")
    val groupCreatorId: Int? = null
)