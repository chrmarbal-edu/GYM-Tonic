package edu.gymtonic_app.data.remote.model

import com.google.gson.annotations.SerializedName

data class LoginRequest( //peticion a la api post login
    @SerializedName("username")
    val user: String,
    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    val data: Data,
    val token: String
)


data class Data(
    val user_birthdate: String,
    val user_email: String,
    val user_height: Double,
    val user_id: Int,
    val user_name: String,
    val user_objective: Int,
    val user_password: String,
    val user_points: Any,
    val user_role: Int,
    val user_username: String,
    val user_weight: Double
)