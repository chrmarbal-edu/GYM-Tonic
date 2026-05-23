package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.remoteDatasource.social.FriendRequestRemoteDataSource
import edu.gymtonic_app.data.remote.remoteDatasource.social.FriendsRemoteDataSource
import edu.gymtonic_app.data.remote.remoteDatasource.user.UsersRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.social.FrequestDto
import edu.gymtonic_app.data.remote.remoteModel.social.FriendRequestWithUserDto
import edu.gymtonic_app.data.remote.remoteModel.social.FriendRequestsByUserResponse
import edu.gymtonic_app.data.remote.remoteModel.user.UserSummaryDto
import edu.gymtonic_app.core.network.ErrorManager
import retrofit2.Response

class FriendsRepository(
    private val friendsDs: FriendsRemoteDataSource = FriendsRemoteDataSource(),
    private val requestsDs: FriendRequestRemoteDataSource = FriendRequestRemoteDataSource(),
    private val usersDs: UsersRemoteDataSource = UsersRemoteDataSource()
) {

    suspend fun getFriendsForUser(userId: Int): Result<List<UserSummaryDto>> = runCatching {
        unwrapList(friendsDs.getFriendsByUserId(userId), "No se pudieron cargar tus amigos")
    }

    suspend fun getRequestsForUser(userId: Int): Result<FriendRequestsByUserResponse> = runCatching {
        unwrapOne(
            requestsDs.getFriendRequestsByUserId(userId),
            "No se pudieron cargar las solicitudes"
        )
    }

    suspend fun searchUsers(): Result<List<UserSummaryDto>> = runCatching {
        unwrapList(usersDs.getUsers(), "No se pudieron cargar los usuarios")
    }

    suspend fun sendRequest(senderId: Int, receiverId: Int): Result<FrequestDto> = runCatching {
        val body = mapOf<String, Any>(
            "sender" to senderId,
            "receiver" to receiverId
        )
        unwrapOne(requestsDs.createFriendRequest(body), "No se pudo enviar la solicitud")
    }

    // El backend al aceptar crea la fila en Friends y elimina la solicitud en
    // la misma operacion. Devolvemos Result<Unit> porque la UI hara reload de
    // amigos+solicitudes en cualquier caso.
    suspend fun acceptRequest(requestId: Int): Result<Unit> = runCatching {
        val response = requestsDs.acceptFriendRequest(requestId)
        if (!response.isSuccessful) {
            throw Exception(ErrorManager.parseResponseError(response))
        }
        Unit
    }

    suspend fun rejectRequest(requestId: Int): Result<Unit> = runCatching {
        val response = requestsDs.rejectFriendRequest(requestId)
        if (!response.isSuccessful) {
            throw Exception(ErrorManager.parseResponseError(response))
        }
        Unit
    }

    // Cancelar = el sender elimina su propia solicitud pendiente.
    suspend fun cancelRequest(requestId: Int): Result<Unit> = runCatching {
        val response = requestsDs.deleteFriendRequest(requestId)
        if (!response.isSuccessful) {
            throw Exception(ErrorManager.parseResponseError(response))
        }
        Unit
    }

    suspend fun removeFriend(friendshipId: Int): Result<Unit> = runCatching {
        val response = friendsDs.deleteFriend(friendshipId)
        if (!response.isSuccessful) {
            throw Exception(ErrorManager.parseResponseError(response))
        }
        Unit
    }

    private fun <T> unwrapOne(response: Response<T>, defaultMessage: String): T {
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("$defaultMessage (body vacio)")
        }
        throw Exception(ErrorManager.parseResponseError(response))
    }

    private fun <T> unwrapList(response: Response<List<T>>, defaultMessage: String): List<T> {
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        }
        throw Exception(ErrorManager.parseResponseError(response))
    }

    // Helper para mapear DTO de solicitud a "perfil" del otro usuario.
    fun otherUser(request: FriendRequestWithUserDto, viewerUserId: Int): UserSummaryDto {
        return if (request.frequestSender == viewerUserId) {
            UserSummaryDto(
                userId = request.frequestReceiver,
                userUsername = request.receiverUsername,
                userName = request.receiverName,
                userPicture = request.receiverPicture
            )
        } else {
            UserSummaryDto(
                userId = request.frequestSender,
                userUsername = request.senderUsername,
                userName = request.senderName,
                userPicture = request.senderPicture
            )
        }
    }
}
