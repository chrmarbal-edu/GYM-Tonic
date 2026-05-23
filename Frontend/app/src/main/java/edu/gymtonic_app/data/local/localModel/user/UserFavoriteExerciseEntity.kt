package edu.gymtonic_app.data.local.localModel.user

import androidx.room.Entity
import androidx.room.ForeignKey
import edu.gymtonic_app.data.local.localModel.ExerciseEntity

@Entity(
    tableName = "user_favorite_exercises",
    primaryKeys = ["userId", "exerciseId"],
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["exercise_id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserFavoriteExerciseEntity(
    val userId: Int,
    val exerciseId: Int
)
