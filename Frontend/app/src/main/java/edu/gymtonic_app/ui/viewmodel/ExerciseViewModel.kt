package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.local.GymTonicDatabase
import edu.gymtonic_app.data.local.datasource.local.exercise.ExerciseLocalDataSource
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.remote.datasource.ExerciseRemoteDataSource
import edu.gymtonic_app.data.repository.ExerciseRepository
import edu.gymtonic_app.ui.mapper.ImageResourceMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExerciseDetailUi(
    val id: String,
    val name: String,
    val durationSeconds: Int,
    val imageRes: Int,
    val instructions: List<String>
)

data class FavoriteExercisePayload(
    val id: String,
    val name: String,
    val description: String = "",
    val type: Int = 0,
    val video: String? = null,
    val image: String? = null
)

sealed class ExerciseUiState {
    object Idle : ExerciseUiState()
    object Loading : ExerciseUiState()
    data class Success(val exercise: ExerciseDetailUi) : ExerciseUiState()
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

    fun isFavorite(exerciseId: String): Boolean {
        val parsedId = exerciseId.toIntOrNull() ?: return false
        return _favoritesSet.value.contains(parsedId)
    }

    fun onToggleFavorite(payload: FavoriteExercisePayload) {
        val parsedId = payload.id.toIntOrNull()
        if (parsedId == null) {
            Log.w(TAG, "No se pudo parsear exerciseId=${payload.id} para toggle de favorito")
            return
        }

        val previousFavorites = _favoritesSet.value
        val optimistic =
            if (previousFavorites.contains(parsedId)) previousFavorites - parsedId
            else previousFavorites + parsedId

        _favoritesSet.value = optimistic

        val entity = ExerciseEntity(
            exercise_id = parsedId,
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
        viewModelScope.launch {
            _uiState.value = ExerciseUiState.Loading

            exerciseRepository.getExerciseById(exerciseId)
                .onSuccess { detail ->
                    _uiState.value = ExerciseUiState.Success(
                        ExerciseDetailUi(
                            id = detail.id,
                            name = detail.name,
                            durationSeconds = detail.durationSeconds,
                            imageRes = ImageResourceMapper.fromKey(detail.imageKey),
                            instructions = detail.instructions
                        )
                    )
                }
                .onFailure { error ->
                    _uiState.value = ExerciseUiState.Error(
                        error.message ?: "No se pudo cargar el ejercicio"
                    )
                }
        }
    }

    companion object {
        private const val TAG = "ExerciseViewModel"
    }
}