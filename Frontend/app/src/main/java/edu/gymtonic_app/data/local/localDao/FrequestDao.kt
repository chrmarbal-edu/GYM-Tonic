package edu.gymtonic_app.data.local.localDao

import androidx.room.*
import edu.gymtonic_app.data.local.model.FrequestEntity

@Dao
interface FrequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFrequest(frequest: FrequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFrequests(frequests: List<FrequestEntity>)

    @Update
    suspend fun updateFrequest(frequest: FrequestEntity)

    @Delete
    suspend fun deleteFrequest(frequest: FrequestEntity)

    @Query("SELECT * FROM frequest WHERE frequest_id = :id")
    suspend fun getById(id: Int): FrequestEntity?

    @Query("SELECT * FROM frequest")
    suspend fun getAll(): List<FrequestEntity>

    @Query("SELECT * FROM frequest WHERE frequest_sender = :userId OR frequest_receiver = :userId")
    suspend fun getByUserId(userId: Int): List<FrequestEntity>
}