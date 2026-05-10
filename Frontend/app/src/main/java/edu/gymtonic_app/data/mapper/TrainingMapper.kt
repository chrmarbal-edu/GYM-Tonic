package edu.gymtonic_app.data.mapper

import edu.gymtonic_app.data.remote.model.training.TrainingCategoryDto
import edu.gymtonic_app.domain.model.training.TrainingCategory
import edu.gymtonic_app.domain.model.training.TrainingRoutine

fun TrainingCategoryDto.toDomain(): TrainingCategory {
    return TrainingCategory(
        id = id,
        title = title,
        routines = routines.map { routine ->
            TrainingRoutine(
                id = routine.id,
                title = routine.title,
                imageKey = routine.imageKey
            )
        }
    )
}

