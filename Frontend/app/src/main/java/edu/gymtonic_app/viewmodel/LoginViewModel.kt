package edu.gymtonic_app.viewmodel

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.Repository
import edu.gymtonic_app.data.remote.RemoteDataSource
import edu.gymtonic_app.data.remote.model.LoginRequest
import edu.gymtonic_app.data.remote.model.LoginResponse
import edu.gymtonic_app.data.remote.model.SessionManager
import edu.gymtonic_app.data.remote.model.sessionDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application): AndroidViewModel(application){
    private val remoteDataSource: RemoteDataSource
    private val repository: Repository

    val sessionManager: SessionManager

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    init {
        remoteDataSource = RemoteDataSource()
        repository = Repository(remoteDataSource)

        val dataStore: DataStore<Preferences> = application.sessionDataStore
        sessionManager = SessionManager(dataStore)
    }

    fun login(username: String, password: String){
        viewModelScope.launch {
            val request = LoginRequest(username, password)
            _loginState.value = LoginState.Loading

            try{
                val response = repository.login(request)

                if(response.ok && response.token != null){
                    sessionManager.saveSession(
                        token = response.token,
                        userId = response.userId!!,
                        username = response.username!!,
                        email = response.email!!,
                        role = response.role!!
                    )

                    _loginState.value = LoginState.Success(response)
                }
            } catch (e: Exception){
                _loginState.value = LoginState.Error(e.message ?: "Error al hacer el login")
            }
        }
    }

}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val response: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}