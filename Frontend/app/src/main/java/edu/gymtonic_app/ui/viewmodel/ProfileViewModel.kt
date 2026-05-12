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
import edu.gymtonic_app.data.repository.GroupRepository
import edu.gymtonic_app.data.repository.RoutineRepository
import edu.gymtonic_app.data.repository.UserMissionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProfileRoutineUi(
	val id: String,
	val title: String,
	val imageUrl: String  // URL desde API, no recurso local
)

data class ProfileGroupUi(
	val id: Int,
	val name: String,
	val membersLabel: String
)

data class ProfileSuccessUi(
	val username: String,
	val streakLabel: String,
	val recentRoutines: List<ProfileRoutineUi>,
	val groups: List<ProfileGroupUi>
)

sealed class ProfileUiState {
	object Loading : ProfileUiState()
	data class Success(val data: ProfileSuccessUi) : ProfileUiState()
	data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

	// region Repositorios y DataSources
	private val userMissionsRemoteDataSource : UserMissionsRemoteDatasource
	private val userMissionsRepository : UserMissionsRepository
	private val routineRepository: RoutineRepository
	private val routineRemoteDataSource: RoutineRemoteDataSource

	private val routineLocalDataSource: RoutineLocalDataSource
	private val groupRepository = GroupRepository()  // Si tiene constructor sin params o DI interno
	private val sessionManager = SessionManager(application.sessionDataStore)
	//endregion
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

			// Obtener días de la semana del usuario para calcular streak
			val weekDaysResult = userMissionsRepository.getWeeklyCalendarDays()
			val weekDays = weekDaysResult.getOrElse { emptyList() }
			val streakDone = weekDays.take(7).count { it.didWorkout }
			val streakLabel = "$streakDone/7 Logrados"

			// Obtener categorías de rutinas desde API
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

			// Mapear rutinas desde categorías
			val category = routinesResult.getOrNull().orEmpty().firstOrNull { it.id == "recent" }
				?: routinesResult.getOrNull().orEmpty().firstOrNull()

			val recentRoutines = category?.routines.orEmpty().take(3).map { routine ->
				ProfileRoutineUi(
					id = routine.id,
					title = routine.title,
					imageUrl = routine.imageKey  // Directo de API, sin mapper
				)
			}

			// Mapear grupos
			val groups = groupsResult.getOrNull().orEmpty().map {
				ProfileGroupUi(
					id = it.id,
					name = it.name,
					membersLabel = it.membersLabel
				)
			}

			_uiState.value = ProfileUiState.Success(
				ProfileSuccessUi(
					username = username,
					streakLabel = streakLabel,
					recentRoutines = recentRoutines,
					groups = groups
				)
			)
		}
	}
}