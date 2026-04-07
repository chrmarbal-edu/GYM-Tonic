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

enum class CalendarDayUiStatus {
	DONE,
	MISSED,
	PENDING
}

data class CalendarDayUi(
	val dayIndex: Int,
	val status: CalendarDayUiStatus
)

data class WeekChallengesUiState(
	val goals: List<WeeklyGoalUi> = emptyList(),
	val calendarDays: List<CalendarDayUi> = emptyList(),
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
			val goalsResult = repository.getWeeklyGoals()
			val calendarResult = repository.getWeeklyCalendarDays()

			goalsResult
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
					val mappedCalendar = calendarResult.getOrElse { emptyList() }.map { day ->
						CalendarDayUi(
							dayIndex = day.dayIndex,
							status = when {
								day.isClosedDay && day.didWorkout -> CalendarDayUiStatus.DONE
								day.isClosedDay && !day.didWorkout -> CalendarDayUiStatus.MISSED
								else -> CalendarDayUiStatus.PENDING
							}
						)
					}

					_uiState.update {
						it.copy(
							goals = mappedGoals,
							calendarDays = normalizeCalendar(mappedCalendar),
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

	private fun normalizeCalendar(days: List<CalendarDayUi>): List<CalendarDayUi> {
		val sortedDays = days.sortedBy { it.dayIndex }
		if (sortedDays.size >= 28) return sortedDays.take(28)

		val padding = (sortedDays.size until 28).map { index ->
			CalendarDayUi(dayIndex = index, status = CalendarDayUiStatus.PENDING)
		}
		return sortedDays + padding
	}
}

