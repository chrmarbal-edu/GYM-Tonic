package edu.gymtonic_app.data.local.localDatasource.routine

import edu.gymtonic_app.data.local.dao.RoutineDao
import edu.gymtonic_app.data.local.localModel.rutine.RoutineEntity
import kotlinx.coroutines.flow.Flow

class RoutineLocalDataSource(
    private val routineDao: RoutineDao
) {
    // READ (FLOW para observar cambios)
    /**
     * Obtiene todas las rutinas creadas por el usuario en tiempo real
     * Ideal para TrainingScreen (mostrar "Mis rutinas" actualizado)
     */
    fun getAllUserRoutines(): Flow<List<RoutineEntity>> {
        return routineDao.getAllRoutines()
    }

    // READ (Suspend - Una sola vez)
    /**
     * Obtiene rutina por ID (solo datos básicos)
     * Útil para validaciones rápidas
     */
    suspend fun getRoutineById(id: Int): RoutineEntity? {
        return routineDao.getRoutineById(id)
    }

    /**
     * Obtiene rutina CON sus ejercicios relacionados
     * Necesario para mostrar detalles en RoutineCatalogScreen
     */
    suspend fun getRoutineWithExercises(id: Int): RoutineEntity? {
        return routineDao.getRoutineWithExercisesById(id)
    }

    /**
     * Valida si ya existe una rutina con ese nombre
     * Útil en CreateRoutineScreen para evitar duplicados
     */
    suspend fun existsRoutineWithName(name: String): Boolean {
        return routineDao.existsByName(name)
    }

    // -------------------------
    // WRITE
    // -------------------------

    /**
     * Inserta o actualiza una rutina (upsert)
     * Retorna el ID de la rutina insertada
     */
    suspend fun createOrUpdateRoutine(routine: RoutineEntity): Long {
        return routineDao.upsertRoutine(routine)
    }

    /**
     * Actualiza rutina existente
     * Retorna número de registros afectados (0 o 1)
     */
    suspend fun updateRoutine(routine: RoutineEntity): Int {
        return routineDao.updateRoutine(routine)
    }

    /**
     * Borra una rutina por ID
     * Retorna número de registros eliminados (0 o 1)
     */
    suspend fun deleteRoutineById(id: Int): Int {
        return routineDao.deleteRoutineById(id)
    }
}
