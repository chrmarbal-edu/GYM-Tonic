package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.repository.AuthRepository
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class RegisterViewModel(application: Application): AndroidViewModel(application) {
    val authRepository: AuthRepository

    val sessionManager: SessionManager

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    // Datos temporales para registro social (Google/Facebook)
    var socialUserData: SocialUserData? = null
        private set

    data class SocialUserData(
        val name: String,
        val email: String,
        val picture: String?,
        val oauth: String
    )

    init {
        authRepository = AuthRepository()

        val dataStore: DataStore<Preferences> = application.sessionDataStore
        sessionManager = SessionManager(dataStore)
    }

    fun prepareSocialRegistration(name: String, email: String, picture: String?, oauth: String) {
        socialUserData = SocialUserData(name, email, picture, oauth)
    }

    fun clearSocialData() {
        socialUserData = null
    }

    fun register(
        username: String,
        name: String,
        password: String?,
        birthdate: String,
        email: String,
        height: Double,
        weight: Double,
        objective: Int,
        oauth: String? = null,
        pictureFile: File? = null,
        pictureUrl: String? = null
    ) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            try{
                val response = authRepository.register(
                    username = username.trim(),
                    name = name.trim(),
                    password = password?.trim(),
                    birthdate = birthdate.trim(),
                    email = email.trim(),
                    height = height,
                    weight = weight,
                    objective = objective,
                    oauth = oauth,
                    pictureFile = pictureFile,
                    pictureUrl = pictureUrl
                )
                Log.i("register", response.toString())
                val user = response.resolvedUser()
                if (user != null) {
                    // Si es un registro social (OAuth) o el backend ya nos da un token (ya verificado),
                    // iniciamos sesión directamente y saltamos el código de confirmación.
                    if (response.token != null && (oauth != null || response.confirmationCode == null)) {
                        clearSocialData()
                        sessionManager.saveSession(
                            token = response.token,
                            userId = user.userId,
                            username = user.userUsername,
                            email = user.userEmail,
                            role = user.userRole
                        )
                        _registerState.value = RegisterState.Success(response)
                    } else {
                        _registerState.value = RegisterState.AwaitingConfirmation(response)
                    }
                } else {
                    _registerState.value = RegisterState.Error("Usuario nulo en respuesta")
                }
            } catch (e: Exception){
                _registerState.value = RegisterState.Error(e.message ?: "Error en el registro")
            }
        }
    }

    fun confirmCode(code: String, response: RegisterResponse) {
        if (response.isCodeExpired()) {
            _registerState.value = RegisterState.Error("El código ha expirado. Por favor, solicita uno nuevo.")
            return
        }

        if (code == response.confirmationCode) {
            viewModelScope.launch {
                val user = response.resolvedUser()!!
                if (response.token != null) {
                    clearSocialData()
                    sessionManager.saveSession(
                        token = response.token,
                        userId = user.userId,
                        username = user.userUsername,
                        email = user.userEmail,
                        role = user.userRole
                    )
                    _registerState.value = RegisterState.Success(response)
                } else {
                    _registerState.value = RegisterState.Error("Token nulo en respuesta")
                }
            }
        } else {
            // Guardamos el estado actual para no perder los datos de la respuesta
            val currentState = _registerState.value as? RegisterState.AwaitingConfirmation
            _registerState.value = RegisterState.Error("Código de confirmación incorrecto")
            
            // Si teníamos una respuesta previa, volvemos al estado de espera tras mostrar el error
            // El componente ObserveToastMessage mostrará el error mientras tanto
            if (currentState != null) {
                viewModelScope.launch {
                    kotlinx.coroutines.delay(100) // Pequeña espera para asegurar que el estado de Error sea procesado
                    _registerState.value = currentState
                }
            }
        }
    }

    fun resetToIdle() {
        _registerState.value = RegisterState.Idle
    }
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class AwaitingConfirmation(val response: RegisterResponse) : RegisterState()
    data class Success(val response: RegisterResponse) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
