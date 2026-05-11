package edu.gymtonic_app.data.remote.remoteModel.mission

import com.google.gson.annotations.SerializedName

data class MissionDto(
    @SerializedName("mission_id")
    val missionId: Int,

    @SerializedName("mission_name")
    val missionName: String,

    @SerializedName("mission_type")
    val missionType: Int,

    @SerializedName("mission_points")
    val missionPoints: Int,

    @SerializedName("mission_objective")
    val missionObjective: Int
)