package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    //Obtener favoritas (Flow)
    @Query("SELECT * FROM exercises")
    fun getFavoriteExercise(): Flow<List<ExerciseEntity>>

    //Obtener favorita por id (suspend)
    @Query("SELECT * FROM exercises WHERE exercise_id = :exerciseId")
    suspend fun getFavExerciseById(exerciseId: Int): ExerciseEntity?

    //Insertar (suspend)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    //Borrar (suspend), devuelve el id del elemento eliminado
    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity): Int
}
