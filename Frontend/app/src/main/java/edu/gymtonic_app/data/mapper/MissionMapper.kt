package edu.gymtonic_app.data.mapper

import edu.gymtonic_app.data.local.localModel.MissionEntity
import edu.gymtonic_app.data.remote.remoteModel.mission.MissionDto

fun MissionDto.toEntity(): MissionEntity {
    return MissionEntity(
        missionId = missionId,
        missionName = missionName ?: "",
        missionType = missionType,
        missionPoints = missionPoints,
        missionObjective = missionObjective
    )
}

fun MissionEntity.toDto(): MissionDto {
    return MissionDto(
        missionId = missionId,
        missionName = missionName,
        missionType = missionType,
        missionPoints = missionPoints,
        missionObjective = missionObjective
    )
}
