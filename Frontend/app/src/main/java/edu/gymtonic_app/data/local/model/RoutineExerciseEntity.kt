package edu.gymtonic_app.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_x_exercise")
data class RoutineExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val routine_x_exercise_id: Int = 0,

    val routine_x_exercise_routineid: Int,
    val routine_x_exercise_exerciseid: Int
)