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
            // 1. Logout remoto primero para usar el token antes de limpiarlo localmente
            val remoteResult = authRepository.logout()
            if (remoteResult.isFailure) {
                // Si falla (p.ej. 401), lo logueamos pero seguimos para limpiar localmente
                val msg = remoteResult.exceptionOrNull()?.message ?: "Error en logout remoto"
                android.util.Log.e("HomeViewModel", "Remote logout failed: $msg")
            }

            // 2. Limpiar sesión local (DataStore)
            val clearResult = runCatching { sessionManager.clearSession() }
            if (clearResult.isFailure) {
                onError(clearResult.exceptionOrNull()?.message ?: "No se pudo limpiar la sesión local")
                return@launch
            }
            
            onLoggedOut()
        }
    }
}