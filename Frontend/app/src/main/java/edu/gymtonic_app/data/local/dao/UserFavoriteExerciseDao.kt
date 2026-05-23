package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.local.localModel.user.UserFavoriteExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserFavoriteExerciseDao {
    @Query("""
        SELECT e.* FROM exercises e
        INNER JOIN user_favorite_exercises f ON e.exercise_id = f.exerciseId
        WHERE f.userId = :userId
    """)
    fun getFavoriteExercises(userId: Int): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: UserFavoriteExerciseEntity)

    @Delete
    suspend fun deleteFavorite(favorite: UserFavoriteExerciseEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM user_favorite_exercises WHERE userId = :userId AND exerciseId = :exerciseId)")
    suspend fun isFavorite(userId: Int, exerciseId: Int): Boolean
}
