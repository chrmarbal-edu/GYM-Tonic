package edu.gymtonic_app.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "frequest")
data class FrequestEntity(
    @PrimaryKey(autoGenerate = true)
    val frequest_id: Int = 0,

    val frequest_sender: Int,
    val frequest_receiver: Int,
    val frequest_status: Int
)