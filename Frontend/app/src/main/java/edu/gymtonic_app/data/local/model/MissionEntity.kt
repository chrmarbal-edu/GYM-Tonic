package edu.gymtonic_app.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey(autoGenerate = true)
    val mission_id: Int = 0,

    val mission_name: String,
    val mission_type: Int,
    val mission_points: Int,
    val mission_objetive: Int
)