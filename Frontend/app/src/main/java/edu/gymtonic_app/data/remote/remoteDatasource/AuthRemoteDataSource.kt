package edu.gymtonic_app.data.remote.remoteDatasource

import android.util.Log
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginRequest
import edu.gymtonic_app.data.remote.remoteModel.auth.GoogleLoginRequest
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginResponse
import edu.gymtonic_app.data.remote.services.RetrofitClient
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterRequest
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterResponse

class AuthRemoteDataSource {
    private val tag = AuthRemoteDataSource::class.java.simpleName
    private val api = RetrofitClient.apiService

    suspend fun login(request: LoginRequest): LoginResponse {
        val response = api.login(request)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta vacia del servidor")
        }

        val errorBody = response.errorBody()?.string()
        Log.e(tag, "Error login: ${response.code()} ${response.message()} | $errorBody")
        throw Exception("Error en login: ${response.message()}")
    }

    suspend fun googleLogin(idToken: String): Any {
        Log.d(tag, "Enviando token a googleLogin: ${idToken.take(10)}...")
        val response = api.googleLogin(GoogleLoginRequest(idToken))
        if (response.isSuccessful) {
            Log.d(tag, "googleLogin exitoso")
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }

        val errorBody = response.errorBody()?.string()
        Log.e(tag, "Error googleLogin: ${response.code()} ${response.message()} | $errorBody")
        throw Exception("Error en googleLogin: ${response.code()} - $errorBody")
    }

    suspend fun register(request: RegisterRequest): RegisterResponse {
        val response = api.register(request)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta vacia del servidor")
        }

        val errorBody = response.errorBody()?.string()
        Log.e(tag, "Error register: ${response.code()} ${response.message()} | $errorBody")
        throw Exception("Error en register: ${response.message()}")
    }

    suspend fun logout() {
        val response = api.logout()
        if (response.isSuccessful) return

        val errorBody = response.errorBody()?.string()
        Log.e(tag, "Error logout: ${response.code()} ${response.message()} | $errorBody")
        throw Exception("Error en logout: ${response.message()}")
    }
}



