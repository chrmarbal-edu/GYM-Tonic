package edu.gymtonic_app.data.local.localModel

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "routine_x_exercise",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["routine_id"],
            childColumns = ["routine_x_exercise_routineid"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["exercise_id"],
            childColumns = ["routine_x_exercise_exerciseid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("routine_x_exercise_routineid"),
        Index("routine_x_exercise_exerciseid")
    ]
)
data class RoutineExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val routine_x_exercise_id: Int = 0,
    val routine_x_exercise_routineid: Int,
    val routine_x_exercise_exerciseid: Int,
    val routine_x_exercise_reps: String
)