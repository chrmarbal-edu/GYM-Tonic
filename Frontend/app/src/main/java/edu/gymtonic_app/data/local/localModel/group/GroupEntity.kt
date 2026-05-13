package edu.gymtonic_app.data.local.localModel.group

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grupos")
data class GroupEntity(
    @PrimaryKey
    val group_id: Int,
    val group_name: String,
    val group_description: String,
    val group_image: String,
    val group_points: Int,
    val group_creator_id: Int
)
