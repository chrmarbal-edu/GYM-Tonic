package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//modelo de UI para un ejercicio (nombre, reps, imagen local).
data class RoutineExerciseUi(
    val name: String,
    val reps: String,
    val imageRes: Int
)
//modelo de UI para el detalle de una rutina (id, titulo, lista de ejercicios).
data class RoutineDetailUi(
    val id: String,
    val title: String,
    val exercises: List<RoutineExerciseUi>
)

sealed class RoutineCatalogUiState {
    object Loading : RoutineCatalogUiState()
    data class Success(val routine: RoutineDetailUi) : RoutineCatalogUiState()
    data class Error(
        val message: String,
        val fallbackRoutine: RoutineDetailUi? = null
    ) : RoutineCatalogUiState()
}

//hardcodeado en memoria, usado como mock mientras no llega la integración real.
class RoutineCatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val routineRepository = RoutineRepository()
    private val _uiState = MutableStateFlow<RoutineCatalogUiState>(RoutineCatalogUiState.Loading)
    val uiState: StateFlow<RoutineCatalogUiState> = _uiState.asStateFlow()

    init {
        loadRoutine("fullbody")
    }

    // Carga remote-first para el detalle de rutina y deja fallback local en caso de error.
    fun loadRoutine(routineId: String) {
        viewModelScope.launch {
            _uiState.value = RoutineCatalogUiState.Loading

            routineRepository.getRoutineByIdFromApi(routineId)
                .onSuccess { routine ->
                    _uiState.value = RoutineCatalogUiState.Success(routine)
                }
                .onFailure { error ->
                    val fallback = routineRepository.getRoutineFromMock(routineId)
                    _uiState.value = RoutineCatalogUiState.Error(
                        message = error.message ?: "No se pudo cargar la rutina",
                        fallbackRoutine = fallback
                    )
                }
        }
    }

    // Carga remote-first para catálogo completo de rutinas; útil para próximas pantallas.
    fun loadRoutines() {
        viewModelScope.launch {
            _uiState.value = RoutineCatalogUiState.Loading

            routineRepository.getRoutinesFromApi()
                .onSuccess { routines ->
                    val firstRoutine = routines.firstOrNull() ?: routineRepository.getRoutineFromMock("fullbody")
                    _uiState.value = RoutineCatalogUiState.Success(firstRoutine)
                }
                .onFailure { error ->
                    val fallback = routineRepository.getRoutineFromMock("fullbody")
                    _uiState.value = RoutineCatalogUiState.Error(
                        message = error.message ?: "No se pudo cargar el catalogo",
                        fallbackRoutine = fallback
                    )
                }
        }
    }

    /**
     * Futuro endpoint: lista de rutinas completas desde API.
     * Se deja comentado hasta que exista endpoint dedicado y DTO de detalle.
     *
     * suspend fun fetchRoutinesFromApi(): Result<List<RoutineDetailUi>> {
     *     return routineRepository.getRoutinesFromApi()
     * }
     */

    /**
     * Futuro endpoint: detalle de rutina por id desde API.
     * Se deja comentado para no afectar el flujo actual hardcodeado.
     *
     * suspend fun fetchRoutineByIdFromApi(routineId: String): Result<RoutineDetailUi> {
     *     return routineRepository.getRoutineByIdFromApi(routineId)
     * }
     */
}