package edu.gymtonic_app.ui.viewmodel.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import edu.gymtonic_app.data.repository.RepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminGroupsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RepositoryProvider.getAdminRepository(application)

    private val _listState = MutableStateFlow(AdminListUiState<GroupDto>())
    val listState: StateFlow<AdminListUiState<GroupDto>> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(AdminGroupDetailState())
    val detailState: StateFlow<AdminGroupDetailState> = _detailState.asStateFlow()

    fun loadList() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            repository.fetchGroups()
                .onSuccess { groups ->
                    _listState.update { it.copy(items = groups, isLoading = false) }
                }
                .onFailure { e ->
                    _listState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun loadDetail(groupId: Int) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            val groupResult = repository.fetchGroup(groupId)
            val membersResult = repository.fetchGroupMembers(groupId)
            val usersResult = repository.fetchUsers()

            groupResult
                .onSuccess { group ->
                    val usersById = usersResult.getOrNull()
                        ?.associateBy { it.userId }
                        .orEmpty()
                    val members = membersResult.getOrElse { emptyList() }.map { member ->
                        val user = usersById[member.userId]
                        AdminGroupMemberUi(
                            userId = member.userId,
                            range = member.range,
                            displayName = user?.userName ?: user?.userUsername ?: "Usuario #${member.userId}",
                            profilePicture = user?.userPicture,
                            points = user?.userPoints ?: member.userPoints
                        )
                    }
                    _detailState.update {
                        it.copy(group = group, members = members, isLoading = false)
                    }
                }
                .onFailure { e ->
                    _detailState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun updateGroup(id: Int, name: String, description: String, points: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _detailState.update { it.copy(isSaving = true, error = null) }
            repository.updateGroup(id, name, description, points)
                .onSuccess { group ->
                    _detailState.update { it.copy(group = group, isSaving = false) }
                    loadList()
                    onSuccess()
                }
                .onFailure { e ->
                    _detailState.update { it.copy(isSaving = false, error = e.message) }
                }
        }
    }

    fun deleteGroup(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteGroup(id)
                .onSuccess {
                    loadList()
                    onSuccess()
                }
                .onFailure { e ->
                    _detailState.update { it.copy(error = e.message) }
                }
        }
    }
}
