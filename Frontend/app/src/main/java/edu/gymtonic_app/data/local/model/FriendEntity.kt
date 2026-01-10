package edu.gymtonic_app.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey(autoGenerate = true)
    val friend_id: Int = 0,

    val friend_userid1: Int,
    val friend_userid2: Int
)