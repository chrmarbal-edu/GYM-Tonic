package edu.gymtonic_app.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val exercise_id: Int = 0,

    val exercise_name: String,
    val exercise_description: String,
    val exercise_type: Int,
    val exercise_video: String? = null,
    val exercise_image: String? = null
)