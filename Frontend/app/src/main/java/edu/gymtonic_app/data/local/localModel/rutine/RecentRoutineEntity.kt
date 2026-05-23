package edu.gymtonic_app.data.local.localModel.rutine

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_routines")
data class RecentRoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val routineId: Int,
    val routineName: String,
    val routineImage: String? = null,
    val routineCreatorId: Int? = null,
    val routineGroupId: Int? = null,
    val lastVisited: Long = System.currentTimeMillis()
)
