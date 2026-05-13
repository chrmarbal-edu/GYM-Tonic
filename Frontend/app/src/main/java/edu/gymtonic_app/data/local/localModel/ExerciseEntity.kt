package edu.gymtonic_app.data.local.localModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey
    val exercise_id: Int,
    val exercise_name: String,
    val exercise_description: String,
    val exercise_type: Int,
    val exercise_video: String? = null,
    val exercise_image: String? = null,
    val is_favorite: Boolean = false
)
