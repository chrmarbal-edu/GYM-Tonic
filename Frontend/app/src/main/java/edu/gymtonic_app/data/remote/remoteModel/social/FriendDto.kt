package edu.gymtonic_app.data.remote.remoteModel.social

import com.google.gson.annotations.SerializedName

data class FriendDto(
    @SerializedName("friend_id")
    val id: Int,

    @SerializedName("friend_userid1")
    val userId1: Int,

    @SerializedName("friend_userid2")
    val userId2: Int
)