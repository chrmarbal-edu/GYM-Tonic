package edu.gymtonic_app.data.local.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.model.RoutineEntity

@Dao
interface RoutineDao {

    // -------------------------
    // READ (FLOW)
    // -------------------------

    // Todas las rutinas
    @Query("SELECT * FROM routines ORDER BY routine_name ASC")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    // Rutina por ID
    @Query("SELECT * FROM routines WHERE routine_id = :id LIMIT 1")
    fun getRoutineById(id: Int): Flow<RoutineEntity?>

    @Query("SELECT * FROM routines WHERE routine_id = :id LIMIT 1")
    suspend fun getRoutineByIdOnce(id: Int): RoutineEntity?

    // Buscar por nombre
    @Query("""
        SELECT * FROM routines
        WHERE routine_name LIKE '%' || :query || '%'
        ORDER BY routine_name ASC
    """)
    fun searchRoutines(query: String): Flow<List<RoutineEntity>>

    // -------------------------
    // WRITE
    // -------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoutine(routine: RoutineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoutines(routines: List<RoutineEntity>): List<Long>

    @Update
    suspend fun updateRoutine(routine: RoutineEntity): Int

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity): Int

    @Query("DELETE FROM routines WHERE routine_id = :id")
    suspend fun deleteRoutineById(id: Int): Int

    @Query("DELETE FROM routines")
    suspend fun deleteAllRoutines(): Int

    // -------------------------
    // UTILS
    // -------------------------

    @Query("SELECT COUNT(*) FROM routines")
    fun countRoutines(): Flow<Int>

    // Saber si existe una rutina con ese nombre
    @Query("""
        SELECT COUNT(*) FROM routines
        WHERE routine_name = :name
    """)
    suspend fun existsByName(name: String): Int
}