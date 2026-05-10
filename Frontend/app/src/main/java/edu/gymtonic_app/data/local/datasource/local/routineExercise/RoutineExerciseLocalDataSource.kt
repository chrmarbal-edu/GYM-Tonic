package edu.gymtonic_app.data.local.datasource.local.routineExercise

import edu.gymtonic_app.data.local.dao.RoutineExerciseDao
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.local.localModel.RoutineExerciseEntity
import kotlinx.coroutines.flow.Flow

class RoutineExerciseLocalDataSource(
    private val routineExerciseDao: RoutineExerciseDao
) {

    // -------------------------
    // READ
    // -------------------------

    /**
     * Obtiene IDs de ejercicios de una rutina (Flow)
     * Ligero, solo IDs
     */
    fun getExerciseIdsForRoutine(routineId: Int): Flow<List<Int>> {
        return routineExerciseDao.getExerciseIdsForRoutine(routineId)
    }

    /**
     * Obtiene ejercicios completos de una rutina (Flow)
     * Incluye toda la información del ejercicio
     */
    fun getExercisesForRoutine(routineId: Int): Flow<List<ExerciseEntity>> {
        return routineExerciseDao.getExercisesForRoutine(routineId)
    }

    /**
     * Valida si un ejercicio está en una rutina
     */
    suspend fun isExerciseInRoutine(routineId: Int, exerciseId: Int): Boolean {
        return routineExerciseDao.getRelationship(routineId, exerciseId) != null
    }

    /**
     * Cuenta ejercicios de una rutina
     */
    fun countExercisesInRoutine(routineId: Int): Flow<Int> {
        return routineExerciseDao.countExercisesInRoutine(routineId)
    }

    // -------------------------
    // WRITE
    // -------------------------

    /**
     * Vincula un ejercicio a una rutina
     * Retorna ID de la relación creada
     */
    suspend fun linkExerciseToRoutine(routineId: Int, exerciseId: Int): Long {
        val relation = RoutineExerciseEntity(
            routine_x_exercise_routineid = routineId,
            routine_x_exercise_exerciseid = exerciseId
        )
        return routineExerciseDao.linkExerciseToRoutine(relation)
    }

    /**
     * Vincula múltiples ejercicios a una rutina (batch)
     * Usado en CreateRoutineScreen al guardar
     */
    suspend fun linkMultipleExercisesToRoutine(routineId: Int, exerciseIds: List<Int>): List<Long> {
        val relations = exerciseIds.map { exerciseId ->
            RoutineExerciseEntity(
                routine_x_exercise_routineid = routineId,
                routine_x_exercise_exerciseid = exerciseId
            )
        }
        return routineExerciseDao.linkMultipleExercisesToRoutine(relations)
    }

    /**
     * Desvincula un ejercicio de una rutina
     * Retorna número de registros eliminados
     */
    suspend fun unlinkExerciseFromRoutine(routineId: Int, exerciseId: Int): Int {
        return routineExerciseDao.unlinkExerciseFromRoutine(routineId, exerciseId)
    }

    /**
     * Desvincula TODOS los ejercicios de una rutina
     * Usado cuando se borra una rutina
     */
    suspend fun deleteAllExercisesForRoutine(routineId: Int): Int {
        return routineExerciseDao.deleteAllExercisesForRoutine(routineId)
    }
}