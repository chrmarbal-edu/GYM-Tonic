package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.local.localModel.rutine.RoutineEntity

@Dao
interface RoutineDao {

    @Query("SELECT * FROM routines WHERE owner_user_id = :ownerUserId ORDER BY routine_name ASC")
    fun getRoutinesByOwner(ownerUserId: Int): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE routine_id = :id AND owner_user_id = :ownerUserId LIMIT 1")
    suspend fun getRoutineByIdForOwner(id: Int, ownerUserId: Int): RoutineEntity?

    @Query("""
        SELECT DISTINCT r.* FROM routines r
        LEFT JOIN routine_x_exercise rxe ON r.routine_id = rxe.routine_x_exercise_routineid
        WHERE r.routine_id = :id AND r.owner_user_id = :ownerUserId
        LIMIT 1
    """)
    suspend fun getRoutineWithExercisesByIdForOwner(id: Int, ownerUserId: Int): RoutineEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM routines WHERE routine_name = :name AND owner_user_id = :ownerUserId)")
    suspend fun existsByNameForOwner(name: String, ownerUserId: Int): Boolean

    @Query("SELECT * FROM routines")
    suspend fun getAllRoutines(): List<RoutineEntity>

    @Query("SELECT * FROM routines WHERE routine_id = :id LIMIT 1")
    suspend fun getRoutineById(id: Int): RoutineEntity?

    @Query("SELECT * FROM routines ORDER BY last_visited DESC LIMIT 3")
    fun getRecentRoutines(): Flow<List<RoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<RoutineEntity>)

    @Update
    suspend fun updateRoutine(routine: RoutineEntity): Int

    @Query("DELETE FROM routines WHERE routine_id = :id AND owner_user_id = :ownerUserId")
    suspend fun deleteRoutineByIdForOwner(id: Int, ownerUserId: Int): Int
}
