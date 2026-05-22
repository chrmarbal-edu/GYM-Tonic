package edu.gymtonic_app.data.mapper

import edu.gymtonic_app.data.local.localModel.rutine.RoutineEntity
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDto

fun RoutineDto.toEntity(ownerUserId: Int): RoutineEntity {
    return RoutineEntity(
        routine_id = routine_id,
        owner_user_id = ownerUserId,
        routine_name = routine_name ?: "Rutina #$routine_id",
        routine_image = routine_image
    )
}

fun RoutineDetailDto.toEntity(ownerUserId: Int): RoutineEntity {
    return RoutineEntity(
        routine_id = routine_id,
        owner_user_id = ownerUserId,
        routine_name = routine_name ?: "Rutina #$routine_id",
        routine_image = routine_image
    )
}

fun RoutineEntity.toDto(): RoutineDto {
    return RoutineDto(
        routine_id = routine_id,
        routine_name = routine_name,
        routine_image = routine_image
    )
}
