package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.model.GroupUserEntity

@Dao
interface GroupUserDao {

    // -------------------------
    // INSERT
    // -------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupUser(relation: GroupUserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupUsers(relations: List<GroupUserEntity>): List<Long>

    // -------------------------
    // DELETE
    // -------------------------

    @Delete
    suspend fun deleteGroupUser(relation: GroupUserEntity): Int

    // Quitar usuario de grupo
    @Query("""
        DELETE FROM group_x_user
        WHERE group_x_user_groupid = :groupId
          AND group_x_user_userid = :userId
    """)
    suspend fun removeUserFromGroup(groupId: Int, userId: Int): Int

    // Quitar TODOS los usuarios de un grupo
    @Query("""
        DELETE FROM group_x_user
        WHERE group_x_user_groupid = :groupId
    """)
    suspend fun removeAllUsersFromGroup(groupId: Int): Int

    // Quitar usuario de TODOS los grupos
    @Query("""
        DELETE FROM group_x_user
        WHERE group_x_user_userid = :userId
    """)
    suspend fun removeUserFromAllGroups(userId: Int): Int

    // -------------------------
    // UPDATE
    // -------------------------

    // Cambiar rango dentro del grupo
    @Query("""
        UPDATE group_x_user
        SET group_x_user_range = :range
        WHERE group_x_user_groupid = :groupId
          AND group_x_user_userid = :userId
    """)
    suspend fun updateUserRange(
        groupId: Int,
        userId: Int,
        range: Int
    ): Int

    @Update
    suspend fun updateRelation(relation: GroupUserEntity): Int

    // -------------------------
    // QUERIES
    // -------------------------

    // Todas las relaciones
    @Query("SELECT * FROM group_x_user")
    fun getAllRelations(): Flow<List<GroupUserEntity>>

    // Miembros de un grupo
    @Query("""
        SELECT * FROM group_x_user
        WHERE group_x_user_groupid = :groupId
        ORDER BY group_x_user_range DESC
    """)
    fun getUsersOfGroup(groupId: Int): Flow<List<GroupUserEntity>>

    // Grupos de un usuario
    @Query("""
        SELECT * FROM group_x_user
        WHERE group_x_user_userid = :userId
    """)
    fun getGroupsOfUser(userId: Int): Flow<List<GroupUserEntity>>

    // Obtener relación concreta
    @Query("""
        SELECT * FROM group_x_user
        WHERE group_x_user_groupid = :groupId
          AND group_x_user_userid = :userId
        LIMIT 1
    """)
    suspend fun getRelation(
        groupId: Int,
        userId: Int
    ): GroupUserEntity?

    // -------------------------
    // UTILS
    // -------------------------

    // Saber si un usuario pertenece a un grupo
    @Query("""
        SELECT COUNT(*) FROM group_x_user
        WHERE group_x_user_groupid = :groupId
          AND group_x_user_userid = :userId
    """)
    suspend fun isUserInGroup(groupId: Int, userId: Int): Int

    // Contar miembros de un grupo
    @Query("""
        SELECT COUNT(*) FROM group_x_user
        WHERE group_x_user_groupid = :groupId
    """)
    fun countUsersInGroup(groupId: Int): Flow<Int>
}
