package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.local.localDatasource.routineExercise.RoutineExerciseInsert
import edu.gymtonic_app.data.local.localDatasource.routineExercise.RoutineExerciseLocalDataSource
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.local.localModel.routineExercise.RoutineExerciseWithExerciseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RoutineExerciseRepository(
    private val routineExerciseLocalDataSource: RoutineExerciseLocalDataSource
) {
    private fun repsByExerciseType(type: Int): String {
        return when (type) {
            1 -> "x20"
            2 -> "x30s"
            else -> "x12"
        }
    }

    fun getExerciseIdsForRoutine(routineId: Int): Flow<List<Int>> {
        return routineExerciseLocalDataSource.getExerciseIdsForRoutine(routineId)
    }

    fun getExercisesForRoutine(routineId: Int): Flow<List<RoutineExerciseWithExerciseEntity>> {
        return routineExerciseLocalDataSource.getExercisesForRoutine(routineId)
    }

    fun countExercisesInRoutine(routineId: Int): Flow<Int> {
        return routineExerciseLocalDataSource.countExercisesInRoutine(routineId)
    }

    suspend fun addExerciseToRoutine(routineId: Int, exercise: ExerciseEntity): Result<Long> {
        return runCatching {
            val exists = routineExerciseLocalDataSource.isExerciseInRoutine(routineId, exercise.exercise_id)
            if (exists) {
                throw IllegalArgumentException("El ejercicio ya está en esta rutina")
            }

            val insertedId = routineExerciseLocalDataSource.linkExerciseToRoutine(
                routineId = routineId,
                exerciseId = exercise.exercise_id,
                reps = repsByExerciseType(exercise.exercise_type)
            )

            // Por seguridad extra: Room devuelve -1 cuando IGNORE evita inserción
            if (insertedId == -1L) {
                throw IllegalArgumentException("El ejercicio ya está en esta rutina")
            }

            insertedId
        }
    }

    suspend fun addMultipleExercisesToRoutine(
        routineId: Int,
        exercises: List<ExerciseEntity>
    ): Result<List<Long>> {
        return runCatching {
            if (exercises.isEmpty()) {
                throw IllegalArgumentException("Debe agregar al menos un ejercicio")
            }

            // Quita repetidos dentro del propio payload
            val uniqueInput = exercises.distinctBy { it.exercise_id }

            // Evita insertar los que ya existen en la rutina
            val existingIds = routineExerciseLocalDataSource
                .getExerciseIdsForRoutine(routineId)
                .first()
                .toSet()

            val toInsert = uniqueInput.filterNot { it.exercise_id in existingIds }

            if (toInsert.isEmpty()) {
                throw IllegalArgumentException("Todos los ejercicios seleccionados ya estaban en la rutina")
            }

            val exerciseLinks = toInsert.map { exercise ->
                RoutineExerciseInsert(
                    exerciseId = exercise.exercise_id,
                    reps = repsByExerciseType(exercise.exercise_type)
                )
            }

            routineExerciseLocalDataSource.linkMultipleExercisesToRoutine(routineId, exerciseLinks)
        }
    }

    suspend fun removeExerciseFromRoutine(routineId: Int, exerciseId: Int): Result<Int> {
        return runCatching {
            routineExerciseLocalDataSource.unlinkExerciseFromRoutine(routineId, exerciseId)
        }
    }

    suspend fun deleteAllExercisesForRoutine(routineId: Int): Result<Int> {
        return runCatching {
            routineExerciseLocalDataSource.deleteAllExercisesForRoutine(routineId)
        }
    }
}