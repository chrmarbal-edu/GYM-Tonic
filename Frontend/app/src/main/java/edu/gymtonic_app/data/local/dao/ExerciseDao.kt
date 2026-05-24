package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercises")
    suspend fun getAllExercises(): List<ExerciseEntity>

    @Query("SELECT * FROM exercises WHERE exercise_id = :exerciseId")
    suspend fun getExerciseById(exerciseId: Int): ExerciseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    //Insertar (suspend) — IGNORE para no borrar favoritos por CASCADE
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Query("UPDATE exercises SET exercise_image = :image, exercise_video = :video WHERE exercise_id = :exerciseId")
    suspend fun updateExerciseMedia(exerciseId: Int, image: String?, video: String?)

    //Borrar (suspend), devuelve el id del elemento eliminado
    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity): Int

    @Query("DELETE FROM exercises WHERE exercise_id = :id")
    suspend fun deleteExerciseById(id: Int): Int
}
