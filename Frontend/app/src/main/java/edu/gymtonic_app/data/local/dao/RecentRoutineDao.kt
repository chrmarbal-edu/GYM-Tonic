package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.local.localModel.rutine.RecentRoutineEntity

@Dao
interface RecentRoutineDao {
    @Query("SELECT * FROM recent_routines WHERE userId = :userId ORDER BY lastVisited DESC LIMIT 10")
    fun getRecentRoutines(userId: Int): Flow<List<RecentRoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecent(recent: RecentRoutineEntity)

    @Query("DELETE FROM recent_routines WHERE userId = :userId AND routineId = :routineId")
    suspend fun deleteExisting(userId: Int, routineId: Int)

    @Query("DELETE FROM recent_routines WHERE routineId = :routineId")
    suspend fun deleteByRoutineId(routineId: Int)

    @Query("DELETE FROM recent_routines WHERE userId = :userId")
    suspend fun clearHistory(userId: Int)
}
