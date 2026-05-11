package edu.gymtonic_app.data.local.localModel.userMission

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_x_mission")
data class UserMissionEntity(
    @PrimaryKey(autoGenerate = true)
    val user_x_mission_id: Int = 0,

    val user_x_mission_userid: Int,
    val user_x_mission_missionid: Int,
    val user_x_mission_expiration: String,
    val user_x_mission_completed: Boolean = false,
    val user_x_mission_progress: Int = 0
)