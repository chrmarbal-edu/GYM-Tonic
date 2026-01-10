package edu.gymtonic_app.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_x_routine")
data class UserRoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val user_x_routine_id: Int = 0,

    val user_x_routine_userid: Int,
    val user_x_routine_routineid: Int
)