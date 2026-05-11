package edu.gymtonic_app.data.local.datasource.local.routineExercise

import edu.gymtonic_app.data.local.dao.RoutineExerciseDao
import edu.gymtonic_app.data.local.localModel.RoutineExerciseEntity
import edu.gymtonic_app.data.local.localModel.RoutineExerciseWithExerciseEntity
import kotlinx.coroutines.flow.Flow

data class RoutineExerciseInsert(
    val exerciseId: Int,
    val reps: String
)

class RoutineExerciseLocalDataSource(
    private val routineExerciseDao: RoutineExerciseDao
) {
    fun getExerciseIdsForRoutine(routineId: Int): Flow<List<Int>> {
        return routineExerciseDao.getExerciseIdsForRoutine(routineId)
    }

    fun getExercisesForRoutine(routineId: Int): Flow<List<RoutineExerciseWithExerciseEntity>> {
        return routineExerciseDao.getExercisesForRoutine(routineId)
    }

    suspend fun isExerciseInRoutine(routineId: Int, exerciseId: Int): Boolean {
        return routineExerciseDao.getRelationship(routineId, exerciseId) != null
    }

    fun countExercisesInRoutine(routineId: Int): Flow<Int> {
        return routineExerciseDao.countExercisesInRoutine(routineId)
    }

    suspend fun linkExerciseToRoutine(
        routineId: Int,
        exerciseId: Int,
        reps: String
    ): Long {
        return routineExerciseDao.linkExerciseToRoutine(
            RoutineExerciseEntity(
                routine_x_exercise_routineid = routineId,
                routine_x_exercise_exerciseid = exerciseId,
                routine_x_exercise_reps = reps
            )
        )
    }

    suspend fun linkMultipleExercisesToRoutine(
        routineId: Int,
        exerciseLinks: List<RoutineExerciseInsert>
    ): List<Long> {
        val relations = exerciseLinks.map { link ->
            RoutineExerciseEntity(
                routine_x_exercise_routineid = routineId,
                routine_x_exercise_exerciseid = link.exerciseId,
                routine_x_exercise_reps = link.reps
            )
        }
        return routineExerciseDao.linkMultipleExercisesToRoutine(relations)
    }

    suspend fun unlinkExerciseFromRoutine(routineId: Int, exerciseId: Int): Int {
        return routineExerciseDao.unlinkExerciseFromRoutine(routineId, exerciseId)
    }

    suspend fun deleteAllExercisesForRoutine(routineId: Int): Int {
        return routineExerciseDao.deleteAllExercisesForRoutine(routineId)
    }
}