package edu.gymtonic_app.data.remote.remoteModel.social

import com.google.gson.annotations.SerializedName

data class FriendRequestWithUserDto(
    @SerializedName("frequest_id")
    val frequestId: Int,

    @SerializedName("frequest_sender")
    val frequestSender: Int,

    @SerializedName("frequest_receiver")
    val frequestReceiver: Int,

    @SerializedName("frequest_status")
    val frequestStatus: Int,

    @SerializedName("sender_username")
    val senderUsername: String? = null,

    @SerializedName("sender_name")
    val senderName: String? = null,

    @SerializedName("sender_picture")
    val senderPicture: String? = null,

    @SerializedName("receiver_username")
    val receiverUsername: String? = null,

    @SerializedName("receiver_name")
    val receiverName: String? = null,

    @SerializedName("receiver_picture")
    val receiverPicture: String? = null
)

data class FriendRequestsByUserResponse(
    @SerializedName("incoming")
    val incoming: List<FriendRequestWithUserDto> = emptyList(),

    @SerializedName("outgoing")
    val outgoing: List<FriendRequestWithUserDto> = emptyList()
)
