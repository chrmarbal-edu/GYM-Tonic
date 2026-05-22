package edu.gymtonic_app.data.remote.remoteModel.group

import com.google.gson.annotations.SerializedName

data class GroupDto(
    @SerializedName("group_id", alternate = ["id", "groupId"])
    val group_id: Int,
    @SerializedName("group_name", alternate = ["name", "groupName"])
    val group_name: String? = null,
    @SerializedName("group_description", alternate = ["description", "groupDescription"])
    val group_description: String? = null,
    @SerializedName("group_image", alternate = ["image", "groupImage"])
    val group_image: String? = null,
    @SerializedName("group_points", alternate = ["points", "groupPoints"])
    val group_points: Int = 0,
    @SerializedName("group_creator_id", alternate = ["creatorId", "groupCreatorId"])
    val group_creator_id: Int = 0
)
