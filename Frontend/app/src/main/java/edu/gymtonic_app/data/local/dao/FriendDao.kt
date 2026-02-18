package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.model.FriendEntity

@Dao
interface FriendDao {

    // -------------------------
    // INSERT
    // -------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: FriendEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<FriendEntity>): List<Long>

    // -------------------------
    // DELETE
    // -------------------------

    @Delete
    suspend fun deleteFriend(friend: FriendEntity): Int

    // Borra la amistad entre dos usuarios (en cualquier orden)
    @Query("""
        DELETE FROM friends
        WHERE (friend_userid1 = :user1 AND friend_userid2 = :user2)
           OR (friend_userid1 = :user2 AND friend_userid2 = :user1)
    """)
    suspend fun deleteFriendship(user1: Int, user2: Int): Int

    // Borra todas las amistades de un usuario
    @Query("""
        DELETE FROM friends
        WHERE friend_userid1 = :userId OR friend_userid2 = :userId
    """)
    suspend fun deleteAllFriendshipsOfUser(userId: Int): Int

    // -------------------------
    // QUERIES
    // -------------------------

    @Query("SELECT * FROM friends ORDER BY friend_id DESC")
    fun getAllFriendsRelations(): Flow<List<FriendEntity>>

    // Obtener la relación concreta si existe (en cualquier orden)
    @Query("""
        SELECT * FROM friends
        WHERE (friend_userid1 = :user1 AND friend_userid2 = :user2)
           OR (friend_userid1 = :user2 AND friend_userid2 = :user1)
        LIMIT 1
    """)
    suspend fun getFriendship(user1: Int, user2: Int): FriendEntity?

    // Comprobar si son amigos (devuelve 0 o 1+)
    @Query("""
        SELECT COUNT(*) FROM friends
        WHERE (friend_userid1 = :user1 AND friend_userid2 = :user2)
           OR (friend_userid1 = :user2 AND friend_userid2 = :user1)
    """)
    suspend fun areFriends(user1: Int, user2: Int): Int

    // Obtener IDs de amigos de un usuario (devuelve el "otro" id)
    @Query("""
        SELECT 
            CASE 
                WHEN friend_userid1 = :userId THEN friend_userid2
                ELSE friend_userid1
            END AS friendUserId
        FROM friends
        WHERE friend_userid1 = :userId OR friend_userid2 = :userId
        ORDER BY friend_id DESC
    """)
    fun getFriendIdsOfUser(userId: Int): Flow<List<Int>>

    // Contar amigos
    @Query("""
        SELECT COUNT(*) FROM friends
        WHERE friend_userid1 = :userId OR friend_userid2 = :userId
    """)
    fun countFriendsOfUser(userId: Int): Flow<Int>
}