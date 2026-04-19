package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.R
import edu.gymtonic_app.data.repository.TrainingRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrainingRoutineUi(
    val id: String,
    val title: String,
    val imageRes: Int
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
    val trainingRepository: TrainingRepository = TrainingRepository()

    private val _uiState = MutableStateFlow(TrainingUiState())
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    init {
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
            trainingRepository.getTrainingCategories()
                .onSuccess { remoteCategories ->
                    _uiState.update {
                        it.copy(
                            categories = remoteCategories.map { category ->
                                TrainingCategoryUi(
                                    id = category.id,
                                    title = category.title,
                                    routines = category.routines.map { routine ->
                                        TrainingRoutineUi(
                                            id = routine.id,
                                            title = routine.title,
                                            imageRes = imageResFromKey(routine.imageKey)
                                        )
                                    }
                                )
                            },
                            isRefreshing = false,
                            errorMessage = null
                        )
                    }
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

    private fun imageResFromKey(imageKey: String): Int {
        return when (imageKey) {
            "espalda" -> R.drawable.espalda
            "fullbody" -> R.drawable.fullbody
            "pushup" -> R.drawable.pushup
            "estiramientos" -> R.drawable.estiramientos
            "brazo" -> R.drawable.brazo
            "pierna" -> R.drawable.pierna
            else -> R.drawable.fullbody
        }
    }
}
