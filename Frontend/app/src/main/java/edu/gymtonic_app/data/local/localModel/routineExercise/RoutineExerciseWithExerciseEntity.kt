package edu.gymtonic_app.data.local.localModel.routineExercise

import androidx.room.ColumnInfo
import androidx.room.Embedded
import edu.gymtonic_app.data.local.localModel.ExerciseEntity

data class RoutineExerciseWithExerciseEntity(
    @Embedded val exercise: ExerciseEntity,
    @ColumnInfo(name = "routine_x_exercise_reps")
    val reps: String
)