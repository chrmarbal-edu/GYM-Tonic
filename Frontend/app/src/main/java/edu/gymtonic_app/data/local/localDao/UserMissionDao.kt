package edu.gymtonic_app.data.local.localDao

import androidx.room.*
import edu.gymtonic_app.data.local.model.UserMissionEntity

@Dao
interface UserMissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserMission(userMission: UserMissionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserMissions(userMissions: List<UserMissionEntity>)

    @Update
    suspend fun updateUserMission(userMission: UserMissionEntity)

    @Delete
    suspend fun deleteUserMission(userMission: UserMissionEntity)

    @Query("SELECT * FROM user_x_mission WHERE user_x_mission_id = :id")
    suspend fun getById(id: Int): UserMissionEntity?

    @Query("SELECT * FROM user_x_mission")
    suspend fun getAll(): List<UserMissionEntity>

    @Query("SELECT * FROM user_x_mission WHERE user_x_mission_userid = :userId")
    suspend fun getByUserId(userId: Int): List<UserMissionEntity>
}