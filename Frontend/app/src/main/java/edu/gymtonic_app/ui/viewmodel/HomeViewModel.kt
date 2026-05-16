package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.repository.AuthRepository
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import kotlinx.coroutines.launch

class HomeViewModel(application: Application): AndroidViewModel(application){
    val authRepository: AuthRepository

    val sessionManager: SessionManager

    init{
        authRepository = AuthRepository()

        val dataStore: DataStore<Preferences> = application.sessionDataStore
        sessionManager = SessionManager(dataStore)
    }

    fun logout(
        onLoggedOut: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val clearResult = runCatching { sessionManager.clearSession() }
            if (clearResult.isFailure) {
                onError(clearResult.exceptionOrNull()?.message ?: "No se pudo limpiar la sesión local")
                return@launch
            }
            onLoggedOut()

            val remoteResult = authRepository.logout()
            if (remoteResult.isFailure) {
                onError(remoteResult.exceptionOrNull()?.message ?: "No se pudo cerrar sesión en servidor")
            }
        }
    }
}