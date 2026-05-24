package edu.gymtonic_app.ui.viewmodel.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.remote.remoteModel.user.UserDto
import edu.gymtonic_app.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminUsersViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AdminRepository()
    private val sessionManager = SessionManager(application.sessionDataStore)

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
            val session = sessionManager.sessionFlow.first()
            val currentUserId = session.userId

            repository.deleteUser(id)
                .onSuccess {
                    // Si el admin se borra a sí mismo, cerramos sesión y NO cargamos lista
                    if (id == currentUserId) {
                        sessionManager.clearSession()
                        onSuccess() // Esto debería disparar la navegación en la UI
                    } else {
                        _detailState.update { it.copy(deleted = true) }
                        loadList()
                        onSuccess()
                    }
                }
                .onFailure { e ->
                    // Si falla por autorización, probablemente ya no somos admin o sesión expiró
                    val errorMsg = e.message ?: ""
                    if (errorMsg.contains("401") || errorMsg.contains("unauthorized", ignoreCase = true)) {
                        sessionManager.clearSession()
                        onSuccess()
                    } else {
                        _detailState.update { it.copy(error = e.message) }
                    }
                }
        }
    }
}
