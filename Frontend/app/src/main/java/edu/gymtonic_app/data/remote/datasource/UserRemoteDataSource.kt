package edu.gymtonic_app.data.remote.datasource

import android.util.Log
import edu.gymtonic_app.data.remote.model.Login.LoginRequest
import edu.gymtonic_app.data.remote.model.Login.LoginResponse
import edu.gymtonic_app.data.remote.model.RegisterRequest
import edu.gymtonic_app.data.remote.model.RegisterResponse
import edu.gymtonic_app.data.remote.services.RetrofitClient

class UserRemoteDataSource {
    private val tag = UserRemoteDataSource::class.java.simpleName
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

