package edu.gymtonic_app.data.remote.remoteDatasource.user

import edu.gymtonic_app.data.remote.services.RetrofitClient

class UsersRemoteDataSource {
    private val api = RetrofitClient.apiService

    suspend fun getUsers() = api.getUsers()

    suspend fun getUserById(id: Int) = api.getUserById(id)

    suspend fun updateUser(id: Int, request: Map<String, Any?>) = api.updateUser(id, request)

    suspend fun deleteUser(id: Int) = api.deleteUser(id)
}