package edu.gymtonic_app.data.local.localModel.rutine

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val routine_id: Int = 0,
    val routine_name: String,
    val routine_image: String? = null
)
