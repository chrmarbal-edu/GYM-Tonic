package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.remote.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class DiscountsUiState {
    object Loading : DiscountsUiState()
    data class Success(val points: Int) : DiscountsUiState()
    data class Error(val message: String) : DiscountsUiState()
}

class DiscountsViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application.sessionDataStore)
    private val api = RetrofitClient.apiService

    private val _uiState = MutableStateFlow<DiscountsUiState>(DiscountsUiState.Loading)
    val uiState: StateFlow<DiscountsUiState> = _uiState.asStateFlow()

    init {
        loadPoints()
    }

    fun loadPoints() {
        viewModelScope.launch {
            _uiState.value = DiscountsUiState.Loading

            val session = sessionManager.sessionFlow.first()
            val userId = session.userId
            if (userId == null) {
                _uiState.value = DiscountsUiState.Error("No session")
                return@launch
            }

            try {
                val response = api.getUserById(userId)
                if (response.isSuccessful) {
                    val points = response.body()?.userPoints ?: 0
                    _uiState.value = DiscountsUiState.Success(points)
                } else {
                    _uiState.value = DiscountsUiState.Error("HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = DiscountsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
