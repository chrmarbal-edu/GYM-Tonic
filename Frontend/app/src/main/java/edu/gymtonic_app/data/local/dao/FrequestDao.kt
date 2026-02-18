package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.model.FrequestEntity

@Dao
interface FrequestDao {

    // -------------------------
    // INSERT
    // -------------------------

    // Enviar solicitud
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFrequest(frequest: FrequestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFrequests(frequests: List<FrequestEntity>): List<Long>

    // -------------------------
    // DELETE
    // -------------------------

    @Delete
    suspend fun deleteFrequest(frequest: FrequestEntity): Int

    // Eliminar solicitud concreta
    @Query("""
        DELETE FROM frequest
        WHERE frequest_sender = :senderId
          AND frequest_receiver = :receiverId
    """)
    suspend fun deleteFrequest(senderId: Int, receiverId: Int): Int

    // -------------------------
    // UPDATE (estado)
    // -------------------------

    // Cambiar estado (0 = pendiente, 1 = aceptada, 2 = rechazada)
    @Query("""
        UPDATE frequest
        SET frequest_status = :status
        WHERE frequest_sender = :senderId
          AND frequest_receiver = :receiverId
    """)
    suspend fun updateStatus(
        senderId: Int,
        receiverId: Int,
        status: Int
    ): Int

    // Aceptar solicitud
    @Query("""
        UPDATE frequest
        SET frequest_status = 1
        WHERE frequest_sender = :senderId
          AND frequest_receiver = :receiverId
    """)
    suspend fun acceptRequest(senderId: Int, receiverId: Int): Int

    // Rechazar solicitud
    @Query("""
        UPDATE frequest
        SET frequest_status = 2
        WHERE frequest_sender = :senderId
          AND frequest_receiver = :receiverId
    """)
    suspend fun rejectRequest(senderId: Int, receiverId: Int): Int

    // -------------------------
    // QUERIES
    // -------------------------

    // Todas las solicitudes
    @Query("SELECT * FROM frequest")
    fun getAllFrequests(): Flow<List<FrequestEntity>>

    // Solicitudes recibidas por un usuario
    @Query("""
        SELECT * FROM frequest
        WHERE frequest_receiver = :userId
        ORDER BY frequest_id DESC
    """)
    fun getReceivedRequests(userId: Int): Flow<List<FrequestEntity>>

    // Solicitudes enviadas por un usuario
    @Query("""
        SELECT * FROM frequest
        WHERE frequest_sender = :userId
        ORDER BY frequest_id DESC
    """)
    fun getSentRequests(userId: Int): Flow<List<FrequestEntity>>

    // Solicitudes pendientes recibidas
    @Query("""
        SELECT * FROM frequest
        WHERE frequest_receiver = :userId
          AND frequest_status = 0
    """)
    fun getPendingReceivedRequests(userId: Int): Flow<List<FrequestEntity>>

    // Solicitudes pendientes enviadas
    @Query("""
        SELECT * FROM frequest
        WHERE frequest_sender = :userId
          AND frequest_status = 0
    """)
    fun getPendingSentRequests(userId: Int): Flow<List<FrequestEntity>>

    // -------------------------
    // UTILIDADES
    // -------------------------

    // Comprobar si ya existe solicitud entre 2 usuarios
    @Query("""
        SELECT COUNT(*) FROM frequest
        WHERE (frequest_sender = :user1 AND frequest_receiver = :user2)
           OR (frequest_sender = :user2 AND frequest_receiver = :user1)
    """)
    suspend fun requestExists(user1: Int, user2: Int): Int

    // Obtener solicitud concreta
    @Query("""
        SELECT * FROM frequest
        WHERE frequest_sender = :senderId
          AND frequest_receiver = :receiverId
        LIMIT 1
    """)
    suspend fun getRequest(
        senderId: Int,
        receiverId: Int
    ): FrequestEntity?
}