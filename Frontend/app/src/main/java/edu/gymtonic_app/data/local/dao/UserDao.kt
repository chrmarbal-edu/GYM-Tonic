package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.model.UserEntity

@Dao
interface UserDao {

    // -------------------------
    // LECTURAS (FLOW)
    // -------------------------

    @Query("SELECT * FROM users ORDER BY user_name ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE user_id = :id LIMIT 1")
    fun getUserById(id: Int): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE user_username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE user_email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    // Login simple (username + password ya “comparada” en BD)
    // Si tu password está hasheada, normalmente compararías el hash.
    @Query("""
        SELECT * FROM users 
        WHERE user_username = :username AND user_password = :password
        LIMIT 1
    """)
    suspend fun login(username: String, password: String): UserEntity?

    // Búsqueda por nombre o username
    @Query("""
        SELECT * FROM users 
        WHERE user_name LIKE '%' || :query || '%'
           OR user_username LIKE '%' || :query || '%'
        ORDER BY user_name ASC
    """)
    fun searchUsers(query: String): Flow<List<UserEntity>>

    // -------------------------
    // ESCRITURAS
    // -------------------------

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUsers(users: List<UserEntity>): List<Long>

    // Si quieres “guardar o reemplazar” (tipo upsert manual)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity): Int

    @Delete
    suspend fun deleteUser(user: UserEntity): Int

    @Query("DELETE FROM users WHERE user_id = :id")
    suspend fun deleteUserById(id: Int): Int

    @Query("DELETE FROM users")
    suspend fun deleteAll(): Int

    // -------------------------
    // UTILIDADES
    // -------------------------

    @Query("SELECT COUNT(*) FROM users")
    fun countUsers(): Flow<Int>

    @Query("UPDATE users SET user_points = :points WHERE user_id = :userId")
    suspend fun updateUserPoints(userId: Int, points: Int?): Int

    @Query("UPDATE users SET user_role = :role WHERE user_id = :userId")
    suspend fun updateUserRole(userId: Int, role: Int): Int
}
