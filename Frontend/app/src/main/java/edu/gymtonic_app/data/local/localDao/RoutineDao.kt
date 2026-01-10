package edu.gymtonic_app.data.local.localDao

import androidx.room.*
import edu.gymtonic_app.data.local.model.RoutineEntity

@Dao
interface RoutineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<RoutineEntity>)

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Query("SELECT * FROM routines WHERE routine_id = :id")
    suspend fun getRoutineById(id: Int): RoutineEntity?

    @Query("SELECT * FROM routines")
    suspend fun getAllRoutines(): List<RoutineEntity>
}