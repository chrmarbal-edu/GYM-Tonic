package edu.gymtonic_app.data.remote.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("user_id")
    val id: Int,

    @SerializedName("user_username")
    val username: String,

    @SerializedName("user_name")
    val name: String,

    @SerializedName("user_password")
    val passwordHash: String,

    @SerializedName("user_birthdate")
    val birthdate: String,

    @SerializedName("user_email")
    val email: String,

    @SerializedName("user_height")
    val height: Double,

    @SerializedName("user_weight")
    val weight: Double,

    @SerializedName("user_objective")
    val objective: Int,

    @SerializedName("user_points")
    val points: Int,

    @SerializedName("user_role")
    val role: Int
)

data class RegisterRequest(
    val username: String,
    val name: String,
    val password: String,
    val birthdate: String,
    val email: String,
    val height: Double,
    val weight: Double,
    val objective: Int
)

data class RegisterResponse(
    val user: UserDto,
    val token: String?
)