package edu.gymtonic_app.data.remote.remoteModel.mission

import com.google.gson.annotations.SerializedName

data class MissionDto(
    @SerializedName("mission_id", alternate = ["id", "missionId"])
    val missionId: Int,

    @SerializedName("mission_name", alternate = ["name", "missionName"])
    val missionName: String,

    @SerializedName("mission_type", alternate = ["type", "missionType"])
    val missionType: Int,

    @SerializedName("mission_points", alternate = ["points", "missionPoints"])
    val missionPoints: Int,

    @SerializedName("mission_objective", alternate = ["objective", "missionObjective"])
    val missionObjective: Int,

    @SerializedName("mission_goal", alternate = ["goal", "missionGoal"])
    val missionGoal: Int? = null
)