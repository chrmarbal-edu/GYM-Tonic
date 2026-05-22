package edu.gymtonic_app.data.local.localDatasource.routine

import edu.gymtonic_app.data.local.dao.RoutineDao
import edu.gymtonic_app.data.local.localModel.rutine.RoutineEntity
import kotlinx.coroutines.flow.Flow

class RoutineLocalDataSource(
    private val routineDao: RoutineDao
) {
    fun getRoutinesByOwner(ownerUserId: Int): Flow<List<RoutineEntity>> {
        return routineDao.getRoutinesByOwner(ownerUserId)
    }

    suspend fun getRoutineByIdForOwner(id: Int, ownerUserId: Int): RoutineEntity? {
        return routineDao.getRoutineByIdForOwner(id, ownerUserId)
    }

    suspend fun getRoutineWithExercisesForOwner(id: Int, ownerUserId: Int): RoutineEntity? {
        return routineDao.getRoutineWithExercisesByIdForOwner(id, ownerUserId)
    }

    suspend fun existsRoutineWithNameForOwner(name: String, ownerUserId: Int): Boolean {
        return routineDao.existsByNameForOwner(name, ownerUserId)
    }

    suspend fun getRoutineById(id: Int): RoutineEntity? {
        return routineDao.getRoutineById(id)
    }

    suspend fun getAllRoutines(): List<RoutineEntity> {
        return routineDao.getAllRoutines()
    }

    suspend fun insertRoutines(routines: List<RoutineEntity>) {
        routineDao.insertRoutines(routines)
    }

    suspend fun createOrUpdateRoutine(routine: RoutineEntity): Long {
        return routineDao.insertRoutines(listOf(routine)).let { 0L } // Using insertRoutines for simplicity
    }

    suspend fun updateRoutine(routine: RoutineEntity): Int {
        return routineDao.updateRoutine(routine)
    }

    suspend fun deleteRoutineByIdForOwner(id: Int, ownerUserId: Int): Int {
        return routineDao.deleteRoutineByIdForOwner(id, ownerUserId)
    }
}
