package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.remote.remoteModel.user.UserDto
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
    private val userRepository = UserRepository()
    private val sessionManager = SessionManager(application.sessionDataStore)

    private val _uiState = MutableStateFlow<AccountUiState>(AccountUiState.Loading)
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

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
        password: String,
        height: Double?,
        weight: Double?,
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
                    username = if (username != currentUser?.userUsername) username else null,
                    password = if (password.isNotBlank()) password else null,
                    height = height,
                    weight = weight,
                    pictureFile = pictureFile
                )
            } else {
                // Si no hay archivo, mandamos JSON. 
                // Si isDefaultPicture es true, mandamos "default" en el campo picture.
                val data = mutableMapOf<String, Any?>()
                if (username != currentUser?.userUsername) data["username"] = username
                if (password.isNotBlank()) data["password"] = password
                data["height"] = height
                data["weight"] = weight
                if (isDefaultPicture) data["picture"] = "default"
                
                userRepository.updateUser(userId, data)
            }

            result.onSuccess { loginResponse ->
                // Actualizamos la sesión completa con el nuevo token y datos devueltos
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
                
                // Convertir LoginUserData a UserDto para la UI del perfil si es necesario, 
                // o recargar el usuario para asegurar consistencia total
                loadUser()
            }.onFailure { e ->
                Log.e("AccountViewModel", "Error al actualizar perfil", e)
            }
        }
    }

    fun deleteAccount(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val session = sessionManager.sessionFlow.first()
            val userId = session.userId ?: return@launch

            userRepository.deleteUser(userId).onSuccess {
                sessionManager.clearSession()
                onDeleted()
            }
        }
    }
}
