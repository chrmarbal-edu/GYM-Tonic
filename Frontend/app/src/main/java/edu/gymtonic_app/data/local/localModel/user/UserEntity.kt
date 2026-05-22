package edu.gymtonic_app.data.local.localModel.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val user_id: Int,
    val user_username: String,
    val user_name: String,
    val user_birthdate: String,
    val user_email: String,
    val user_picture: String? = null,
    val user_height: Float,
    val user_weight: Float,
    val user_objective: Int,
    val user_points: Int,
    val user_role: Int
)
