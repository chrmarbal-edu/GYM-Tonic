package edu.gymtonic_app.data.local.localModel.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grupos")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "group_id")
    val groupId: Int = 0,

    @ColumnInfo(name = "group_name")
    val groupName: String,

    @ColumnInfo(name = "group_description")
    val groupDescription: String? = null,

    @ColumnInfo(name = "group_image")
    val groupImage: String? = null,

    @ColumnInfo(name = "group_points")
    val groupPoints: Int = 0,

    @ColumnInfo(name = "group_creator_id")
    val groupCreatorId: Int? = null
)