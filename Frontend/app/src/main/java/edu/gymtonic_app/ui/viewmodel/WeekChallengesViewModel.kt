package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.RemoteDataSource
import edu.gymtonic_app.data.repository.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeeklyGoalUi(
	val title: String,
	val progressLabel: String,
	val pointsLabel: String,
	val progress: Float
)

data class WeekChallengesUiState(
	val goals: List<WeeklyGoalUi> = emptyList(),
	val achievedLabel: String = "0/0 Logrados",
	val isRefreshing: Boolean = false,
	val errorMessage: String? = null
)

class WeekChallengesViewModel(application: Application) : AndroidViewModel(application) {
	val remoteDataSource: RemoteDataSource = RemoteDataSource()
	val repository: Repository = Repository(remoteDataSource)

	private val _uiState = MutableStateFlow(WeekChallengesUiState())
	val uiState: StateFlow<WeekChallengesUiState> = _uiState.asStateFlow()

	init {
		loadWeekGoals()
	}

	fun refreshWeekGoals() {
		viewModelScope.launch {
			_uiState.update { it.copy(isRefreshing = true) }
			delay(700)
			loadWeekGoals()
		}
	}

	private fun loadWeekGoals() {
		viewModelScope.launch {
			repository.getWeeklyGoals()
				.onSuccess { remoteGoals ->
					val mappedGoals = remoteGoals.map {
						WeeklyGoalUi(
							title = it.title,
							progressLabel = it.progressLabel,
							pointsLabel = it.pointsLabel,
							progress = it.progress
						)
					}
					val achievedCount = mappedGoals.count { it.progress >= 1f }

					_uiState.update {
						it.copy(
							goals = mappedGoals,
							achievedLabel = "$achievedCount/${mappedGoals.size} Logrados",
							isRefreshing = false,
							errorMessage = null
						)
					}
				}
				.onFailure { error ->
					_uiState.update {
						it.copy(
							isRefreshing = false,
							errorMessage = error.message ?: "No se pudo cargar semana"
						)
					}
				}
		}
	}
}

