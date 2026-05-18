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

    suspend fun createOrUpdateRoutine(routine: RoutineEntity): Long {
        return routineDao.upsertRoutine(routine)
    }

    suspend fun updateRoutine(routine: RoutineEntity): Int {
        return routineDao.updateRoutine(routine)
    }

    suspend fun deleteRoutineByIdForOwner(id: Int, ownerUserId: Int): Int {
        return routineDao.deleteRoutineByIdForOwner(id, ownerUserId)
    }
}
