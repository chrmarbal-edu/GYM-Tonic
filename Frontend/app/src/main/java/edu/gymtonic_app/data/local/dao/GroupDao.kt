package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.model.GroupEntity

@Dao
interface GroupDao {

    // -------------------------
    // READ (FLOW)
    // -------------------------

    // Todos los grupos
    @Query("SELECT * FROM grupos ORDER BY group_name ASC")
    fun getAllGroups(): Flow<List<GroupEntity>>

    // Grupo por ID
    @Query("SELECT * FROM grupos WHERE group_id = :id LIMIT 1")
    fun getGroupById(id: Int): Flow<GroupEntity?>

    @Query("SELECT * FROM grupos WHERE group_id = :id LIMIT 1")
    suspend fun getGroupByIdOnce(id: Int): GroupEntity?

    // Buscar por nombre
    @Query("""
        SELECT * FROM grupos
        WHERE group_name LIKE '%' || :query || '%'
        ORDER BY group_name ASC
    """)
    fun searchGroups(query: String): Flow<List<GroupEntity>>

    // -------------------------
    // WRITE
    // -------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGroup(group: GroupEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGroups(groups: List<GroupEntity>): List<Long>

    @Update
    suspend fun updateGroup(group: GroupEntity): Int

    @Delete
    suspend fun deleteGroup(group: GroupEntity): Int

    @Query("DELETE FROM grupos WHERE group_id = :id")
    suspend fun deleteGroupById(id: Int): Int

    @Query("DELETE FROM grupos")
    suspend fun deleteAllGroups(): Int

    // -------------------------
    // UTILS
    // -------------------------

    @Query("SELECT COUNT(*) FROM grupos")
    fun countGroups(): Flow<Int>

    // Saber si existe un grupo con ese nombre
    @Query("""
        SELECT COUNT(*) FROM grupos
        WHERE group_name = :name
    """)
    suspend fun existsByName(name: String): Int
}