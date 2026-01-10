package edu.gymtonic_app.data.local.localDao

import androidx.room.*
import edu.gymtonic_app.data.local.model.GroupUserEntity

@Dao
interface GroupUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupUser(groupUser: GroupUserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupUsers(groupUsers: List<GroupUserEntity>)

    @Update
    suspend fun updateGroupUser(groupUser: GroupUserEntity)

    @Delete
    suspend fun deleteGroupUser(groupUser: GroupUserEntity)

    @Query("SELECT * FROM group_x_user WHERE group_x_user_id = :id")
    suspend fun getById(id: Int): GroupUserEntity?

    @Query("SELECT * FROM group_x_user")
    suspend fun getAll(): List<GroupUserEntity>

    @Query("SELECT * FROM group_x_user WHERE group_x_user_userid = :userId")
    suspend fun getByUserId(userId: Int): List<GroupUserEntity>
}
