package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import edu.gymtonic_app.data.remote.remoteModel.training.TrainingRoutineDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserSummaryDto
import edu.gymtonic_app.data.repository.FriendsRepository
import edu.gymtonic_app.data.repository.GroupRepository
import edu.gymtonic_app.data.repository.RepositoryProvider
import edu.gymtonic_app.data.repository.RoutineRepository
import edu.gymtonic_app.data.repository.UserMissionsRepository
import edu.gymtonic_app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProfileData(
	val username: String,
	val userPoints: Int,
	val objective: Int,
	val streakLabel: String,
	val recentRoutines: List<TrainingRoutineDto>,
	val groups: List<GroupDto>,
	val friends: List<UserSummaryDto> = emptyList()
)

sealed class ProfileUiState {
	object Loading : ProfileUiState()
	data class Success(val data: ProfileData) : ProfileUiState()
	data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

	private val userMissionsRepository = RepositoryProvider.getUserMissionsRepository(application)
	private val routineRepository = RepositoryProvider.getRoutineRepository(application)
	private val userRepository = RepositoryProvider.getUserRepository(application)

	private val groupRepository = GroupRepository()
	private val friendsRepository = FriendsRepository()
	private val sessionManager = SessionManager(application.sessionDataStore)

	private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
	val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

	init {
		loadProfile()
	}

	fun loadProfile() {
		viewModelScope.launch {
			_uiState.value = ProfileUiState.Loading

			val session = sessionManager.sessionFlow.first()
			val userId = session.userId
			val username = session.username ?: "Usuario"

			var points = 0
			var objective = 0
			if (userId != null) {
				userRepository.getUserById(userId).onSuccess { user ->
					points = user.userPoints
					objective = user.userObjective
				}
			}

			val weekDaysResult = userMissionsRepository.getWeeklyCalendarDays()
			val weekDays = weekDaysResult.getOrElse { emptyList() }
			val streakDone = weekDays.take(7).count { it.didWorkout }
			val streakLabel = "$streakDone/7 Logrados"

			val routinesResult = routineRepository.getRoutineCategoriesFromApi()
			// Ensure personal routines are updated in cache with correct userId
			if (userId != null) {
				routineRepository.getRoutinesFromApi(userId)
			}
			val groupsResult = groupRepository.getUserGroups()
			val friendsResult = if (userId != null) friendsRepository.getFriendsForUser(userId) else null

			val routines = routinesResult.getOrDefault(emptyList())
			val groups = groupsResult.getOrDefault(emptyList())
			val friends = friendsResult?.getOrDefault(emptyList()) ?: emptyList()

			val category = routines.firstOrNull { it.id == "recent" } ?: routines.firstOrNull()
			val recentRoutines = category?.routines.orEmpty().take(3)

			_uiState.value = ProfileUiState.Success(
				ProfileData(
					username = username,
					userPoints = points,
					objective = objective,
					streakLabel = streakLabel,
					recentRoutines = recentRoutines,
					groups = groups,
					friends = friends
				)
			)
		}
	}
}
