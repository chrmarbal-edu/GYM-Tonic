package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.local.GymTonicDatabase
import edu.gymtonic_app.data.local.localDatasource.routine.RoutineLocalDataSource
import edu.gymtonic_app.data.local.localModel.rutine.RoutineEntity
import edu.gymtonic_app.data.remote.remoteDatasource.RoutineRemoteDataSource
import edu.gymtonic_app.data.repository.RoutineRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrainingRoutineUi(
    val id: String,
    val title: String,
    val imageKey: String? = null,
    val isLocal: Boolean
)

data class TrainingCategoryUi(
    val id: String,
    val title: String,
    val routines: List<TrainingRoutineUi>
)

data class TrainingUiState(
    val categories: List<TrainingCategoryUi> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

class TrainingScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val routineRepository: RoutineRepository

    private val _uiState = MutableStateFlow(TrainingUiState())
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    private val _remoteCategories = MutableStateFlow<List<TrainingCategoryUi>>(emptyList())
    private val _userRoutineCategory = MutableStateFlow<TrainingCategoryUi?>(null)

    init {
        val database = GymTonicDatabase.getInstance(application)
        val routineDao = database.routineDao()
        val routineLocalDataSource = RoutineLocalDataSource(routineDao)
        routineRepository = RoutineRepository(
            routineRemoteDataSource = RoutineRemoteDataSource(),
            routineLocalDataSource = routineLocalDataSource
        )

        observeUserRoutines()
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
            routineRepository.getRoutineCategoriesFromApi()
                .onSuccess { remoteCategories ->
                    val mappedRemoteCategories = remoteCategories.map { category ->
                        TrainingCategoryUi(
                            id = category.id,
                            title = category.title,
                            routines = category.routines.map { routine ->
                                TrainingRoutineUi(
                                    id = routine.id,
                                    title = routine.title,
                                    imageKey = routine.imageKey,
                                    isLocal = false
                                )
                            }
                        )
                    }

                    _remoteCategories.value = mappedRemoteCategories
                    rebuildCategories()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            errorMessage = error.message ?: "No se pudo cargar entrenamientos"
                        )
                    }
                }
        }
    }

    private fun observeUserRoutines() {
        viewModelScope.launch {
            routineRepository.getUserCreatedRoutines().collect { routineEntities ->
                _userRoutineCategory.value = mapUserRoutinesToCategory(routineEntities)
                rebuildCategories()
            }
        }
    }

    private fun mapUserRoutinesToCategory(routines: List<RoutineEntity>): TrainingCategoryUi? {
        if (routines.isEmpty()) return null

        return TrainingCategoryUi(
            id = "my_routines",
            title = "Mis rutinas",
            routines = routines.map { routine ->
                TrainingRoutineUi(
                    id = routine.routine_id.toString(),
                    title = routine.routine_name,
                    imageKey = routine.imageKey,
                    isLocal = true
                )
            }
        )
    }

    private fun rebuildCategories() {
        val finalCategories = buildList {
            _userRoutineCategory.value?.let { add(it) }
            addAll(_remoteCategories.value)
        }

        _uiState.update {
            it.copy(
                categories = finalCategories,
                isRefreshing = false,
                errorMessage = null
            )
        }
    }
}