package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.local.datasource.local.routineExercise.RoutineExerciseLocalDataSource
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import kotlinx.coroutines.flow.Flow

//de la tabla relacion routine-exercise
class RoutineExerciseRepository(
    private val routineExerciseLocalDataSource: RoutineExerciseLocalDataSource
) {

    // -------------------------
    // READ - Obtener ejercicios
    // -------------------------

    /**
     * Obtiene IDs de ejercicios de una rutina
     * Usado para validaciones
     */
    fun getExerciseIdsForRoutine(routineId: Int): Flow<List<Int>> {
        return routineExerciseLocalDataSource.getExerciseIdsForRoutine(routineId)
    }

    /**
     * Obtiene ejercicios completos de una rutina
     * Usado por RoutineCatalogScreen para mostrar detalles
     */
    fun getExercisesForRoutine(routineId: Int): Flow<List<ExerciseEntity>> {
        return routineExerciseLocalDataSource.getExercisesForRoutine(routineId)
    }

    /**
     * Cuenta ejercicios de una rutina
     */
    fun countExercisesInRoutine(routineId: Int): Flow<Int> {
        return routineExerciseLocalDataSource.countExercisesInRoutine(routineId)
    }

    // -------------------------
    // WRITE - Crear relaciones
    // -------------------------

    /**
     * Agrega un ejercicio a una rutina
     */
    suspend fun addExerciseToRoutine(routineId: Int, exerciseId: Int): Result<Long> {
        return runCatching {
            // Validar que no esté duplicado
            val exists = routineExerciseLocalDataSource.isExerciseInRoutine(routineId, exerciseId)
            if (exists) {
                throw IllegalArgumentException("El ejercicio ya está en esta rutina")
            }
            routineExerciseLocalDataSource.linkExerciseToRoutine(routineId, exerciseId)
        }
    }

    /**
     * Agrega múltiples ejercicios a una rutina (batch)
     * Usado en CreateRoutineScreen al guardar
     * Retorna lista de IDs de relaciones creadas
     */
    suspend fun addMultipleExercisesToRoutine(
        routineId: Int,
        exerciseIds: List<Int>
    ): Result<List<Long>> {
        return runCatching {
            if (exerciseIds.isEmpty()) {
                throw IllegalArgumentException("Debe agregar al menos un ejercicio")
            }
            routineExerciseLocalDataSource.linkMultipleExercisesToRoutine(routineId, exerciseIds)
        }
    }

    // -------------------------
    // WRITE - Eliminar relaciones
    // -------------------------

    /**
     * Quita un ejercicio de una rutina
     */
    suspend fun removeExerciseFromRoutine(routineId: Int, exerciseId: Int): Result<Int> {
        return runCatching {
            routineExerciseLocalDataSource.unlinkExerciseFromRoutine(routineId, exerciseId)
        }
    }

    /**
     * Borra todos los ejercicios de una rutina
     * Usado cuando se borra la rutina completa
     */
    suspend fun deleteAllExercisesForRoutine(routineId: Int): Result<Int> {
        return runCatching {
            routineExerciseLocalDataSource.deleteAllExercisesForRoutine(routineId)
        }
    }
}