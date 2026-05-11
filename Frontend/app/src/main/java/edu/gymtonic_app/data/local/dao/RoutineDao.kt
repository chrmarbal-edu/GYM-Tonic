package edu.gymtonic_app.data.local.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.local.localModel.RoutineEntity

@Dao
interface RoutineDao {

    @Query("SELECT * FROM routines ORDER BY routine_name ASC")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE routine_id = :id LIMIT 1")
    suspend fun getRoutineById(id: Int): RoutineEntity?

    // Obtiene una rutina con sus ejercicios asociados
    @Query("""
        SELECT DISTINCT r.* FROM routines r
        LEFT JOIN routine_x_exercise rxe ON r.routine_id = rxe.routine_x_exercise_routineid
        WHERE r.routine_id = :id
        LIMIT 1
    """)
    suspend fun getRoutineWithExercisesById(id: Int): RoutineEntity?

    @Query("SELECT COUNT(*) FROM routines WHERE routine_name = :name")
    suspend fun existsByName(name: String): Boolean

    // WRITE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoutine(routine: RoutineEntity): Long

    @Update
    suspend fun updateRoutine(routine: RoutineEntity): Int

    @Query("DELETE FROM routines WHERE routine_id = :id")
    suspend fun deleteRoutineById(id: Int): Int
}