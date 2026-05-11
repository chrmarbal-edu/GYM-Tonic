package edu.gymtonic_app.data.remote.remoteModel.social

import com.google.gson.annotations.SerializedName

data class FrequestDto(
    @SerializedName("frequest_id")
    val frequestId: Int,

    @SerializedName("frequest_sender")
    val frequestSender: Int,

    @SerializedName("frequest_receiver")
    val frequestReceiver: Int,

    @SerializedName("frequest_status")
    val frequestStatus: Int
)