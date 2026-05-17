package edu.gymtonic_app.data.remote.remoteModel.user

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_username")
    val userUsername: String,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("user_birthdate")
    val userBirthdate: String,
    @SerializedName("user_email")
    val userEmail: String,
    @SerializedName("user_picture")
    val userPicture: String? = null,
    @SerializedName("user_height")
    val userHeight: Float,
    @SerializedName("user_weight")
    val userWeight: Float,
    @SerializedName("user_objetive")
    val userObjetive: Int,
    @SerializedName("user_points")
    val userPoints: Int,
    @SerializedName("user_role")
    val userRole: Int,
    @SerializedName("user_oauth")
    val userOauth: String? = null
)

data class RegisterRequest(
    val username: String,
    val name: String,
    val password: String? = null,
    val birthdate: String,
    val email: String,
    val height: Double,
    val weight: Double,
    val objective: Int,
    val oauth: String? = null,
    val picture: String? = null
)

data class RegisterResponse(
    val user: UserDto? = null,
    val data: UserDto? = null,
    val token: String? = null
) {
    fun resolvedUser(): UserDto? = user ?: data
}
