package edu.gymtonic_app.ui.viewmodel.exercise

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.local.GymTonicDatabase
import edu.gymtonic_app.data.local.localDatasource.exercise.ExerciseLocalDataSource
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.remote.remoteDatasource.ExerciseRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseDto
import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseRequest
import edu.gymtonic_app.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoriteExercisePayload(
    val id: Int,
    val name: String,
    val description: String = "",
    val type: Int = 0,
    val video: String? = null,
    val image: String? = null
)

sealed class ExerciseUiState {
    object Idle : ExerciseUiState()
    object Loading : ExerciseUiState()
    data class Success(val exercise: ExerciseDto) : ExerciseUiState()
    data class Error(val message: String) : ExerciseUiState()
}

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    private val exerciseRepository: ExerciseRepository
    private val exerciseRemoteDataSource: ExerciseRemoteDataSource
    private val exerciseLocalDataSource: ExerciseLocalDataSource

    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Idle)
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    private val _favoritesSet = MutableStateFlow<Set<Int>>(emptySet())
    val favoritesSet: StateFlow<Set<Int>> = _favoritesSet.asStateFlow()

    private val _favoriteExercises = MutableStateFlow<List<ExerciseEntity>>(emptyList())
    val favoriteExercises: StateFlow<List<ExerciseEntity>> = _favoriteExercises.asStateFlow()

    init {
        val database = GymTonicDatabase.getInstance(application)
        val dao = database.exerciseDao()

        exerciseRemoteDataSource = ExerciseRemoteDataSource()
        exerciseLocalDataSource = ExerciseLocalDataSource(dao)
        exerciseRepository = ExerciseRepository(exerciseRemoteDataSource, exerciseLocalDataSource)

        observeFavoritesFromRoom()
    }

    private fun observeFavoritesFromRoom() {
        viewModelScope.launch {
            exerciseRepository.getFavExercises().collect { favorites ->
                _favoritesSet.value = favorites.map { it.exercise_id }.toSet()
                _favoriteExercises.value = favorites
            }
        }
    }

    fun isFavorite(exerciseId: Int): Boolean {
        return _favoritesSet.value.contains(exerciseId)
    }

    fun onToggleFavorite(payload: FavoriteExercisePayload) {
        val previousFavorites = _favoritesSet.value
        val optimistic =
            if (previousFavorites.contains(payload.id)) previousFavorites - payload.id
            else previousFavorites + payload.id

        _favoritesSet.value = optimistic

        val entity = ExerciseEntity(
            exercise_id = payload.id,
            exercise_name = payload.name,
            exercise_description = payload.description.ifBlank { "Sin descripción" },
            exercise_type = payload.type,
            exercise_video = payload.video,
            exercise_image = payload.image,
            is_favorite = true
        )

        viewModelScope.launch {
            runCatching {
                exerciseRepository.updateFavWord(entity)
            }.onFailure { error ->
                Log.e(TAG, "Error al alternar favorito para exerciseId=${payload.id}", error)
                _favoritesSet.value = previousFavorites
            }
        }
    }

    fun loadSpecificExercise(exerciseId: String) {
        val idInt = exerciseId.toIntOrNull()
        if (idInt == null) {
            _uiState.value = ExerciseUiState.Error("ID de ejercicio invalido")
            return
        }

        viewModelScope.launch {
            _uiState.value = ExerciseUiState.Loading

            exerciseRepository.getExerciseById(idInt)
                .onSuccess { dto ->
                    _uiState.value = ExerciseUiState.Success(dto)
                }
                .onFailure { error ->
                    _uiState.value = ExerciseUiState.Error(
                        error.message ?: "No se pudo cargar el ejercicio"
                    )
                }
        }
    }

    fun loadExercises(
        onSuccess: (List<ExerciseDto>) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            exerciseRepository.getExercises()
                .onSuccess { exercises ->
                    onSuccess(exercises)
                }
                .onFailure { error ->
                    val message = error.message ?: "No se pudieron cargar los ejercicios"
                    _uiState.value = ExerciseUiState.Error(message)
                    onError(message)
                }
        }
    }

    fun loadExercisesByType(
        type: String,
        onSuccess: (List<ExerciseDto>) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            exerciseRepository.getExercisesByType(type)
                .onSuccess { exercises ->
                    onSuccess(exercises)
                }
                .onFailure { error ->
                    val message = error.message ?: "No se pudieron cargar los ejercicios del tipo $type"
                    _uiState.value = ExerciseUiState.Error(message)
                    onError(message)
                }
        }
    }

    fun createExercise(
        request: ExerciseRequest,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            exerciseRepository.createExercise(request)
                .onSuccess { onSuccess() }
                .onFailure { error ->
                    val message = error.message ?: "No se pudo crear el ejercicio"
                    _uiState.value = ExerciseUiState.Error(message)
                    onError(message)
                }
        }
    }

    fun updateExercise(
        id: Int,
        request: ExerciseRequest,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            exerciseRepository.updateExercise(id, request)
                .onSuccess { onSuccess() }
                .onFailure { error ->
                    val message = error.message ?: "No se pudo actualizar el ejercicio"
                    _uiState.value = ExerciseUiState.Error(message)
                    onError(message)
                }
        }
    }

    fun deleteExercise(
        id: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            exerciseRepository.deleteExercise(id)
                .onSuccess { onSuccess() }
                .onFailure { error ->
                    val message = error.message ?: "No se pudo eliminar el ejercicio"
                    _uiState.value = ExerciseUiState.Error(message)
                    onError(message)
                }
        }
    }

    companion object {
        private const val TAG = "ExerciseViewModel"
    }
}
