package edu.gymtonic_app.data.mapper

import edu.gymtonic_app.data.remote.model.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.model.routine.RoutineExerciseDto
import edu.gymtonic_app.domain.model.routine.RoutineDetail
import edu.gymtonic_app.domain.model.routine.RoutineExercise
import java.text.Normalizer

fun RoutineDetailDto.toDomain(): RoutineDetail {
    return RoutineDetail(
        id = routineId,
        title = routineName,
        exercises = safeExercises().mapIndexed { index, exercise ->
            exercise.toDomain(routineId, index)
        }
    )
}

fun RoutineExerciseDto.toDomain(routineId: String, index: Int): RoutineExercise {
    val fallbackId = "$routineId-${slugify(resolvedName())}-$index"
    return RoutineExercise(
        id = exerciseId?.takeIf { it.isNotBlank() } ?: fallbackId,
        name = resolvedName(),
        reps = resolvedReps(),
        imageKey = resolvedImageKey()
    )
}

fun normalizeRoutineKey(raw: String): String {
    val normalized = Normalizer.normalize(raw, Normalizer.Form.NFD)
    return normalized
        .replace("\\p{M}+".toRegex(), "")
        .lowercase()
        .replace("[\\s_-]+".toRegex(), "")
        .replace("[^a-z0-9]".toRegex(), "")
}

private fun slugify(raw: String): String {
    return raw
        .lowercase()
        .replace(" ", "-")
        .replace("_", "-")
        .replace("[^a-z0-9-]".toRegex(), "")
}

