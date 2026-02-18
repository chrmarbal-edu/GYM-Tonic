package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.model.ExerciseEntity

@Dao
interface ExerciseDao {

    // -------------------------
    // READ (FLOW)
    // -------------------------

    @Query("SELECT * FROM exercises ORDER BY exercise_name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE exercise_id = :id LIMIT 1")
    fun getExerciseById(id: Int): Flow<ExerciseEntity?>

    @Query("SELECT * FROM exercises WHERE exercise_id = :id LIMIT 1")
    suspend fun getExerciseByIdOnce(id: Int): ExerciseEntity?

    @Query("""
        SELECT * FROM exercises
        WHERE exercise_type = :type
        ORDER BY exercise_name ASC
    """)
    fun getExercisesByType(type: Int): Flow<List<ExerciseEntity>>

    // Búsqueda por nombre o descripción
    @Query("""
        SELECT * FROM exercises
        WHERE exercise_name LIKE '%' || :query || '%'
           OR exercise_description LIKE '%' || :query || '%'
        ORDER BY exercise_name ASC
    """)
    fun searchExercises(query: String): Flow<List<ExerciseEntity>>

    // -------------------------
    // WRITE
    // -------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExercise(exercise: ExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExercises(exercises: List<ExerciseEntity>): List<Long>

    @Update
    suspend fun updateExercise(exercise: ExerciseEntity): Int

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity): Int

    @Query("DELETE FROM exercises WHERE exercise_id = :id")
    suspend fun deleteExerciseById(id: Int): Int

    @Query("DELETE FROM exercises")
    suspend fun deleteAllExercises(): Int

    // -------------------------
    // UTILS
    // -------------------------

    @Query("SELECT COUNT(*) FROM exercises")
    fun countExercises(): Flow<Int>

    // Para saber si existe un ejercicio con ese nombre (opcional)
    @Query("SELECT COUNT(*) FROM exercises WHERE exercise_name = :name")
    suspend fun existsByName(name: String): Int
}