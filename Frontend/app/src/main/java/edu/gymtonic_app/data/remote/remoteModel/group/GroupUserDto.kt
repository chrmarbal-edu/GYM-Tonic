package edu.gymtonic_app.data.remote.remoteModel.group

import com.google.gson.annotations.SerializedName

data class GroupUserDto(
    @SerializedName("user_x_group_id")
    val groupUserId: Int,

    @SerializedName("user_x_group_userid")
    val userId: Int,

    @SerializedName("user_x_group_groupid")
    val groupId: Int,

    @SerializedName("user_x_group_range")
    val range: Int
)