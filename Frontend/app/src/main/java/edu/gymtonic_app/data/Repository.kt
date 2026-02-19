package edu.gymtonic_app.data

import edu.gymtonic_app.data.remote.model.LoginRequest
import edu.gymtonic_app.data.remote.model.LoginResponse
import edu.gymtonic_app.data.remote.RemoteDataSource

class Repository(
    private val remoteDataSource: RemoteDataSource
) {


    // Función para obtener el login.
    suspend fun login(request: LoginRequest): LoginResponse {
        val response = remoteDataSource.login(request)
        return remoteDataSource.login(request)
    }
}