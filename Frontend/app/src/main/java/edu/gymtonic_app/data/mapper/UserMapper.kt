package edu.gymtonic_app.data.mapper

import edu.gymtonic_app.data.local.localModel.user.UserEntity
import edu.gymtonic_app.data.remote.remoteModel.user.UserDto

fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        user_id = userId,
        user_username = userUsername ?: "",
        user_name = userName ?: "",
        user_birthdate = userBirthdate ?: "",
        user_email = userEmail ?: "",
        user_picture = userPicture,
        user_height = userHeight,
        user_weight = userWeight,
        user_objective = userObjective,
        user_points = userPoints,
        user_role = userRole
    )
}

fun UserEntity.toDto(): UserDto {
    return UserDto(
        userId = user_id,
        userUsername = user_username,
        userName = user_name,
        userBirthdate = user_birthdate,
        userEmail = user_email,
        userPicture = user_picture,
        userHeight = user_height,
        userWeight = user_weight,
        userObjective = user_objective,
        userPoints = user_points,
        userRole = user_role
    )
}
