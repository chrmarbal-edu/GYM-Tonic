package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.local.GymTonicDatabase
import edu.gymtonic_app.data.local.localDatasource.routine.RoutineLocalDataSource
import edu.gymtonic_app.data.remote.remoteDatasource.RoutineRemoteDataSource
import edu.gymtonic_app.data.remote.remoteDatasource.user.UserMissionsRemoteDatasource
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import edu.gymtonic_app.data.remote.remoteModel.training.TrainingRoutineDto
import edu.gymtonic_app.data.repository.GroupRepository
import edu.gymtonic_app.data.repository.RoutineRepository
import edu.gymtonic_app.data.repository.UserMissionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProfileData(
	val username: String,
	val streakLabel: String,
	val recentRoutines: List<TrainingRoutineDto>,
	val groups: List<GroupDto>
)

sealed class ProfileUiState {
	object Loading : ProfileUiState()
	data class Success(val data: ProfileData) : ProfileUiState()
	data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

	private val userMissionsRemoteDataSource : UserMissionsRemoteDatasource
	private val userMissionsRepository : UserMissionsRepository
	private val routineRepository: RoutineRepository
	private val routineRemoteDataSource: RoutineRemoteDataSource

	private val routineLocalDataSource: RoutineLocalDataSource
	private val groupRepository = GroupRepository()
	private val sessionManager = SessionManager(application.sessionDataStore)

	private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
	val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

	init {
		val database = GymTonicDatabase.getInstance(application)
		val dao = database.routineDao()

		userMissionsRemoteDataSource = UserMissionsRemoteDatasource()
		userMissionsRepository = UserMissionsRepository(userMissionsRemoteDataSource)

		routineRemoteDataSource = RoutineRemoteDataSource()
		routineLocalDataSource = RoutineLocalDataSource(dao)
		routineRepository = RoutineRepository(routineRemoteDataSource, routineLocalDataSource)

		loadProfile()
	}

	fun loadProfile() {
		viewModelScope.launch {
			_uiState.value = ProfileUiState.Loading

			val session = sessionManager.sessionFlow.first()
			val username = session.username ?: "Usuario"

			val weekDaysResult = userMissionsRepository.getWeeklyCalendarDays()
			val weekDays = weekDaysResult.getOrElse { emptyList() }
			val streakDone = weekDays.take(7).count { it.didWorkout }
			val streakLabel = "$streakDone/7 Logrados"

			val routinesResult = routineRepository.getRoutineCategoriesFromApi()
			val groupsResult = groupRepository.getUserGroups(session.userId)

			if (routinesResult.isFailure || groupsResult.isFailure) {
				_uiState.value = ProfileUiState.Error(
					message = routinesResult.exceptionOrNull()?.message
						?: groupsResult.exceptionOrNull()?.message
						?: "No se pudo cargar el perfil"
				)
				return@launch
			}

			val category = routinesResult.getOrNull().orEmpty().firstOrNull { it.id == "recent" }
				?: routinesResult.getOrNull().orEmpty().firstOrNull()

			val recentRoutines = category?.routines.orEmpty().take(3)
			val groups = groupsResult.getOrNull().orEmpty()

			_uiState.value = ProfileUiState.Success(
				ProfileData(
					username = username,
					streakLabel = streakLabel,
					recentRoutines = recentRoutines,
					groups = groups
				)
			)
		}
	}
}
