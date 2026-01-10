package edu.gymtonic_app.data.local.localDao

import androidx.room.*
import edu.gymtonic_app.data.local.model.UserRoutineEntity

@Dao
interface UserRoutineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRoutine(userRoutine: UserRoutineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRoutines(userRoutines: List<UserRoutineEntity>)

    @Update
    suspend fun updateUserRoutine(userRoutine: UserRoutineEntity)

    @Delete
    suspend fun deleteUserRoutine(userRoutine: UserRoutineEntity)

    @Query("SELECT * FROM user_x_routine WHERE user_x_routine_id = :id")
    suspend fun getById(id: Int): UserRoutineEntity?

    @Query("SELECT * FROM user_x_routine")
    suspend fun getAll(): List<UserRoutineEntity>

    @Query("SELECT * FROM user_x_routine WHERE user_x_routine_userid = :userId")
    suspend fun getByUserId(userId: Int): List<UserRoutineEntity>
}