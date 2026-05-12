package edu.gymtonic_app.data.remote.remoteDatasource.social

import edu.gymtonic_app.data.remote.services.RetrofitClient

class FriendRequestRemoteDataSource {
    private val api = RetrofitClient.apiService
    suspend fun getFriendRequests() = api.getFriendRequests()

    suspend fun getFriendRequestById( id: String) = api.getFriendRequestById(id)

    suspend fun createFriendRequest(request: Map<String, Any>) = api.createFriendRequest(request)

    suspend fun acceptFriendRequest( id: String)= api.acceptFriendRequest(id)

    suspend fun rejectFriendRequest( id: String) = api.rejectFriendRequest(id)

    suspend fun deleteFriendRequest( id: String) = api.deleteFriendRequest(id)
}