package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.local.GymTonicDatabase
import edu.gymtonic_app.data.local.datasource.local.routine.RoutineLocalDataSource
import edu.gymtonic_app.data.local.datasource.local.routineExercise.RoutineExerciseLocalDataSource
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.local.localModel.RoutineEntity
import edu.gymtonic_app.data.local.localModel.RoutineExerciseWithExerciseEntity
import edu.gymtonic_app.data.remote.datasource.RoutineRemoteDataSource
import edu.gymtonic_app.data.repository.RoutineExerciseRepository
import edu.gymtonic_app.data.repository.RoutineRepository
import edu.gymtonic_app.domain.model.routine.RoutineDetail
import edu.gymtonic_app.ui.mapper.ImageResourceMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class RoutineExerciseUi(
    val id: String,
    val name: String,
    val reps: String,
    val imageRes: Int
)

data class RoutineDetailUi(
    val id: String,
    val title: String,
    val exercises: List<RoutineExerciseUi>
)

data class UserRoutineUi(
    val id: Int,
    val name: String,
    val imageKey: String? = null
)

sealed class RoutineCatalogUiState {
    object Loading : RoutineCatalogUiState()
    data class Success(val routine: RoutineDetailUi) : RoutineCatalogUiState()
    data class Error(
        val message: String,
        val fallbackRoutine: RoutineDetailUi? = null
    ) : RoutineCatalogUiState()
}

sealed class UserRoutinesUiState {
    object Loading : UserRoutinesUiState()
    data class Success(val routines: List<UserRoutineUi>) : UserRoutinesUiState()
    data class Error(val message: String) : UserRoutinesUiState()
}

class RoutineCatalogViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var routineRepository: RoutineRepository
    private lateinit var routineExerciseRepository: RoutineExerciseRepository

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
        if (routineId.isBlank()) {
            _catalogUiState.value = RoutineCatalogUiState.Error(message = "Rutina no valida")
            return
        }

        viewModelScope.launch {
            _catalogUiState.value = RoutineCatalogUiState.Loading

            if (isLocal) {
                val localId = routineId.toIntOrNull()
                if (localId == null) {
                    _catalogUiState.value = RoutineCatalogUiState.Error(
                        message = "ID de rutina local invalido"
                    )
                    return@launch
                }

                routineRepository.getUserRoutineWithExercises(localId)
                    .onSuccess { routineEntity ->
                        if (routineEntity != null) {
                            try {
                                val exercises = routineExerciseRepository
                                    .getExercisesForRoutine(localId)
                                    .first()

                                _catalogUiState.value = RoutineCatalogUiState.Success(
                                    mapLocalRoutineToUi(routineEntity, exercises)
                                )
                                return@launch
                            } catch (e: Exception) {
                                _catalogUiState.value = RoutineCatalogUiState.Error(
                                    message = e.message ?: "No se pudieron cargar los ejercicios de la rutina local"
                                )
                                return@launch
                            }
                        } else {
                            _catalogUiState.value = RoutineCatalogUiState.Error(
                                message = "Rutina local no encontrada"
                            )
                            return@launch
                        }
                    }
                    .onFailure { error ->
                        _catalogUiState.value = RoutineCatalogUiState.Error(
                            message = error.message ?: "No se pudo cargar la rutina local"
                        )
                    }

                return@launch
            }

            routineRepository.getRoutineByIdFromApi(routineId)
                .onSuccess { routineData ->
                    _catalogUiState.value = RoutineCatalogUiState.Success(mapRoutineDataToUi(routineData))
                }
                .onFailure { error ->
                    val fallback = mapRoutineDataToUi(routineRepository.getRoutineFromMock(routineId))
                    _catalogUiState.value = RoutineCatalogUiState.Error(
                        message = error.message ?: "No se pudo cargar la rutina",
                        fallbackRoutine = fallback
                    )
                }
        }
    }

    fun loadRoutines() {
        viewModelScope.launch {
            _catalogUiState.value = RoutineCatalogUiState.Loading

            routineRepository.getRoutinesFromApi()
                .onSuccess { routinesData ->
                    val firstRoutineData = routinesData.firstOrNull()
                        ?: routineRepository.getRoutineFromMock("")
                    _catalogUiState.value = RoutineCatalogUiState.Success(mapRoutineDataToUi(firstRoutineData))
                }
                .onFailure { error ->
                    val fallback = mapRoutineDataToUi(routineRepository.getRoutineFromMock(""))
                    _catalogUiState.value = RoutineCatalogUiState.Error(
                        message = error.message ?: "No se pudo cargar el catalogo",
                        fallbackRoutine = fallback
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
                .map { routineEntities ->
                    routineEntities.map { entity ->
                        UserRoutineUi(
                            id = entity.routine_id,
                            name = entity.routine_name,
                            imageKey = entity.imageKey
                        )
                    }
                }
                .collect { routines ->
                    _userRoutinesState.value = UserRoutinesUiState.Success(routines)
                }
        }
    }

    private fun mapLocalRoutineToUi(
        routineEntity: RoutineEntity,
        exercises: List<RoutineExerciseWithExerciseEntity>
    ): RoutineDetailUi {
        return RoutineDetailUi(
            id = routineEntity.routine_id.toString(),
            title = routineEntity.routine_name,
            exercises = exercises.map { item ->
                RoutineExerciseUi(
                    id = item.exercise.exercise_id.toString(),
                    name = item.exercise.exercise_name,
                    reps = item.reps,
                    imageRes = ImageResourceMapper.fromKey(item.exercise.exercise_image)
                )
            }
        )
    }

    private fun mapRoutineDataToUi(data: RoutineDetail): RoutineDetailUi {
        return RoutineDetailUi(
            id = data.id,
            title = data.title,
            exercises = data.exercises.map { exercise ->
                RoutineExerciseUi(
                    id = exercise.id,
                    name = exercise.name,
                    reps = exercise.reps,
                    imageRes = ImageResourceMapper.fromKey(exercise.imageKey)
                )
            }
        )
    }
}