package edu.gymtonic_app.ui.viewmodel.routine

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.local.GymTonicDatabase
import edu.gymtonic_app.data.local.localDatasource.routine.RoutineLocalDataSource
import edu.gymtonic_app.data.local.localDatasource.routineExercise.RoutineExerciseLocalDataSource
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.local.localModel.rutine.RoutineEntity
import edu.gymtonic_app.data.local.localModel.routineExercise.RoutineExerciseWithExerciseEntity
import edu.gymtonic_app.data.remote.remoteDatasource.RoutineRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineExerciseDto
import edu.gymtonic_app.data.repository.RoutineExerciseRepository
import edu.gymtonic_app.data.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class RoutineCatalogUiState {
    object Loading : RoutineCatalogUiState()
    data class Success(val routine: RoutineDetailDto) : RoutineCatalogUiState()
    data class Error(
        val message: String,
        val fallbackRoutine: RoutineDetailDto? = null
    ) : RoutineCatalogUiState()
}

sealed class UserRoutinesUiState {
    object Loading : UserRoutinesUiState()
    data class Success(val routines: List<RoutineEntity>) : UserRoutinesUiState()
    data class Error(val message: String) : UserRoutinesUiState()
}

class RoutineCatalogViewModel(application: Application) : AndroidViewModel(application) {

    private  var routineRepository: RoutineRepository
    private  var routineExerciseRepository: RoutineExerciseRepository

    private val _catalogUiState = MutableStateFlow<RoutineCatalogUiState>(RoutineCatalogUiState.Loading)
    val catalogUiState: StateFlow<RoutineCatalogUiState> = _catalogUiState.asStateFlow()

    private val _userRoutinesState = MutableStateFlow<UserRoutinesUiState>(UserRoutinesUiState.Loading)
    val userRoutinesState: StateFlow<UserRoutinesUiState> = _userRoutinesState.asStateFlow()

    init {
        val database = GymTonicDatabase.getInstance(application)

        val routineDao = database.routineDao()
        val routineExerciseDao = database.routineExerciseDao()

        val routineRemoteDataSource = RoutineRemoteDataSource()
        val routineLocalDataSource = RoutineLocalDataSource(routineDao)
        val routineExerciseLocalDataSource = RoutineExerciseLocalDataSource(routineExerciseDao)

        routineRepository = RoutineRepository(routineRemoteDataSource, routineLocalDataSource)
        routineExerciseRepository = RoutineExerciseRepository(routineExerciseLocalDataSource)

        observeUserCreatedRoutines()
    }

    fun loadRoutine(routineId: String, isLocal: Boolean = false) {
        viewModelScope.launch {
            _catalogUiState.value = RoutineCatalogUiState.Loading

            val idInt = routineId.toIntOrNull()

            if (isLocal) {
                if (idInt == null) {
                    _catalogUiState.value = RoutineCatalogUiState.Error(message = "ID de rutina local invalido")
                    return@launch
                }
                routineRepository.getUserRoutineWithExercises(idInt)
                    .onSuccess { routineEntity ->
                        if (routineEntity != null) {
                            try {
                                val exercises = routineExerciseRepository
                                    .getExercisesForRoutine(idInt)
                                    .first()

                                _catalogUiState.value = RoutineCatalogUiState.Success(
                                    mapLocalRoutineToDto(routineEntity, exercises)
                                )
                            } catch (e: Exception) {
                                _catalogUiState.value = RoutineCatalogUiState.Error(
                                    message = e.message ?: "No se pudieron cargar los ejercicios de la rutina local"
                                )
                            }
                        } else {
                            _catalogUiState.value = RoutineCatalogUiState.Error(
                                message = "Rutina local no encontrada (ID=$idInt)"
                            )
                        }
                    }
                    .onFailure { error ->
                        _catalogUiState.value = RoutineCatalogUiState.Error(
                            message = error.message ?: "No se pudo cargar la rutina local"
                        )
                    }

                return@launch
            }

            // API detail
            if (idInt != null) {
                routineRepository.getRoutineWithExercisesByIdFromApi(idInt)
                    .onSuccess { routineDetailDto ->
                        _catalogUiState.value = RoutineCatalogUiState.Success(routineDetailDto)
                    }
                    .onFailure { error ->
                        _catalogUiState.value = RoutineCatalogUiState.Error(
                            message = error.message ?: "No se pudo cargar la rutina"
                        )
                    }
            } else {
                _catalogUiState.value = RoutineCatalogUiState.Error(message = "ID de rutina de API invalido")
            }
        }
    }

    fun loadRoutines() {
        viewModelScope.launch {
            _catalogUiState.value = RoutineCatalogUiState.Loading

            routineRepository.getRoutinesFromApi()
                .onSuccess { routinesData ->
                    val firstRoutineData = routinesData.firstOrNull()
                    if (firstRoutineData != null) {
                        _catalogUiState.value = RoutineCatalogUiState.Success(
                            RoutineDetailDto(
                                routine_id = firstRoutineData.routine_id,
                                routine_name = firstRoutineData.routine_name,
                                exercises = emptyList()
                            )
                        )
                    } else {
                        _catalogUiState.value = RoutineCatalogUiState.Error(
                            message = "No hay rutinas disponibles"
                        )
                    }
                }
                .onFailure { error ->
                    _catalogUiState.value = RoutineCatalogUiState.Error(
                        message = error.message ?: "No se pudo cargar el catalogo"
                    )
                }
        }
    }

    fun createUserRoutineWithExercises(
        routineName: String,
        exercises: List<ExerciseEntity>,
        imageKey: String? = null,
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
            routineRepository.createUserRoutine(routineName, imageKey)
                .onSuccess { routineId ->
                    routineExerciseRepository
                        .addMultipleExercisesToRoutine(routineId.toInt(), exercises)
                        .onSuccess {
                            onSuccess()
                        }
                        .onFailure { relationError ->
                            routineRepository.deleteUserRoutine(routineId.toInt())
                            onError(relationError.message ?: "No se pudieron vincular los ejercicios")
                        }
                }
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
            routineExerciseRepository.deleteAllExercisesForRoutine(routineId)
                .onSuccess {
                    routineRepository.deleteUserRoutine(routineId)
                        .onSuccess {
                            onSuccess()
                        }
                        .onFailure { error ->
                            onError(error.message ?: "No se pudo borrar la rutina")
                        }
                }
                .onFailure { error ->
                    onError(error.message ?: "No se pudieron borrar los ejercicios de la rutina")
                }
        }
    }

    fun getExercisesForUserRoutine(routineId: Int): Flow<List<RoutineExerciseWithExerciseEntity>> {
        return routineExerciseRepository.getExercisesForRoutine(routineId)
    }

    fun removeExerciseFromRoutine(routineId: Int, exerciseId: Int) {
        viewModelScope.launch {
            routineExerciseRepository.removeExerciseFromRoutine(routineId, exerciseId)
        }
    }

    private fun observeUserCreatedRoutines() {
        viewModelScope.launch {
            routineRepository.getUserCreatedRoutines()
                .collect { routines ->
                    _userRoutinesState.value = UserRoutinesUiState.Success(routines)
                }
        }
    }

    private fun mapLocalRoutineToDto(
        routineEntity: RoutineEntity,
        exercises: List<RoutineExerciseWithExerciseEntity>
    ): RoutineDetailDto {
        return RoutineDetailDto(
            routine_id = routineEntity.routine_id,
            routine_name = routineEntity.routine_name,
            routine_image = routineEntity.routine_image,
            exercises = exercises.map { item ->
                RoutineExerciseDto(
                    exercise_id = item.exercise.exercise_id,
                    exercise_name = item.exercise.exercise_name,
                    exercise_description = item.exercise.exercise_description,
                    exercise_type = item.exercise.exercise_type,
                    exercise_video = item.exercise.exercise_video,
                    exercise_image = item.exercise.exercise_image,
                    reps = item.reps
                )
            }
        )
    }
}
