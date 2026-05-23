package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.remote.remoteModel.social.FriendRequestWithUserDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserSummaryDto
import edu.gymtonic_app.data.repository.FriendsRepository
import edu.gymtonic_app.core.network.ErrorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class FriendsUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val currentUserId: Int? = null,
    val friends: List<UserSummaryDto> = emptyList(),
    val incoming: List<FriendRequestWithUserDto> = emptyList(),
    val outgoing: List<FriendRequestWithUserDto> = emptyList(),
    val error: String? = null,
    // ids con accion en curso para deshabilitar botones individualmente
    val busyRequestIds: Set<Int> = emptySet(),
    val busyFriendIds: Set<Int> = emptySet()
)

class FriendsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FriendsRepository()
    private val sessionManager = SessionManager(application.sessionDataStore)

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private val _searchResults = MutableStateFlow<List<UserSummaryDto>>(emptyList())
    val searchResults: StateFlow<List<UserSummaryDto>> = _searchResults.asStateFlow()

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    fun loadAll() {
        viewModelScope.launch {
            val userId = sessionManager.sessionFlow.first().userId
            if (userId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "No hay sesion activa"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isLoading = _uiState.value.friends.isEmpty(),
                isRefreshing = _uiState.value.friends.isNotEmpty(),
                currentUserId = userId,
                error = null
            )

            val friendsResult = repository.getFriendsForUser(userId)
            val requestsResult = repository.getRequestsForUser(userId)

            val friends = friendsResult.getOrElse { emptyList() }
            val requests = requestsResult.getOrNull()

            val error = friendsResult.exceptionOrNull()?.let { ErrorManager.normalizeError(it) }
                ?: requestsResult.exceptionOrNull()?.let { ErrorManager.normalizeError(it) }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isRefreshing = false,
                currentUserId = userId,
                friends = friends,
                incoming = requests?.incoming.orEmpty(),
                outgoing = requests?.outgoing.orEmpty(),
                error = error
            )
        }
    }

    fun refresh() = loadAll()

    fun sendRequest(receiverId: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            val userId = state.currentUserId ?: return@launch

            if (state.friends.any { it.userId == receiverId }) {
                _actionMessage.value = ALREADY_FRIEND
                return@launch
            }
            val alreadyPending = state.outgoing.any { it.frequestReceiver == receiverId } ||
                state.incoming.any { it.frequestSender == receiverId }
            if (alreadyPending) {
                _actionMessage.value = ALREADY_PENDING
                return@launch
            }

            val result = repository.sendRequest(userId, receiverId)
            result.fold(
                onSuccess = {
                    _actionMessage.value = REQUEST_SENT
                    loadAll()
                },
                onFailure = { _actionMessage.value = ErrorManager.normalizeError(it) }
            )
        }
    }

    fun acceptRequest(requestId: Int) {
        viewModelScope.launch {
            markRequestBusy(requestId, true)

            // Optimismo: removemos la solicitud de incoming antes de la respuesta.
            val previous = _uiState.value
            val optimisticIncoming = previous.incoming.filterNot { it.frequestId == requestId }
            _uiState.value = previous.copy(incoming = optimisticIncoming)

            val result = repository.acceptRequest(requestId)
            markRequestBusy(requestId, false)

            result.fold(
                onSuccess = {
                    _actionMessage.value = ACCEPTED
                    loadAll()
                },
                onFailure = {
                    _uiState.value = previous // revertimos
                    _actionMessage.value = ErrorManager.normalizeError(it)
                }
            )
        }
    }

    fun rejectRequest(requestId: Int) {
        viewModelScope.launch {
            markRequestBusy(requestId, true)
            val previous = _uiState.value
            _uiState.value = previous.copy(
                incoming = previous.incoming.filterNot { it.frequestId == requestId }
            )

            val result = repository.rejectRequest(requestId)
            markRequestBusy(requestId, false)

            result.fold(
                onSuccess = { _actionMessage.value = REJECTED },
                onFailure = {
                    _uiState.value = previous
                    _actionMessage.value = ErrorManager.normalizeError(it)
                }
            )
        }
    }

    fun cancelRequest(requestId: Int) {
        viewModelScope.launch {
            markRequestBusy(requestId, true)
            val previous = _uiState.value
            _uiState.value = previous.copy(
                outgoing = previous.outgoing.filterNot { it.frequestId == requestId }
            )

            val result = repository.cancelRequest(requestId)
            markRequestBusy(requestId, false)

            result.fold(
                onSuccess = { _actionMessage.value = CANCELLED },
                onFailure = {
                    _uiState.value = previous
                    _actionMessage.value = ErrorManager.normalizeError(it)
                }
            )
        }
    }

    fun removeFriend(friendshipId: Int) {
        viewModelScope.launch {
            markFriendBusy(friendshipId, true)
            val previous = _uiState.value
            _uiState.value = previous.copy(
                friends = previous.friends.filterNot { it.friendId == friendshipId }
            )

            val result = repository.removeFriend(friendshipId)
            markFriendBusy(friendshipId, false)

            result.fold(
                onSuccess = { _actionMessage.value = REMOVED },
                onFailure = {
                    _uiState.value = previous
                    _actionMessage.value = ErrorManager.normalizeError(it)
                }
            )
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            val state = _uiState.value
            val userId = state.currentUserId
            val result = repository.searchUsers()
            val all = result.getOrElse { emptyList() }
            val q = query.trim().lowercase()

            val excludeIds = buildSet {
                userId?.let { add(it) }
                addAll(state.friends.map { it.userId })
                addAll(state.outgoing.map { it.frequestReceiver })
                addAll(state.incoming.map { it.frequestSender })
            }

            val filtered = all.asSequence()
                .filter { it.userId !in excludeIds }
                .filter { it.userRole != 1 } // Ocultar administradores (rol 1), mostrar el resto (0 o null)
                .filter { user ->
                    if (q.isEmpty()) true
                    else (user.userUsername?.lowercase()?.contains(q) == true) ||
                        (user.userName?.lowercase()?.contains(q) == true)
                }
                .take(20)
                .toList()

            _searchResults.value = filtered
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
    }

    private fun markRequestBusy(id: Int, busy: Boolean) {
        val current = _uiState.value
        val set = current.busyRequestIds.toMutableSet().apply {
            if (busy) add(id) else remove(id)
        }
        _uiState.value = current.copy(busyRequestIds = set)
    }

    private fun markFriendBusy(id: Int, busy: Boolean) {
        val current = _uiState.value
        val set = current.busyFriendIds.toMutableSet().apply {
            if (busy) add(id) else remove(id)
        }
        _uiState.value = current.copy(busyFriendIds = set)
    }

    companion object {
        // Claves para que la UI mapee a strings localizadas (LocalStrings).
        const val ALREADY_FRIEND = "__friends_already_friend"
        const val ALREADY_PENDING = "__friends_already_pending"
        const val REQUEST_SENT = "__friends_request_sent"
        const val ACCEPTED = "__friends_accepted"
        const val REJECTED = "__friends_rejected"
        const val CANCELLED = "__friends_cancelled"
        const val REMOVED = "__friends_removed"
    }
}
