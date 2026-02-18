package edu.gymtonic_app.data.remote.model

import com.google.gson.annotations.SerializedName

data class LoginRequest( //peticion a la api post login
    @SerializedName("usuario")
    val user: String,
    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("token") val token: String?,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("user_username") val username: String?,
    @SerializedName("user_email") val email: String?,
    @SerializedName("user_role") val role: Int?
)
