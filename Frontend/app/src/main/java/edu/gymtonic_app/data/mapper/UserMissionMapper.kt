package edu.gymtonic_app.data.mapper

import edu.gymtonic_app.data.local.localModel.MissionEntity
import edu.gymtonic_app.data.local.localModel.userMission.UserMissionEntity
import edu.gymtonic_app.data.remote.remoteModel.user.UserMissionDto

fun UserMissionDto.toEntity(): UserMissionEntity {
    return UserMissionEntity(
        user_x_mission_id = userMissionId,
        user_x_mission_userid = userMissionUserid,
        user_x_mission_missionid = missionId,
        user_x_mission_expiration = userMissionExpiration,
        user_x_mission_completed = completed,
        user_x_mission_progress = progress
    )
}

fun UserMissionDto.toMissionEntity(): MissionEntity? {
    if (missionName == null) return null
    return MissionEntity(
        missionId = missionId,
        missionName = missionName,
        missionType = 0, // Not available in UserMissionDto
        missionPoints = missionPoints ?: 0,
        missionObjective = missionGoal ?: 0
    )
}
