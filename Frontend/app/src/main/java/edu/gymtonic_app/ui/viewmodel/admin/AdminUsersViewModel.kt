package edu.gymtonic_app.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.user.UserDto
import edu.gymtonic_app.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminUsersViewModel : ViewModel() {
    private val repository = AdminRepository()

    private val _listState = MutableStateFlow(AdminListUiState<UserDto>())
    val listState: StateFlow<AdminListUiState<UserDto>> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(AdminDetailUiState<UserDto>())
    val detailState: StateFlow<AdminDetailUiState<UserDto>> = _detailState.asStateFlow()

    fun loadList() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            repository.fetchUsers()
                .onSuccess { users ->
                    _listState.update { it.copy(items = users, isLoading = false) }
                }
                .onFailure { e ->
                    _listState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            repository.fetchUser(id)
                .onSuccess { user ->
                    _detailState.update { it.copy(item = user, isLoading = false) }
                }
                .onFailure { e ->
                    _detailState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun deleteUser(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteUser(id)
                .onSuccess {
                    _detailState.update { it.copy(deleted = true) }
                    loadList()
                    onSuccess()
                }
                .onFailure { e ->
                    _detailState.update { it.copy(error = e.message) }
                }
        }
    }
}
