package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteDatasource.RoutineRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.training.TrainingCategoryDto
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.remote.remoteModel.training.TrainingRoutineDto
import edu.gymtonic_app.data.repository.GroupRepository
import edu.gymtonic_app.data.repository.RepositoryProvider
import edu.gymtonic_app.data.repository.RoutineRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrainingUiState(
    val categories: List<TrainingCategoryDto> = emptyList(),
    val recentRoutines: List<TrainingRoutineDto> = emptyList(),
    val personalRoutinesFromCategory: List<TrainingRoutineDto> = emptyList(),
    val groupRoutines: List<TrainingRoutineDto> = emptyList(),
    val allRoutines: List<TrainingRoutineDto> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

class TrainingScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val routineRepository = RepositoryProvider.getRoutineRepository(application)
    private val groupRepository = GroupRepository()

    private val _uiState = MutableStateFlow(TrainingUiState())
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    init {
        // Observe recent routines automatically for real-time updates
        viewModelScope.launch {
            routineRepository.getRecentRoutines().collect { entities ->
                val recent = entities.filter { it.last_visited > 0 }.take(3).map {
                    TrainingRoutineDto(
                        routine_id = it.routine_id,
                        routine_name = it.routine_name,
                        routine_image = it.routine_image
                    )
                }
                _uiState.update { it.copy(recentRoutines = recent) }
            }
        }
        loadCategories()
    }

    fun refreshCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            delay(700)
            loadCategories()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val sessionManager = edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager(getApplication<Application>().sessionDataStore)
            val userId = sessionManager.sessionFlow.first().userId

            // Load categories and find "Mis rutinas" de verdad
            val categoriesResult = routineRepository.getRoutineCategoriesFromApi()
            var personalRoutinesFromCategory = emptyList<TrainingRoutineDto>()
            
            categoriesResult.onSuccess { categories ->
                personalRoutinesFromCategory = categories
                    .find { it.title.equals("Mis rutinas", ignoreCase = true) }
                    ?.routines ?: emptyList()
            }
            
            // Load global routines (labeled as "Todas")
            val allRoutinesResult = routineRepository.getRoutinesFromApi(userId)
            val userGroupsResult = groupRepository.getUserGroups()
            val userGroupIds = userGroupsResult.getOrNull()?.map { it.group_id }?.toSet() ?: emptySet()

            val allRaw = allRoutinesResult.getOrNull() ?: emptyList()

            val groupRoutines = allRaw.filter { routine ->
                routine.routine_groupid != null && userGroupIds.contains(routine.routine_groupid)
            }.map {
                TrainingRoutineDto(
                    routine_id = it.routine_id,
                    routine_name = it.routine_name,
                    routine_image = it.routine_image,
                    routine_creator_id = it.routine_creator_id,
                    routine_groupid = it.routine_groupid
                )
            }

            val allRoutines = allRaw.filter { routine ->
                val isPredefined = routine.routine_creator_id == null
                val isCreator = routine.routine_creator_id == userId
                // We show predefined and creator routines in "Todas"
                // Group routines are separated now
                isPredefined || isCreator
            }.map {
                TrainingRoutineDto(
                    routine_id = it.routine_id,
                    routine_name = it.routine_name,
                    routine_image = it.routine_image,
                    routine_creator_id = it.routine_creator_id,
                    routine_groupid = it.routine_groupid
                )
            }

            _uiState.update {
                it.copy(
                    categories = emptyList(), // We handle sections manually in the screen
                    allRoutines = allRoutines,
                    groupRoutines = groupRoutines,
                    personalRoutinesFromCategory = personalRoutinesFromCategory,
                    isRefreshing = false,
                    errorMessage = null
                )
            }

            if (categoriesResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        errorMessage = categoriesResult.exceptionOrNull()?.message ?: "Error"
                    )
                }
            }
        }
    }
}
