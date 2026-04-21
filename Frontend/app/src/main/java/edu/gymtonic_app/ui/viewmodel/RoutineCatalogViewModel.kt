package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.R
import edu.gymtonic_app.data.repository.RoutineRepository
import edu.gymtonic_app.data.remote.model.routine.RoutineDetailData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//modelo de UI para un ejercicio (nombre, reps, imagen local).
data class RoutineExerciseUi(
    val id: String,
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
                .onSuccess { routineData ->
                    _uiState.value = RoutineCatalogUiState.Success(mapRoutineDataToUi(routineData))
                }
                .onFailure { error ->
                    val fallback = mapRoutineDataToUi(routineRepository.getRoutineFromMock(routineId))
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
                .onSuccess { routinesData ->
                    val firstRoutineData = routinesData.firstOrNull()
                        ?: routineRepository.getRoutineFromMock("fullbody")
                    _uiState.value = RoutineCatalogUiState.Success(mapRoutineDataToUi(firstRoutineData))
                }
                .onFailure { error ->
                    val fallback = mapRoutineDataToUi(routineRepository.getRoutineFromMock("fullbody"))
                    _uiState.value = RoutineCatalogUiState.Error(
                        message = error.message ?: "No se pudo cargar el catalogo",
                        fallbackRoutine = fallback
                    )
                }
        }
    }

    private fun mapRoutineDataToUi(data: RoutineDetailData): RoutineDetailUi {
        return RoutineDetailUi(
            id = data.id,
            title = data.title,
            exercises = data.exercises.map { exercise ->
                RoutineExerciseUi(
                    id = exercise.id,
                    name = exercise.name,
                    reps = exercise.reps,
                    imageRes = imageResFromKey(exercise.imageKey)
                )
            }
        )
    }

    private fun imageResFromKey(imageKey: String?): Int {
        return when (imageKey) {
            "espalda" -> R.drawable.espalda
            "fullbody" -> R.drawable.fullbody
            "pushup" -> R.drawable.pushup
            "estiramientos" -> R.drawable.estiramientos
            "brazo" -> R.drawable.brazo
            "pierna" -> R.drawable.pierna
            "estocadas" -> R.drawable.estocadas
            "pressbanca" -> R.drawable.pressbanca
            "pullover" -> R.drawable.pullover
            "remo" -> R.drawable.remo
            "sentadilla" -> R.drawable.sentadilla
            "pesomuerto" -> R.drawable.pesomuerto
            else -> R.drawable.fullbody
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