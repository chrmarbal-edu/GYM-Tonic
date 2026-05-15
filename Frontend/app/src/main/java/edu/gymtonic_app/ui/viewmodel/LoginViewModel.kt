package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.repository.AuthRepository
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginRequest
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginResponse
import edu.gymtonic_app.data.remote.remoteModel.auth.GoogleLoginResponse
import com.google.gson.Gson
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application): AndroidViewModel(application){
    private val authRepository: AuthRepository

    val sessionManager: SessionManager

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    init {
        authRepository = AuthRepository()

        val dataStore: DataStore<Preferences> = application.sessionDataStore
        sessionManager = SessionManager(dataStore)
    }

    fun login(username: String, password: String){
        viewModelScope.launch {
            val request = LoginRequest(username, password)
            _loginState.value = LoginState.Loading

            try{
                val response = authRepository.login(request)
                handleLoginResponse(response)
            } catch (e: Exception){
                _loginState.value = LoginState.Error(e.message ?: "Error al hacer el login")
            }
        }
    }

    fun googleLogin(idToken: String) {
        Log.d("LoginViewModel", "googleLogin called with token: ${idToken.take(15)}...")
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                Log.d("LoginViewModel", "Requesting backend googleLogin...")
                val result = authRepository.googleLogin(idToken)
                Log.d("LoginViewModel", "Backend response received: $result")

                val gson = Gson()
                val json = gson.toJson(result)
                
                // Comprobamos si es un usuario nuevo o una sesión existente
                if (json.contains("\"oauth\":\"Google\"") || json.contains("\"oauth\": \"Google\"")) {
                    Log.d("LoginViewModel", "New user detected, redirecting to registration Step 2")
                    val googleData = gson.fromJson(json, GoogleLoginResponse::class.java)
                    _loginState.value = LoginState.NeedsRegistration(googleData)
                } else {
                    Log.d("LoginViewModel", "Existing user detected, performing direct login")
                    val loginResponse = gson.fromJson(json, LoginResponse::class.java)
                    handleLoginResponse(loginResponse)
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error in googleLogin", e)
                _loginState.value = LoginState.Error(e.message ?: "Error al iniciar sesión con Google")
            }
        }
    }

    private suspend fun handleLoginResponse(response: LoginResponse) {
        Log.i("login", response.toString())
        if (response.token != null) {
            sessionManager.saveSession(
                token = response.token,
                userId = response.data.user_id,
                username = response.data.user_username,
                email = response.data.user_email,
                role = response.data.user_role
            )
            _loginState.value = LoginState.Success(response)
        } else {
            _loginState.value = LoginState.Error("Token no recibido")
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val response: LoginResponse) : LoginState()
    data class NeedsRegistration(val googleData: GoogleLoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}
