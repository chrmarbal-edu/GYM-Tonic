package edu.gymtonic_app.data.remote

import edu.gymtonic_app.data.remote.model.LoginRequest
import edu.gymtonic_app.data.remote.model.LoginResponse
import edu.gymtonic_app.data.remote.remoteDatasource.RemoteDataSource

class Repository() {
    private val remoteDataSource = RemoteDataSource()

    // Función para obtener el login.
    suspend fun login(request: LoginRequest): LoginResponse {
        return remoteDataSource.login(request)
    }
}