package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.model.MissionEntity

@Dao
interface MissionDao {

    // -------------------------
    // READ (FLOW)
    // -------------------------

    @Query("SELECT * FROM missions ORDER BY mission_name ASC")
    fun getAllMissions(): Flow<List<MissionEntity>>

    @Query("SELECT * FROM missions WHERE mission_id = :id LIMIT 1")
    fun getMissionById(id: Int): Flow<MissionEntity?>

    @Query("SELECT * FROM missions WHERE mission_id = :id LIMIT 1")
    suspend fun getMissionByIdOnce(id: Int): MissionEntity?

    @Query("""
        SELECT * FROM missions
        WHERE mission_type = :type
        ORDER BY mission_points DESC, mission_name ASC
    """)
    fun getMissionsByType(type: Int): Flow<List<MissionEntity>>

    // Búsqueda por nombre
    @Query("""
        SELECT * FROM missions
        WHERE mission_name LIKE '%' || :query || '%'
        ORDER BY mission_name ASC
    """)
    fun searchMissions(query: String): Flow<List<MissionEntity>>

    // -------------------------
    // WRITE
    // -------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMission(mission: MissionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMissions(missions: List<MissionEntity>): List<Long>

    @Update
    suspend fun updateMission(mission: MissionEntity): Int

    @Delete
    suspend fun deleteMission(mission: MissionEntity): Int

    @Query("DELETE FROM missions WHERE mission_id = :id")
    suspend fun deleteMissionById(id: Int): Int

    @Query("DELETE FROM missions")
    suspend fun deleteAllMissions(): Int

    // -------------------------
    // UTILS
    // -------------------------

    @Query("SELECT COUNT(*) FROM missions")
    fun countMissions(): Flow<Int>

    // Si quieres comprobar duplicados por nombre (opcional)
    @Query("SELECT COUNT(*) FROM missions WHERE mission_name = :name")
    suspend fun existsByName(name: String): Int
}