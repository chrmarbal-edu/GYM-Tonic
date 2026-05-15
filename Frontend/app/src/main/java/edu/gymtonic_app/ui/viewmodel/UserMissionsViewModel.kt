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
	val dayNumber: Int = 0,
	val status: CalendarDayUiStatus,
	val isToday: Boolean = false
)

data class UserMissionsUiState(
	val goals: List<WeeklyGoalUi> = emptyList(),
	val calendarDays: List<CalendarDayUi> = emptyList(),
	val calendarYear: Int = 0,
	val calendarMonth: Int = 0,
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

			// El calendario siempre se muestra, independientemente del resultado de las misiones
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
			val currentCal = java.util.Calendar.getInstance()
			_uiState.update {
				it.copy(
					calendarDays = normalizeCalendar(mappedCalendar),
					calendarYear = currentCal.get(java.util.Calendar.YEAR),
					calendarMonth = currentCal.get(java.util.Calendar.MONTH) + 1
				)
			}

			userMissionsResult
				.onSuccess { userMissions ->
					missionsDetailsResult
						.onSuccess { missionDetails ->
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
							_uiState.update {
								it.copy(
									goals = mappedGoals,
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
		val cal = java.util.Calendar.getInstance()
		val todayDay = cal.get(java.util.Calendar.DAY_OF_MONTH)
		val daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)

		cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
		// Convert Sunday=1..Saturday=7 to Monday-first offset (0=Mon, 6=Sun)
		val firstDayOffset = (cal.get(java.util.Calendar.DAY_OF_WEEK) - 2 + 7) % 7

		val sortedDays = days.sortedBy { it.dayIndex }
		val result = mutableListOf<CalendarDayUi>()

		repeat(firstDayOffset) {
			result.add(CalendarDayUi(dayIndex = -1, dayNumber = 0, status = CalendarDayUiStatus.PENDING))
		}

		for (dayNum in 1..daysInMonth) {
			val dayIdx = dayNum - 1
			val existing = sortedDays.find { it.dayIndex == dayIdx }
			result.add(CalendarDayUi(
				dayIndex = dayIdx,
				dayNumber = dayNum,
				status = existing?.status ?: CalendarDayUiStatus.PENDING,
				isToday = (dayNum == todayDay)
			))
		}

		val remainder = result.size % 7
		if (remainder != 0) {
			repeat(7 - remainder) {
				result.add(CalendarDayUi(dayIndex = -1, dayNumber = 0, status = CalendarDayUiStatus.PENDING))
			}
		}

		return result
	}
}