package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.repository.AuthRepository
import edu.gymtonic_app.data.repository.RepositoryProvider
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginRequest
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginResponse
import edu.gymtonic_app.data.remote.remoteModel.auth.SocialLoginResponse
import com.google.gson.Gson
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.core.network.ErrorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application): AndroidViewModel(application){
    private val authRepository: AuthRepository

    val sessionManager: SessionManager

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    init {
        authRepository = RepositoryProvider.getAuthRepository(application)

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
                _loginState.value = LoginState.Error(ErrorManager.normalizeError(e))
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
                handleSocialLoginResponse(result)
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error in googleLogin", e)
                _loginState.value = LoginState.Error(ErrorManager.normalizeError(e))
            }
        }
    }

    fun facebookLogin(accessToken: String) {
        Log.d("LoginViewModel", "facebookLogin called with token: ${accessToken.take(15)}...")
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                Log.d("LoginViewModel", "Requesting backend facebookLogin...")
                val result = authRepository.facebookLogin(accessToken)
                handleSocialLoginResponse(result)
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error in facebookLogin", e)
                _loginState.value = LoginState.Error(ErrorManager.normalizeError(e))
            }
        }
    }

    private suspend fun handleSocialLoginResponse(result: Any) {
        Log.d("LoginViewModel", "Social Login response received: $result")
        val gson = Gson()
        val json = gson.toJson(result)

        // Comprobamos si es un usuario nuevo o una sesión existente
        if (json.contains("\"oauth\":\"Google\"") || json.contains("\"oauth\": \"Google\"") ||
            json.contains("\"oauth\":\"Facebook\"") || json.contains("\"oauth\": \"Facebook\"")
        ) {
            Log.d("LoginViewModel", "New user detected, redirecting to registration Step 2")
            val socialData = gson.fromJson(json, SocialLoginResponse::class.java)
            _loginState.value = LoginState.NeedsRegistration(socialData)
        } else {
            Log.d("LoginViewModel", "Existing user detected, performing direct login")
            val loginResponse = gson.fromJson(json, LoginResponse::class.java)
            handleLoginResponse(loginResponse)
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun resetPassword(email: String, password: String, onSuccess: () -> Unit) {
        val trimmedPassword = password.trim()
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            authRepository.recoverAccount(email, trimmedPassword)
                .onSuccess { response ->
                    _loginState.value = LoginState.AwaitingRecoveryConfirmation(
                        recoveryToken = response.recoveryToken,
                        expiresAt = response.expiresAt,
                        email = email
                    )
                    onSuccess()
                }
                .onFailure { e ->
                    _loginState.value = LoginState.Error(ErrorManager.normalizeError(e))
                }
        }
    }

    fun confirmRecoveryCode(code: String, recoveryToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            authRepository.changePassword(code, recoveryToken)
                .onSuccess {
                    _loginState.value = LoginState.Idle
                    onSuccess()
                }
                .onFailure { e ->
                    Log.e("LoginViewModel", "Error al cambiar contraseña", e)
                    _loginState.value = LoginState.Error(ErrorManager.normalizeError(e))
                }
        }
    }

    private suspend fun handleLoginResponse(response: LoginResponse) {
        Log.i("login", response.toString())
        val userData = response.data
        if (response.token != null && userData != null) {
            sessionManager.saveSession(
                token = response.token,
                userId = userData.user_id,
                username = userData.user_username,
                email = userData.user_email,
                role = userData.user_role
            )
            _loginState.value = LoginState.Success(response)
        } else {
            _loginState.value = LoginState.Error("Datos de usuario o token no recibidos")
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val response: LoginResponse) : LoginState()
    data class NeedsRegistration(val socialData: SocialLoginResponse) : LoginState()
    data class AwaitingRecoveryConfirmation(
        val recoveryToken: String,
        val expiresAt: String,
        val email: String
    ) : LoginState()
    data class Error(val message: String) : LoginState()
}
