package edu.gymtonic_app.data.local.localDatasource.routine

import edu.gymtonic_app.data.local.dao.RecentRoutineDao
import edu.gymtonic_app.data.local.localModel.rutine.RecentRoutineEntity
import kotlinx.coroutines.flow.Flow

class RecentRoutineLocalDataSource(private val recentRoutineDao: RecentRoutineDao) {
    fun getRecentRoutines(userId: Int): Flow<List<RecentRoutineEntity>> {
        return recentRoutineDao.getRecentRoutines(userId)
    }

    suspend fun addRecentRoutine(userId: Int, routineId: Int, name: String, image: String?, creatorId: Int?, groupId: Int?) {
        // Primero eliminamos para moverlo al principio (evitar duplicados)
        recentRoutineDao.deleteExisting(userId, routineId)
        
        val recent = RecentRoutineEntity(
            userId = userId,
            routineId = routineId,
            routineName = name,
            routineImage = image,
            routineCreatorId = creatorId,
            routineGroupId = groupId,
            lastVisited = System.currentTimeMillis()
        )
        recentRoutineDao.insertRecent(recent)
    }

    suspend fun deleteRecentByRoutineId(routineId: Int) {
        recentRoutineDao.deleteByRoutineId(routineId)
    }
}
