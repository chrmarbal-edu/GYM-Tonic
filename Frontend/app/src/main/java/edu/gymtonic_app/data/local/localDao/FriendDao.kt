package edu.gymtonic_app.data.local.localDao

import androidx.room.*
import edu.gymtonic_app.data.local.model.FriendEntity

@Dao
interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: FriendEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<FriendEntity>)

    @Update
    suspend fun updateFriend(friend: FriendEntity)

    @Delete
    suspend fun deleteFriend(friend: FriendEntity)

    @Query("SELECT * FROM friends WHERE friend_id = :id")
    suspend fun getById(id: Int): FriendEntity?

    @Query("SELECT * FROM friends")
    suspend fun getAll(): List<FriendEntity>

    @Query("SELECT * FROM friends WHERE friend_userid1 = :userId OR friend_userid2 = :userId")
    suspend fun getByUserId(userId: Int): List<FriendEntity>
}