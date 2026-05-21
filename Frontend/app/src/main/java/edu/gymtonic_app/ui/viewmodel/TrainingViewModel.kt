package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteDatasource.RoutineRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.training.TrainingCategoryDto
import edu.gymtonic_app.data.remote.remoteModel.training.TrainingRoutineDto
import edu.gymtonic_app.data.repository.GroupRepository
import edu.gymtonic_app.data.repository.RoutineRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrainingUiState(
    val categories: List<TrainingCategoryDto> = emptyList(),
    val groupRoutines: List<TrainingRoutineDto> = emptyList(),
    val personalRoutines: List<TrainingRoutineDto> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

class TrainingScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val routineRepository = RoutineRepository(
        routineRemoteDataSource = RoutineRemoteDataSource(),
        routineLocalDataSource = null
    )
    private val groupRepository = GroupRepository()

    private val _uiState = MutableStateFlow(TrainingUiState())
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    init {
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
            // Load standard categories
            val categoriesResult = routineRepository.getRoutineCategoriesFromApi()
            
            // Load personal routines
            val personalRoutinesResult = routineRepository.getRoutinesFromApi()
            val personalRoutines = personalRoutinesResult.getOrNull()?.map {
                TrainingRoutineDto(
                    routine_id = it.routine_id,
                    routine_name = it.routine_name,
                    routine_image = it.routine_image
                )
            } ?: emptyList()

            // Load group routines
            val groupRoutinesResult = groupRepository.getUserGroups()
            val allGroupRoutines = mutableListOf<TrainingRoutineDto>()
            
            groupRoutinesResult.onSuccess { groups ->
                groups.forEach { group ->
                    groupRepository.getGroupRoutines(group.group_id).onSuccess { routines ->
                        allGroupRoutines.addAll(routines.map { 
                            TrainingRoutineDto(
                                routine_id = it.routine_id,
                                routine_name = it.routine_name,
                                routine_image = it.routine_image
                            )
                        })
                    }
                }
            }

            categoriesResult.onSuccess { categories ->
                _uiState.update {
                    it.copy(
                        categories = categories,
                        groupRoutines = allGroupRoutines.distinctBy { r -> r.routine_id },
                        personalRoutines = personalRoutines,
                        isRefreshing = false,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        errorMessage = error.message ?: "No se pudo cargar entrenamientos"
                    )
                }
            }
        }
    }
}
