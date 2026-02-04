package edu.gymtonic_app.data.remote.model

import com.google.gson.annotations.SerializedName

sealed class LoginState {
    object Idle : LoginState()      // Estado inactivo (esperando acción del usuario (antes de larle al boton)), le de a iniciar sesion "standby". Al crearlo LoginState me permite comprobarlo con el when
    object Loading : LoginState()   // Estado cargando (esperando respuesta del servidor una vez le da a aceptar al login)
    data class Success(val response: LoginResponse) : LoginState()  // Estado éxito (respuesta correcta del servidor), contiene la respuesta de loginresponse
    data class Error(val message: String) : LoginState()    // Estado error (respuesta incorrecta del servidor)
}


data class LoginRequest( //peticion a la api post login
    @SerializedName("usuario")
    val user: String,
    @SerializedName("password")
    val password: String
)


data class LoginResponse( //respuesta de la api ante peticion de login, tanto para ok como para cuando falla
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("token") val token: String?,
    @SerializedName("message") val message: String?, //para fallos
    val username: String //para guardame yo el campo y no pedirlo constantemente
)