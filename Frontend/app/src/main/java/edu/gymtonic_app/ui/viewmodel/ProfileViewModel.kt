package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.model.auth.SessionManager
import edu.gymtonic_app.data.remote.model.auth.sessionDataStore
import edu.gymtonic_app.data.repository.GroupRepository
import edu.gymtonic_app.data.repository.TrainingRepository
import edu.gymtonic_app.data.repository.WeekRepository
import edu.gymtonic_app.ui.mapper.ImageResourceMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProfileRoutineUi(
	val id: String,
	val title: String,
	val imageRes: Int
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
	private val weekRepository = WeekRepository()
	private val trainingRepository = TrainingRepository()
	private val groupRepository = GroupRepository()
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
			val username = session.username ?: "Usuario"

			val weekDays = weekRepository.getWeeklyCalendarDays().getOrElse { emptyList() }
			val streakDone = weekDays.take(7).count { it.didWorkout }
			val streakLabel = "$streakDone/7 Logrados"

			val routinesResult = trainingRepository.getTrainingCategories()
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

			val recentRoutines = category?.routines.orEmpty().take(3).map { routine ->
				ProfileRoutineUi(
					id = routine.id,
					title = routine.title,
					imageRes = ImageResourceMapper.fromKey(routine.imageKey)
				)
			}

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

