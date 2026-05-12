package edu.gymtonic_app.data.remote.remoteDatasource

import edu.gymtonic_app.data.remote.remoteModel.auth.LoginRequest
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterRequest
import edu.gymtonic_app.data.remote.services.RetrofitClient

class UsersRemoteDataSource {
    private val api = RetrofitClient.apiService

    suspend fun getUsers() = api.getUsers()

    suspend fun getUserById(id: String) = api.getUserById(id)

    suspend fun updateUser(id: String, request: Map<String, Any?>) = api.updateUser(id, request)

    suspend fun deleteUser(id: String) = api.deleteUser(id)
}