package edu.gymtonic_app.data.mapper

import edu.gymtonic_app.data.remote.model.exercise.ExerciseDetailDto
import edu.gymtonic_app.domain.model.exercise.ExerciseDetail

fun ExerciseDetailDto.toDomain(fallbackExerciseId: String = "exercise"): ExerciseDetail {
    val resolvedId = id ?: exerciseId ?: fallbackExerciseId
    val resolvedName = name ?: exerciseName ?: "EJERCICIO"
    val resolvedDuration = durationSeconds ?: durationByType(exerciseType)
    val resolvedImageKey = when {
        !imageKey.isNullOrBlank() -> imageKey
        !exerciseImage.isNullOrBlank() -> exerciseImage.substringBeforeLast(".")
        else -> "fullbody"
    }

    val resolvedInstructions = when {
        !instructions.isNullOrEmpty() -> instructions
        !exerciseDescription.isNullOrBlank() -> listOf(exerciseDescription)
        else -> listOf(
            "Mantén técnica controlada durante toda la serie.",
            "Respira de forma constante y evita compensaciones."
        )
    }

    return ExerciseDetail(
        id = resolvedId,
        name = resolvedName,
        durationSeconds = resolvedDuration,
        imageKey = resolvedImageKey,
        instructions = resolvedInstructions
    )
}

private fun durationByType(exerciseType: Int?): Int {
    return when (exerciseType) {
        1 -> 20
        2 -> 30
        else -> 15
    }
}

