package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.local.datasource.local.routineExercise.RoutineExerciseInsert
import edu.gymtonic_app.data.local.datasource.local.routineExercise.RoutineExerciseLocalDataSource
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.local.localModel.RoutineExerciseWithExerciseEntity
import kotlinx.coroutines.flow.Flow

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

    suspend fun addExerciseToRoutine(routineId: Int, exerciseId: Int): Result<Long> {
        return runCatching {
            val exists = routineExerciseLocalDataSource.isExerciseInRoutine(routineId, exerciseId)
            if (exists) {
                throw IllegalArgumentException("El ejercicio ya está en esta rutina")
            }
            routineExerciseLocalDataSource.linkExerciseToRoutine(
                routineId = routineId,
                exerciseId = exerciseId,
                reps = "x12"
            )
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

            val exerciseLinks = exercises.map { exercise ->
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