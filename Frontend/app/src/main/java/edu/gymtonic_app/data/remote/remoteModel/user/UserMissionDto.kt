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
    val progress: Int = 0,

    @SerializedName("expired")
    val expired: Boolean = false,

    @SerializedName("user_x_mission_completed_date")
    val completedDate: String? = null,

    @SerializedName("mission_name")
    val missionName: String? = null,

    @SerializedName("mission_goal", alternate = ["goal", "missionGoal"])
    val missionGoal: Int? = null,

    @SerializedName("mission_points")
    val missionPoints: Int? = null
)

data class NotificationDto(
    @SerializedName("message")
    val message: String,

    @SerializedName("mission_name")
    val missionName: String,

    @SerializedName("expired")
    val expired: Boolean
)

data class UserMissionsResponseDto(
    @SerializedName("missions")
    val missions: List<UserMissionDto> = emptyList(),

    @SerializedName("expiredMissions")
    val expiredMissions: List<UserMissionDto> = emptyList(),

    @SerializedName("notifications")
    val notifications: List<NotificationDto> = emptyList()
)