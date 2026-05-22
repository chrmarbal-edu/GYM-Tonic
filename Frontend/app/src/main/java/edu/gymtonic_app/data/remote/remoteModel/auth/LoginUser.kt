package edu.gymtonic_app.data.remote.remoteModel.auth

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username")
    val user: String,
    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    val data: LoginUserData?,
    val token: String
)

data class SocialLoginResponse(
    val email: String,
    val username: String,
    val picture: String?,
    val oauth: String
)

data class GoogleLoginRequest(
    @SerializedName("idToken")
    val idToken: String
)

data class FacebookLoginRequest(
    @SerializedName("accessToken")
    val accessToken: String
)

data class LoginUserData(
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
