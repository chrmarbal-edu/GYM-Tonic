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
    @SerializedName("userData")
    val userData: UserDto? = null,
    val token: String? = null,
    @SerializedName("confirmationCode", alternate = ["confirmation_code"])
    val confirmationCode: String? = null,
    @SerializedName("expirationCode", alternate = ["expiration_code", "expiration_date", "expiration"])
    val expirationCode: String? = null
) {
    fun resolvedUser(): UserDto? = user ?: data ?: userData

    fun isCodeExpired(): Boolean {
        if (expirationCode == null) return false
        return try {
            // Probamos el formato con espacio y el formato ISO con 'T'
            val formats = listOf("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss")
            val now = java.util.Date()
            formats.any { format ->
                try {
                    val sdf = java.text.SimpleDateFormat(format, java.util.Locale.getDefault())
                    val expirationDate = sdf.parse(expirationCode)
                    expirationDate?.before(now) ?: false
                } catch (e: Exception) {
                    false
                }
            }
        } catch (e: Exception) {
            false
        }
    }
}

data class ResetPasswordRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("newPassword", alternate = ["new_password", "password"])
    val newPassword: String
)

data class RecoverResponse(
    @SerializedName("msg")
    val msg: String? = null,
    @SerializedName("recoveryToken", alternate = ["recovery_token", "token"])
    val recoveryToken: String,
    @SerializedName("expiresAt", alternate = ["expires_at", "expiration"])
    val expiresAt: String // ISO Date
)

data class ChangePasswordRequest(
    @SerializedName("code", alternate = ["confirmation_code", "otp", "recovery_code", "confirmationCode"])
    val code: String,
    @SerializedName("recoveryToken", alternate = ["recovery_token", "token"])
    val recoveryToken: String
)
