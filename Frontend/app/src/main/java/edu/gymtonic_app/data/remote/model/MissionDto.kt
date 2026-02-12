package edu.gymtonic_app.data.remote.model

data class MissionDto(
    val missionId: Int,
    val missionName: String,
    val missionType: String,
    val missionPoints: Int,
    val missionObjective: Int
)
