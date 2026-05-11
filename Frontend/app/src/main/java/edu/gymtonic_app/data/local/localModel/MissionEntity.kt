package edu.gymtonic_app.data.local.localModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mission_id")
    val missionId: Int = 0,

    @ColumnInfo(name = "mission_name")
    val missionName: String,

    @ColumnInfo(name = "mission_type")
    val missionType: Int,

    @ColumnInfo(name = "mission_points")
    val missionPoints: Int,

    @ColumnInfo(name = "mission_objective")
    val missionObjective: Int
)