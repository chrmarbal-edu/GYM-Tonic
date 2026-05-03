package edu.gymtonic_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

sealed class ExerciseUiState {
	object Idle : ExerciseUiState()
	object Loading : ExerciseUiState()
	data class Success(val exercise: ExerciseDetailUi) : ExerciseUiState()
	data class Error(val message: String) : ExerciseUiState()
}

class ExerciseViewModel(
	private val exerciseRepository: ExerciseRepository = ExerciseRepository()
) : ViewModel() {

	private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Idle)
	val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

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

}
