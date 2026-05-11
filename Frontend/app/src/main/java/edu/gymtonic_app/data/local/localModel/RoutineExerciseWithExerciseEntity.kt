package edu.gymtonic_app.data.local.localModel

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class RoutineExerciseWithExerciseEntity(
    @Embedded val exercise: ExerciseEntity,
    @ColumnInfo(name = "routine_x_exercise_reps")
    val reps: String
)