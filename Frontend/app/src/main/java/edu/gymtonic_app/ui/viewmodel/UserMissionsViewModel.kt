package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteDatasource.user.UserMissionsRemoteDatasource
import edu.gymtonic_app.data.repository.UserMissionsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeeklyGoalUi(
	val userMissionId: Int,
	val missionId: Int,
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

data class UserMissionsUiState(
	val goals: List<WeeklyGoalUi> = emptyList(),
	val calendarDays: List<CalendarDayUi> = emptyList(),
	val achievedCount: Int = 0,
	val totalCount: Int = 0,
	val isRefreshing: Boolean = false,
	val errorMessage: String? = null
)

class UserMissionsViewModel(application: Application) : AndroidViewModel(application) {
	private val userMissionsRemoteDataSource : UserMissionsRemoteDatasource
	private val userMissionsRepository : UserMissionsRepository

	private val _uiState = MutableStateFlow(UserMissionsUiState())
	val uiState: StateFlow<UserMissionsUiState> = _uiState.asStateFlow()

	init {
		userMissionsRemoteDataSource = UserMissionsRemoteDatasource()
		userMissionsRepository = UserMissionsRepository(userMissionsRemoteDataSource)
		loadUserMissions()
	}

	fun refreshUserMissions() {
		viewModelScope.launch {
			_uiState.update { it.copy(isRefreshing = true) }
			delay(700)
			loadUserMissions()
		}
	}

	private fun loadUserMissions() {
		viewModelScope.launch {
			val userMissionsResult = userMissionsRepository.getUserMissions()
			val missionsDetailsResult = userMissionsRepository.getMissions()
			val calendarResult = userMissionsRepository.getWeeklyCalendarDays()

			userMissionsResult
				.onSuccess { userMissions ->
					missionsDetailsResult
						.onSuccess { missionDetails ->
							// Mapear UserMissionDto + MissionDto → WeeklyGoalUi
							val mappedGoals = userMissions.mapNotNull { userMission ->
								val missionDetail = missionDetails.find { it.missionId == userMission.missionId }
								if (missionDetail != null) {
									val progressFloat = if (missionDetail.missionObjective > 0) {
										(userMission.progress.toFloat() / missionDetail.missionObjective.toFloat())
											.coerceIn(0f, 1f)
									} else {
										0f
									}

									WeeklyGoalUi(
										userMissionId = userMission.userMissionId,
										missionId = userMission.missionId,
										title = missionDetail.missionName,
										progressLabel = "${userMission.progress} de ${missionDetail.missionObjective}",
										pointsLabel = "${missionDetail.missionPoints} pts",
										progress = progressFloat
									)
								} else {
									null
								}
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
									achievedCount = achievedCount,
									totalCount = mappedGoals.size,
									isRefreshing = false,
									errorMessage = null
								)
							}
						}
						.onFailure { error ->
							_uiState.update {
								it.copy(
									isRefreshing = false,
									errorMessage = error.message ?: "No se pudieron cargar los detalles de misiones"
								)
							}
						}
				}
				.onFailure { error ->
					_uiState.update {
						it.copy(
							isRefreshing = false,
							errorMessage = error.message ?: "No se pudieron cargar las misiones del usuario"
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