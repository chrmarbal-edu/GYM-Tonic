package edu.gymtonic_app.data.remote.remoteDatasource.social

import edu.gymtonic_app.data.remote.services.RetrofitClient

class FriendsRemoteDataSource {

    private val api = RetrofitClient.apiService

    suspend fun getFriends()= api.getFriends()

    suspend fun getFriendById(id: Int)= api.getFriendById(id)

    suspend fun getFriendsByUserId(userId: Int) = api.getFriendsByUserId(userId)

    suspend fun createFriend(request: Map<String, Any>) = api.createFriend(request)

    suspend fun deleteFriend(id: Int) = api.deleteFriend(id)
}