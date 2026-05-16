package edu.gymtonic_app.data.remote.remoteModel.user

import com.google.gson.annotations.SerializedName

// DTO ligero para listas/busqueda: el backend solo envia campos publicos
// para no-admin (no incluye password, email, etc.). En la lista de amigos
// el backend incluye tambien friend_id (PK de Friends) para poder borrar
// la amistad sin consultar de nuevo.
data class UserSummaryDto(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_username")
    val userUsername: String? = null,
    @SerializedName("user_name")
    val userName: String? = null,
    @SerializedName("user_picture")
    val userPicture: String? = null,
    @SerializedName("friend_id")
    val friendId: Int? = null
)
