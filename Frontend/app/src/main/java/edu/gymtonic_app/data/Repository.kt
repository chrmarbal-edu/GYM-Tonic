package edu.gymtonic_app.data

import edu.gymtonic_app.data.remote.RemoteDataSource
import edu.gymtonic_app.data.remote.model.LoginRequest
import edu.gymtonic_app.data.remote.model.LoginResponse
import edu.gymtonic_app.data.remote.model.RegisterRequest
import edu.gymtonic_app.data.remote.model.RegisterResponse

class Repository(
    private val remoteDataSource: RemoteDataSource
) {


    // Función para obtener el login.
    suspend fun login(request: LoginRequest): LoginResponse {
        return remoteDataSource.login(request)
    }

    suspend fun register(request: RegisterRequest): RegisterResponse {
        return remoteDataSource.register(request)
    }
}