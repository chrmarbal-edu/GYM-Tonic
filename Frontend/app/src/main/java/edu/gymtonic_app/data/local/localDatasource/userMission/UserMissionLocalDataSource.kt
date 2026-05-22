package edu.gymtonic_app.data.local.localDatasource.userMission

import edu.gymtonic_app.data.local.dao.UserMissionDao
import edu.gymtonic_app.data.local.localModel.userMission.UserMissionEntity

class UserMissionLocalDataSource(
    private val userMissionDao: UserMissionDao
) {
    suspend fun getUserMissions(userId: Int): List<UserMissionEntity> {
        return userMissionDao.getUserMissions(userId)
    }

    suspend fun insertUserMissions(userMissions: List<UserMissionEntity>) {
        userMissionDao.insertUserMissions(userMissions)
    }
}
