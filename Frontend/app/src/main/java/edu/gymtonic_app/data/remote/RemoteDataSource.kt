package edu.gymtonic_app.data.remote

import android.util.Log
import edu.gymtonic_app.data.remote.model.LoginRequest
import edu.gymtonic_app.data.remote.model.LoginResponse
import edu.gymtonic_app.data.remote.model.RegisterRequest
import edu.gymtonic_app.data.remote.model.RegisterResponse

class RemoteDataSource {
    //Para logear
    private val TAG = RemoteDataSource::class.java.simpleName
    private val api = RetrofitClient.apiService

    // Función para obtener el login, se pasa el objeto RequestLogin en el body.
    // Se devuelve un objeto LoginResponse.
    suspend fun login(request: LoginRequest): LoginResponse {
        val response = api.login(request)
        if (response.isSuccessful) { //si es 200
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error: ${response.message()} | $errorBody")
            throw Exception("Error en login: ${response.message()}")
        }
    }

    suspend fun register(request: RegisterRequest): RegisterResponse {
        val response = api.register(request)
        if(response.isSuccessful){
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        } else{
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error: ${response.message()} | $errorBody")
            throw Exception("Error en login: ${response.message()}")
        }
    }

    suspend fun logout(){
        api.logout()
    }
}