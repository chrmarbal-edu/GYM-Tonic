package edu.gymtonic_app.data.local.localModel.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_x_user")
data class GroupUserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_x_group_id")
    val userXGroupId: Int = 0,

    @ColumnInfo(name = "user_x_group_userid")
    val userXGroupUserId: Int,

    @ColumnInfo(name = "user_x_group_groupid")
    val userXGroupGroupId: Int,

    @ColumnInfo(name = "user_x_group_range")
    val userXGroupRange: Int
)