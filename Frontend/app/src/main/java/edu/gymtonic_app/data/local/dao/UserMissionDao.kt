package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.gymtonic_app.data.local.localModel.userMission.UserMissionEntity

@Dao
interface UserMissionDao {
    @Query("SELECT * FROM user_x_mission WHERE user_x_mission_userid = :userId")
    suspend fun getUserMissions(userId: Int): List<UserMissionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserMissions(userMissions: List<UserMissionEntity>)

    @Query("DELETE FROM user_x_mission WHERE user_x_mission_userid = :userId")
    suspend fun deleteUserMissions(userId: Int)
}
