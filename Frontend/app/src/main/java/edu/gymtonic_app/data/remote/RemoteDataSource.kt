package edu.gymtonic_app.data.remote

import android.util.Log
import edu.gymtonic_app.data.remote.model.LoginRequest
import edu.gymtonic_app.data.remote.model.LoginResponse

class RemoteDataSource {
    //Para logear
    private val TAG = RemoteDataSource::class.java.simpleName
    private val api = RetrofitClient.apiService

    // Función para obtener el login, se pasa el objeto RequestLogin en el body.
    // Se devuelve un objeto LoginResponse.
    suspend fun login(request: LoginRequest): LoginResponse {
        val response = api.login(request) //devuelve le objeto response que devuelve
        if (response.isSuccessful) { //si es 200
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        } else { //cojo del response el errorbody y lo propago
            val errorBody = response.errorBody()?.string() // Se obtienen detalles del error.
            Log.e(TAG, "Error: ${response.message()} | $errorBody")
            throw Exception("Error en login: ${response.message()}")
        }
    }
}