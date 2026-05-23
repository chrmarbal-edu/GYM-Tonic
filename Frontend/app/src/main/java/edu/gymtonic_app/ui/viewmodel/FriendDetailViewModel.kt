package edu.gymtonic_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserDto
import edu.gymtonic_app.data.repository.GroupRepository
import edu.gymtonic_app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FriendDetailUiState(
    val isLoading: Boolean = true,
    val friend: UserDto? = null,
    val sharedGroups: List<GroupDto> = emptyList(),
    val error: String? = null
)

class FriendDetailViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val groupRepository: GroupRepository = GroupRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendDetailUiState())
    val uiState: StateFlow<FriendDetailUiState> = _uiState.asStateFlow()

    fun loadFriendDetail(friendId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val userResult = userRepository.getUserById(friendId)
            val myGroupsResult = groupRepository.getUserGroups()

            if (userResult.isSuccess && myGroupsResult.isSuccess) {
                val friend = userResult.getOrNull()
                val myGroups = myGroupsResult.getOrNull().orEmpty()
                
                // Para encontrar los grupos compartidos, para cada grupo mio
                // miramos si el amigo es miembro.
                val shared = mutableListOf<GroupDto>()
                for (group in myGroups) {
                    val membersResult = groupRepository.getGroupMembers(group.group_id)
                    val members = membersResult.getOrNull().orEmpty()
                    if (members.any { it.userId == friendId }) {
                        shared.add(group)
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    friend = friend,
                    sharedGroups = shared
                )
            } else {
                val error = userResult.exceptionOrNull()?.message 
                    ?: myGroupsResult.exceptionOrNull()?.message 
                    ?: "Error desconocido"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error
                )
            }
        }
    }
}
