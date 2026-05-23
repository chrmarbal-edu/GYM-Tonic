package edu.gymtonic_app.data.local.localModel.rutine

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val routine_id: Int = 0,
    val owner_user_id: Int,
    val routine_name: String,
    val routine_image: String? = null,
    val last_visited: Long = 0,
    val routine_creator_id: Int? = null,
    val routine_groupid: Int? = null
)
