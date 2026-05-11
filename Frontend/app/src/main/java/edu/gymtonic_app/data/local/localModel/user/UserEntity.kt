package edu.gymtonic_app.data.local.localModel.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    val userId: Int = 0,

    @ColumnInfo(name = "user_username")
    val userUsername: String,

    @ColumnInfo(name = "user_name")
    val userName: String,

    @ColumnInfo(name = "user_password")
    val userPassword: String,

    @ColumnInfo(name = "user_birthdate")
    val userBirthdate: String,

    @ColumnInfo(name = "user_email")
    val userEmail: String,

    @ColumnInfo(name = "user_height")
    val userHeight: Double,

    @ColumnInfo(name = "user_weight")
    val userWeight: Double,

    @ColumnInfo(name = "user_objective")
    val userObjective: Int,

    @ColumnInfo(name = "user_points")
    val userPoints: Int? = null,

    @ColumnInfo(name = "user_role")
    val userRole: Int
)