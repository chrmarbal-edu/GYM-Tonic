package edu.gymtonic_app.data.remote.remoteDatasource.social

import edu.gymtonic_app.data.remote.remoteModel.social.FriendDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserSummaryDto
import edu.gymtonic_app.data.remote.services.RetrofitClient
import retrofit2.Response

class FriendsRemoteDataSource {

    private val api = RetrofitClient.apiService

    suspend fun getFriends(): Response<List<FriendDto>> = api.getFriends()

    suspend fun getFriendById(id: Int): Response<FriendDto> = api.getFriendById(id)

    suspend fun getFriendsByUserId(userId: Int): Response<List<UserSummaryDto>> =
        api.getFriendsByUserId(userId)

    suspend fun createFriend(request: Map<String, Any>): Response<FriendDto> =
        api.createFriend(request)

    suspend fun deleteFriend(id: Int): Response<Unit> = api.deleteFriend(id)
}
