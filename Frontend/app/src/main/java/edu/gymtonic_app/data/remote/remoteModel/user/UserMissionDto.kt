package edu.gymtonic_app.data.remote.remoteModel.user

import com.google.gson.annotations.SerializedName

data class UserMissionDto(
    @SerializedName("user_x_mission_id")
    val userMissionId: Int,

    @SerializedName("user_x_mission_userid")
    val userMissionUserid: Int,

    @SerializedName("user_x_mission_missionid")
    val missionId: Int,

    @SerializedName("user_x_mission_expiration")
    val userMissionExpiration: String,

    @SerializedName("user_x_mission_completed")
    val completed: Boolean = false,

    @SerializedName("user_x_mission_progress")
    val progress: Int = 0
)