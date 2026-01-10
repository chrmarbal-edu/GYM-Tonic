package edu.gymtonic_app.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_x_user")
data class GroupUserEntity(
    @PrimaryKey(autoGenerate = true)
    val group_x_user_id: Int = 0,

    val group_x_user_groupid: Int,
    val group_x_user_userid: Int,
    val group_x_user_range: Int
)