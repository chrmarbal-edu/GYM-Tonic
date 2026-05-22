package edu.gymtonic_app.ui.viewmodel.routine

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDetailDto
import edu.gymtonic_app.data.repository.RepositoryProvider
import edu.gymtonic_app.data.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

sealed class RoutineCatalogUiState {
    object Loading : RoutineCatalogUiState()
    data class Success(val routine: RoutineDetailDto) : RoutineCatalogUiState()
    data class Error(
        val message: String,
        val fallbackRoutine: RoutineDetailDto? = null
    ) : RoutineCatalogUiState()
}

class RoutineCatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val routineRepository = RepositoryProvider.getRoutineRepository(application)

    private val _catalogUiState = MutableStateFlow<RoutineCatalogUiState>(RoutineCatalogUiState.Loading)
    val catalogUiState: StateFlow<RoutineCatalogUiState> = _catalogUiState.asStateFlow()

    fun loadRoutine(routineId: String, @Suppress("UNUSED_PARAMETER") isLocal: Boolean = false) {
        viewModelScope.launch {
            _catalogUiState.value = RoutineCatalogUiState.Loading

            val idInt = routineId.toIntOrNull()
            if (idInt == null) {
                _catalogUiState.value = RoutineCatalogUiState.Error(message = "ID de rutina inválido")
                return@launch
            }

            val sessionManager = SessionManager(getApplication<Application>().sessionDataStore)
            val userId = sessionManager.sessionFlow.first().userId

            routineRepository.getRoutineWithExercisesByIdFromApi(idInt, userId)
                .onSuccess { routineDetailDto ->
                    _catalogUiState.value = RoutineCatalogUiState.Success(routineDetailDto)
                }
                .onFailure { error ->
                    _catalogUiState.value = RoutineCatalogUiState.Error(
                        message = error.message ?: "No se pudo cargar la rutina"
                    )
                }
        }
    }

    fun createUserRoutineWithExercises(
        routineName: String,
        exercises: List<edu.gymtonic_app.data.remote.remoteModel.routine.RoutineExerciseDto>,
        imageFile: File? = null,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (routineName.isBlank()) {
            onError("El nombre de la rutina no puede estar vacío")
            return
        }

        if (exercises.isEmpty()) {
            onError("Debes añadir al menos un ejercicio")
            return
        }

        viewModelScope.launch {
            routineRepository.saveRoutineWithFiles(
                id = null,
                name = routineName,
                exercises = exercises,
                imageFile = imageFile,
                isPersonal = true
            )
                .onSuccess { onSuccess() }
                .onFailure { error ->
                    onError(error.message ?: "No se pudo crear la rutina")
                }
        }
    }

    fun deleteUserRoutine(
        routineId: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            routineRepository.deleteRoutineFromApi(routineId)
                .onSuccess { onSuccess() }
                .onFailure { error ->
                    onError(error.message ?: "No se pudo borrar la rutina")
                }
        }
    }
}
