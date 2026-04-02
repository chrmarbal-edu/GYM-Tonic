package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.RemoteDataSource
import edu.gymtonic_app.data.remote.datasource.model.Login.LoginRequest
import edu.gymtonic_app.data.remote.datasource.model.Login.LoginResponse
import edu.gymtonic_app.data.remote.datasource.model.RegisterRequest
import edu.gymtonic_app.data.remote.datasource.model.RegisterResponse

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

    suspend fun logout(): Result<Unit> {
        return runCatching {
            remoteDataSource.logout()
        }
    }
}