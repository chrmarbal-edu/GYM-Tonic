package edu.gymtonic_app.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.model.UserRoutineEntity

@Dao
interface UserRoutineDao {

    // -------------------------
    // INSERTS
    // -------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRoutine(relation: UserRoutineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRoutines(relations: List<UserRoutineEntity>): List<Long>

    // -------------------------
    // DELETE
    // -------------------------

    @Delete
    suspend fun deleteUserRoutine(relation: UserRoutineEntity): Int

    // Quitar una rutina concreta a un usuario
    @Query("""
        DELETE FROM user_x_routine
        WHERE user_x_routine_userid = :userId
          AND user_x_routine_routineid = :routineId
    """)
    suspend fun deleteUserRoutine(userId: Int, routineId: Int): Int

    // Quitar TODAS las rutinas de un usuario
    @Query("""
        DELETE FROM user_x_routine
        WHERE user_x_routine_userid = :userId
    """)
    suspend fun deleteAllRoutinesFromUser(userId: Int): Int

    // Quitar TODOS los usuarios de una rutina
    @Query("""
        DELETE FROM user_x_routine
        WHERE user_x_routine_routineid = :routineId
    """)
    suspend fun deleteAllUsersFromRoutine(routineId: Int): Int

    // -------------------------
    // QUERIES
    // -------------------------

    // Obtener TODAS las relaciones
    @Query("SELECT * FROM user_x_routine")
    fun getAllRelations(): Flow<List<UserRoutineEntity>>

    // Rutinas asignadas a un usuario (solo IDs de relación)
    @Query("""
        SELECT * FROM user_x_routine
        WHERE user_x_routine_userid = :userId
    """)
    fun getRoutinesOfUser(userId: Int): Flow<List<UserRoutineEntity>>

    // Usuarios asignados a una rutina
    @Query("""
        SELECT * FROM user_x_routine
        WHERE user_x_routine_routineid = :routineId
    """)
    fun getUsersOfRoutine(routineId: Int): Flow<List<UserRoutineEntity>>

    // -------------------------
    // UTILIDADES
    // -------------------------

    // Comprobar si existe la relación (evitar duplicados manualmente)
    @Query("""
        SELECT COUNT(*) FROM user_x_routine
        WHERE user_x_routine_userid = :userId
          AND user_x_routine_routineid = :routineId
    """)
    suspend fun relationExists(userId: Int, routineId: Int): Int

    // Contar rutinas de un usuario
    @Query("""
        SELECT COUNT(*) FROM user_x_routine
        WHERE user_x_routine_userid = :userId
    """)
    fun countRoutinesOfUser(userId: Int): Flow<Int>
}
