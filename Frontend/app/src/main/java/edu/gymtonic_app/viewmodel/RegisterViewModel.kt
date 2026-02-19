package edu.gymtonic_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.Repository
import edu.gymtonic_app.data.remote.RemoteDataSource
import edu.gymtonic_app.data.remote.model.RegisterRequest
import edu.gymtonic_app.data.remote.model.RegisterResponse
import edu.gymtonic_app.data.remote.model.SessionManager
import edu.gymtonic_app.data.remote.model.sessionDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application): AndroidViewModel(application) {
    val remoteDataSource: RemoteDataSource

    val repository: Repository

    val sessionManager: SessionManager

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    init {
        remoteDataSource = RemoteDataSource()
        repository = Repository(remoteDataSource)

        val dataStore: DataStore<Preferences> = application.sessionDataStore
        sessionManager = SessionManager(dataStore)
    }

    fun register(
        username: String,
        name: String,
        password: String,
        birthdate: String,
        email: String,
        height: Double,
        weight: Double,
        objective: Int
    ) {
        viewModelScope.launch {
            val request = RegisterRequest(username, name, password, birthdate, email, height, weight, objective)
            _registerState.value = RegisterState.Loading

            try{
                val response = repository.register(request)
                Log.i("register",response.toString())
                if(response.token != null){
                    sessionManager.saveSession(
                        token = response.token,

                        userId = response.user.id,
                        username = response.user.username,
                        email = response.user.email,
                        role = response.user.role
                    )


                    _registerState.value = RegisterState.Success(response)
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