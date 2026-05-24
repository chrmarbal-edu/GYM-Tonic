package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.remote.remoteModel.user.UserDto
import edu.gymtonic_app.data.repository.RepositoryProvider
import edu.gymtonic_app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

sealed class AccountUiState {
    object Loading : AccountUiState()
    data class Success(val user: UserDto) : AccountUiState()
    data class Error(val message: String) : AccountUiState()
}

class AccountViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = RepositoryProvider.getUserRepository(application)
    private val sessionManager = SessionManager(application.sessionDataStore)

    private val _uiState = MutableStateFlow<AccountUiState>(AccountUiState.Loading)
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            _uiState.value = AccountUiState.Loading
            val session = sessionManager.sessionFlow.first()
            val userId = session.userId ?: return@launch
            
            userRepository.getUserById(userId).onSuccess { user ->
                _uiState.value = AccountUiState.Success(user)
            }.onFailure { e ->
                _uiState.value = AccountUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateAccount(
        username: String,
        currentPassword: String?,
        newPassword: String,
        height: Double?,
        weight: Double?,
        objective: Int? = null,
        pictureFile: File? = null,
        isDefaultPicture: Boolean = false
    ) {
        viewModelScope.launch {
            val session = sessionManager.sessionFlow.first()
            val userId = session.userId ?: return@launch
            
            val currentState = _uiState.value as? AccountUiState.Success
            val currentUser = currentState?.user

            // Si hay archivo nuevo, priorizamos Multipart
            val result = if (pictureFile != null) {
                userRepository.updateUserWithFile(
                    id = userId,
                    username = username,
                    currentPassword = currentPassword,
                    newPassword = if (newPassword.isNotBlank()) newPassword else null,
                    height = height,
                    weight = weight,
                    objective = objective,
                    pictureFile = pictureFile
                )
            } else {
                // Si no hay archivo, mandamos JSON. 
                // Si isDefaultPicture es true, mandamos "default" en el campo picture.
                val data = mutableMapOf<String, Any?>()
                data["username"] = username
                if (currentPassword?.isNotBlank() == true) data["currentPassword"] = currentPassword
                if (newPassword.isNotBlank()) data["newPassword"] = newPassword
                data["height"] = height
                data["weight"] = weight
                if (objective != null) data["objective"] = objective
                if (isDefaultPicture) data["picture"] = "default"
                
                userRepository.updateUser(userId, data)
            }

            result.onSuccess { loginResponse ->
                loginResponse.data?.let { userData ->
                    sessionManager.saveSession(
                        token = loginResponse.token,
                        userId = userData.user_id,
                        username = userData.user_username,
                        email = userData.user_email,
                        role = userData.user_role
                    )
                    Log.d("AccountViewModel", "Perfil y sesión actualizados con éxito")
                } ?: Log.e("AccountViewModel", "Error: La respuesta del servidor no contiene datos de usuario")

                _toastMessage.value = "Perfil actualizado correctamente"
                loadUser()
            }.onFailure { e ->
                Log.e("AccountViewModel", "Error al actualizar perfil", e)
                _toastMessage.value = e.message ?: "No se pudo actualizar el perfil"
            }
        }
    }

    fun deleteAccount(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val session = sessionManager.sessionFlow.first()
            val userId = session.userId ?: return@launch

            val result = userRepository.deleteUser(userId)
            
            // Independientemente de si falla (p.ej. 401), si intentamos borrar y no podemos porque no estamos autorizados,
            // procedemos a limpiar la sesión local.
            if (result.isSuccess) {
                sessionManager.clearSession()
                onDeleted()
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: ""
                if (errorMsg.contains("401") || errorMsg.contains("404") || errorMsg.contains("unauthorized", ignoreCase = true)) {
                    sessionManager.clearSession()
                    onDeleted()
                } else {
                    _toastMessage.value = result.exceptionOrNull()?.message ?: "No se pudo eliminar la cuenta"
                }
            }
        }
    }
}
