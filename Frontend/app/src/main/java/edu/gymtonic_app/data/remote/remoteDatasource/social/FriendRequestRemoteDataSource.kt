package edu.gymtonic_app.data.remote.remoteDatasource.social

import edu.gymtonic_app.data.remote.services.RetrofitClient

class FriendRequestRemoteDataSource {
    private val api = RetrofitClient.apiService
    suspend fun getFriendRequests() = api.getFriendRequests()

    suspend fun getFriendRequestById( id: Int) = api.getFriendRequestById(id)

    suspend fun createFriendRequest(request: Map<String, Any>) = api.createFriendRequest(request)

    suspend fun acceptFriendRequest( id: Int)= api.acceptFriendRequest(id)

    suspend fun rejectFriendRequest( id: Int) = api.rejectFriendRequest(id)

    suspend fun deleteFriendRequest( id: Int) = api.deleteFriendRequest(id)
}