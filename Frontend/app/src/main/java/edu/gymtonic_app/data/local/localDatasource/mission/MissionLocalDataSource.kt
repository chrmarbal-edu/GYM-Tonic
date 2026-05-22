package edu.gymtonic_app.data.local.localDatasource.mission

import edu.gymtonic_app.data.local.dao.MissionDao
import edu.gymtonic_app.data.local.localModel.MissionEntity
import kotlinx.coroutines.flow.first

class MissionLocalDataSource(
    private val missionDao: MissionDao
) {
    suspend fun getAllMissions(): List<MissionEntity> {
        return missionDao.getAllMissions().first()
    }

    suspend fun getMissionById(id: Int): MissionEntity? {
        return missionDao.getMissionByIdOnce(id)
    }

    suspend fun insertMissions(missions: List<MissionEntity>) {
        missionDao.upsertMissions(missions)
    }
}
