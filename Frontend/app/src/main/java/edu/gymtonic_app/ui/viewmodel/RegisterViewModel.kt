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
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterRequest
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application): AndroidViewModel(application) {
    val authRepository: AuthRepository

    val sessionManager: SessionManager

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    // Datos temporales para registro con Google
    var googleUserData: GoogleUserData? = null
        private set

    data class GoogleUserData(
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

    fun prepareGoogleRegistration(name: String, email: String, picture: String?, oauth: String) {
        googleUserData = GoogleUserData(name, email, picture, oauth)
    }

    fun clearGoogleData() {
        googleUserData = null
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
        oauth: String? = null
    ) {
        viewModelScope.launch {
            val request = RegisterRequest(username, name, password, birthdate, email, height, weight, objective, oauth)
            _registerState.value = RegisterState.Loading

            try{
                val response = authRepository.register(request)
                Log.i("register",response.toString())
                val user = response.resolvedUser()
                if(response.token != null && user != null){
                    clearGoogleData() // Limpiamos los datos de Google tras el éxito
                    sessionManager.saveSession(
                        token = response.token,
                        userId = user.userId,
                        username = user.userUsername,
                        email = user.userEmail,
                        role = user.userRole
                    )
                    _registerState.value = RegisterState.Success(response)
                } else {
                    _registerState.value = RegisterState.Error("Usuario o token nulo en respuesta")
                }
            } catch (e: Exception){
                _registerState.value = RegisterState.Error(e.message ?: "Error en el registro")
            }
        }
    }
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val response: RegisterResponse) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
