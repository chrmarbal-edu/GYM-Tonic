package edu.gymtonic_app.data.remote.remoteModel.group

import com.google.gson.annotations.SerializedName

data class GroupUserDto(
    @SerializedName("Group_x_user_id", alternate = ["user_x_group_id"])
    val groupUserId: Int = 0,

    @SerializedName("Group_x_user_userid", alternate = ["user_x_group_userid"])
    val userId: Int = 0,

    @SerializedName("Group_x_user_groupid", alternate = ["user_x_group_groupid"])
    val groupId: Int = 0,

    @SerializedName("Group_x_user_range", alternate = ["user_x_group_range"])
    val range: Int = 0,

    @SerializedName("user_username")
    val userUsername: String? = null,

    @SerializedName("user_name")
    val userName: String? = null,

    @SerializedName("user_points")
    val userPoints: Int? = null,

    @SerializedName("user_picture")
    val userPicture: String? = null
)
