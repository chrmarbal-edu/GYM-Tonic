package edu.gymtonic_app.data.remote.remoteDatasource

import edu.gymtonic_app.data.remote.services.RetrofitClient

class GroupRemoteDataSource {
    private val api = RetrofitClient.apiService

    suspend fun getGroups() = api.getGroups()

    suspend fun getGroupById( id: Int) = api.getGroupById(id)

    suspend fun createGroup( request: Map<String, Any>) = api.createGroup(request)

    suspend fun updateGroup(id: Int, request: Map<String, Any?>) = api.updateGroup(id, request)

    suspend fun deleteGroup( id: Int) = api.deleteGroup(id)
}
