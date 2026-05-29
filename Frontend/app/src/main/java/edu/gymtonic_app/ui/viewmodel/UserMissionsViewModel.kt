package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.repository.RepositoryProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeeklyGoalUi(
	val userMissionId: Int,
	val missionId: Int,
	val title: String?,
	val progressLabel: String,
	val pointsLabel: String,
	val progress: Float,
	val progressValue: Int = 0,
	val objectiveValue: Int = 0,
	val isExpired: Boolean = false,
	val isCompleted: Boolean = false
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

data class NotificationUi(
	val message: String,
	val missionName: String,
	val isExpired: Boolean
)

data class UserMissionsUiState(
	val goals: List<WeeklyGoalUi> = emptyList(),
	val expiredGoals: List<WeeklyGoalUi> = emptyList(),
	val notifications: List<NotificationUi> = emptyList(),
	val calendarDays: List<CalendarDayUi> = emptyList(),
	val calendarYear: Int = 0,
	val calendarMonth: Int = 0,
	val achievedCount: Int = 0,
	val totalCount: Int = 0,
	val isRefreshing: Boolean = false,
	val isLoadingAction: Boolean = false,
	val actionError: String? = null,
	val errorMessage: String? = null
)

class UserMissionsViewModel(application: Application) : AndroidViewModel(application) {
	private val userMissionsRepository = RepositoryProvider.getUserMissionsRepository(application)
	private val sessionManager = SessionManager(application.sessionDataStore)

	private val _uiState = MutableStateFlow(UserMissionsUiState())
	val uiState: StateFlow<UserMissionsUiState> = _uiState.asStateFlow()

	init {
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
			val userId = sessionManager.sessionFlow.first().userId
			if (userId == null) {
				// No disparamos error con mensaje para evitar Toasts durante logout/delete
				_uiState.update { it.copy(isRefreshing = false) }
				return@launch
			}

			val userMissionsResult = userMissionsRepository.getUserMissionByUserId(userId)
			val missionsDetailsResult = userMissionsRepository.getMissions()

			val userMissionsResponse = userMissionsResult.getOrNull()
			val missionDetails = missionsDetailsResult.getOrDefault(emptyList())
			
			if (userMissionsResponse == null) {
				_uiState.update {
					it.copy(
						isRefreshing = false,
						errorMessage = userMissionsResult.exceptionOrNull()?.message ?: "Error al cargar misiones"
					)
				}
				return@launch
			}

			val currentCal = java.util.Calendar.getInstance()
			val currentYear = currentCal.get(java.util.Calendar.YEAR)
			val currentMonth = currentCal.get(java.util.Calendar.MONTH) + 1

			// Unir todas las misiones para calcular estados del calendario
			val allMissions = userMissionsResponse.missions + userMissionsResponse.expiredMissions
			val calendarStatuses = calculateCalendarStatuses(allMissions, currentYear, currentMonth)

			val mappedCalendar = (0 until 31).map { dayIdx ->
				CalendarDayUi(
					dayIndex = dayIdx,
					status = calendarStatuses[dayIdx] ?: CalendarDayUiStatus.PENDING
				)
			}

			// Procesar misiones activas
			val mappedGoals = userMissionsResponse.missions.mapNotNull { userMission ->
				val missionDetail = missionDetails.find { it.missionId == userMission.missionId }
				if (missionDetail != null) {
					val goalValue = userMission.missionGoal ?: missionDetail.missionGoal ?: missionDetail.missionObjective
					val progressFloat = if (goalValue > 0) {
						(userMission.progress.toFloat() / goalValue.toFloat())
							.coerceIn(0f, 1f)
					} else {
						0f
					}
					WeeklyGoalUi(
						userMissionId = userMission.userMissionId,
						missionId = userMission.missionId,
						title = missionDetail.missionName,
						progressLabel = "${userMission.progress} de $goalValue",
						pointsLabel = "${missionDetail.missionPoints} pts",
						progress = progressFloat,
						progressValue = userMission.progress,
						objectiveValue = goalValue,
						isExpired = userMission.expired,
						isCompleted = userMission.completed
					)
				} else {
					null
				}
			}

			// Procesar misiones expiradas
			val mappedExpiredGoals = userMissionsResponse.expiredMissions.mapNotNull { userMission ->
				val missionDetail = missionDetails.find { it.missionId == userMission.missionId }
				if (missionDetail != null) {
					val goalValue = userMission.missionGoal ?: missionDetail.missionGoal ?: missionDetail.missionObjective
					val progressFloat = if (goalValue > 0) {
						(userMission.progress.toFloat() / goalValue.toFloat())
							.coerceIn(0f, 1f)
					} else {
						0f
					}
					WeeklyGoalUi(
						userMissionId = userMission.userMissionId,
						missionId = userMission.missionId,
						title = missionDetail.missionName,
						progressLabel = "${userMission.progress} de $goalValue",
						pointsLabel = "${missionDetail.missionPoints} pts",
						progress = progressFloat,
						progressValue = userMission.progress,
						objectiveValue = goalValue,
						isExpired = true,
						isCompleted = userMission.completed
					)
				} else {
					null
				}
			}

			// Convertir notificaciones
			val notifications = userMissionsResponse.notifications.map { notif ->
				NotificationUi(
					message = notif.message,
					missionName = notif.missionName,
					isExpired = notif.expired
				)
			}

			// Mostrar en "goals" tanto las activas como las expiradas pero completadas
			val visibleGoals = mappedGoals.filter { !it.isExpired } + 
							 mappedExpiredGoals.filter { it.isCompleted }

			val achievedCount = (mappedGoals + mappedExpiredGoals).count { it.isCompleted }
			val totalCount = (mappedGoals + mappedExpiredGoals).size

			_uiState.update {
				it.copy(
					goals = visibleGoals.sortedBy { g -> if (g.isCompleted) 1 else 0 },
					expiredGoals = mappedExpiredGoals.filter { !it.isCompleted },
					notifications = notifications,
					achievedCount = achievedCount,
					totalCount = totalCount,
					calendarDays = normalizeCalendar(mappedCalendar),
					calendarYear = currentYear,
					calendarMonth = currentMonth,
					isRefreshing = false,
					errorMessage = null
				)
			}
		}
	}

	fun completeMission(userMissionId: Int) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoadingAction = true, actionError = null) }
			
			userMissionsRepository.completeMission(userMissionId)
				.onSuccess {
					_uiState.update { state ->
						state.copy(isLoadingAction = false)
					}
					// Refrescar misiones
					loadUserMissions()
				}
				.onFailure { error ->
					_uiState.update {
						it.copy(
							isLoadingAction = false,
							actionError = error.message ?: "Error al completar la misión"
						)
					}
				}
		}
	}

	fun updateMissionProgress(userMissionId: Int, progress: Int) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoadingAction = true, actionError = null) }
			
			userMissionsRepository.updateMissionProgress(userMissionId, progress)
				.onSuccess {
					_uiState.update { state ->
						state.copy(isLoadingAction = false)
					}
					// Refrescar misiones
					loadUserMissions()
				}
				.onFailure { error ->
					_uiState.update {
						it.copy(
							isLoadingAction = false,
							actionError = error.message ?: "Error al actualizar el progreso"
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

	private fun calculateCalendarStatuses(
		allMissions: List<edu.gymtonic_app.data.remote.remoteModel.user.UserMissionDto>,
		currentYear: Int,
		currentMonth: Int
	): Map<Int, CalendarDayUiStatus> {
		val statusMap = mutableMapOf<Int, CalendarDayUiStatus>()
		val today = java.util.Calendar.getInstance()

		// Sort: not completed first, so completed ones override them
		val sortedMissions = allMissions.sortedBy { it.completed }

		for (m in sortedMissions) {
			// Strip time component: "2026-05-20T00:00:00.000Z" → "2026-05-20"
			fun parseDatePart(raw: String?): Triple<Int, Int, Int>? {
				if (raw == null) return null
				val parts = raw.substringBefore("T").substringBefore(" ").split("-")
				if (parts.size != 3) return null
				val y = parts[0].toIntOrNull() ?: return null
				val mo = parts[1].toIntOrNull() ?: return null
				val d = parts[2].toIntOrNull() ?: return null
				if (y == 0 || mo == 0 || d == 0) return null
				return Triple(y, mo, d)
			}

			if (m.completed) {
				if (m.completedDate != null) {
					// Use the actual completion date
					val (y, month, d) = parseDatePart(m.completedDate) ?: continue
					if (y == currentYear && month == currentMonth) {
						statusMap[d - 1] = CalendarDayUiStatus.DONE
					}
				} else {
					// Legacy: no completion date stored → mark today
					val todayDay = today.get(java.util.Calendar.DAY_OF_MONTH)
					statusMap[todayDay - 1] = CalendarDayUiStatus.DONE
				}
			} else {
				// Use expiration date to mark missed days
				val (y, month, d) = parseDatePart(m.userMissionExpiration) ?: continue
				val expCal = java.util.Calendar.getInstance().apply {
					set(y, month - 1, d, 23, 59, 59)
					set(java.util.Calendar.MILLISECOND, 999)
				}
				val isExpired = expCal.before(today)
				if (y == currentYear && month == currentMonth && isExpired) {
					if (statusMap[d - 1] != CalendarDayUiStatus.DONE) {
						statusMap[d - 1] = CalendarDayUiStatus.MISSED
					}
				}
			}
		}
		return statusMap
	}
}