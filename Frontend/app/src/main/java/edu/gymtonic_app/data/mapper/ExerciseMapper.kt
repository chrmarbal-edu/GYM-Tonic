package edu.gymtonic_app.data.mapper

import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseDto

fun ExerciseDto.toEntity(): ExerciseEntity {
    return ExerciseEntity(
        exercise_id = exercise_id,
        exercise_name = exercise_name,
        exercise_description = exercise_description,
        exercise_type = exercise_type,
        exercise_video = exercise_video,
        exercise_image = exercise_image
    )
}

fun ExerciseEntity.toDto(): ExerciseDto {
    return ExerciseDto(
        exercise_id = exercise_id,
        exercise_name = exercise_name,
        exercise_description = exercise_description,
        exercise_type = exercise_type,
        exercise_video = exercise_video,
        exercise_image = exercise_image
    )
}
