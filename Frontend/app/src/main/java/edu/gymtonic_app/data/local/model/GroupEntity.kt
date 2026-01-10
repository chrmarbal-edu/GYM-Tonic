package edu.gymtonic_app.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grupos")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true)
    val group_id: Int = 0,

    val group_name: String
)