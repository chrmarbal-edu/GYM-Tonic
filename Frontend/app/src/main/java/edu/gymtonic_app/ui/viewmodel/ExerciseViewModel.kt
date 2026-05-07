package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.repository.ExerciseRepository
import edu.gymtonic_app.data.repository.FavoritesRepository
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

sealed class ExerciseUiState {
    object Idle : ExerciseUiState()
    object Loading : ExerciseUiState()
    data class Success(val exercise: ExerciseDetailUi) : ExerciseUiState()
    data class Error(val message: String) : ExerciseUiState()
}

class ExerciseViewModel(
    application: Application,
    private val exerciseRepository: ExerciseRepository = ExerciseRepository(),
    private val favoritesRepository: FavoritesRepository = FavoritesRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Idle)
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    private val _favoritesSet = MutableStateFlow<Set<Int>>(emptySet())
    val favoritesSet: StateFlow<Set<Int>> = _favoritesSet.asStateFlow()

    init {
        viewModelScope.launch {
            favoritesRepository.observeFavoriteIds().collect { favoriteIds ->
                _favoritesSet.value = favoriteIds
            }
        }
    }

    fun loadExercise(exerciseId: String) {
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

    fun isFavorite(exerciseId: String): Boolean {
        val parsedId = exerciseId.toIntOrNull() ?: return false
        return _favoritesSet.value.contains(parsedId)
    }

    fun onToggleFavorite(exerciseId: String) {
        val parsedId = exerciseId.toIntOrNull()
        if (parsedId == null) {
            Log.w(TAG, "No se pudo parsear exerciseId=$exerciseId para toggle de favorito")
            return
        }

        val previous = _favoritesSet.value
        val optimistic = if (previous.contains(parsedId)) {
            previous - parsedId
        } else {
            previous + parsedId
        }

        _favoritesSet.value = optimistic

        viewModelScope.launch {
            runCatching {
                favoritesRepository.toggleFavorite(parsedId)
            }.onFailure { error ->
                Log.e(TAG, "Error al alternar favorito para exerciseId=$exerciseId", error)
                _favoritesSet.value = previous
            }
        }
    }

    companion object {
        private const val TAG = "ExerciseViewModel"
    }
}
