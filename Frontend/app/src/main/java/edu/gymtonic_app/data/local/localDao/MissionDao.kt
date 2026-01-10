package edu.gymtonic_app.data.local.localDao

import androidx.room.*
import edu.gymtonic_app.data.local.model.MissionEntity

@Dao
interface MissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: MissionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<MissionEntity>)

    @Update
    suspend fun updateMission(mission: MissionEntity)

    @Delete
    suspend fun deleteMission(mission: MissionEntity)

    @Query("SELECT * FROM missions WHERE mission_id = :id")
    suspend fun getMissionById(id: Int): MissionEntity?

    @Query("SELECT * FROM missions")
    suspend fun getAllMissions(): List<MissionEntity>
}