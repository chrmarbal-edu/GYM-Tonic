package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.R
import edu.gymtonic_app.data.remote.RemoteDataSource
import edu.gymtonic_app.data.repository.Repository
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
	val isRefreshing: Boolean = false
)

class TrainingScreenViewModel(application: Application) : AndroidViewModel(application) {
	// Igual que HomeViewModel, dejamos la capa de datos preparada para conectar backend real.
	val remoteDataSource: RemoteDataSource = RemoteDataSource()
	val repository: Repository = Repository(remoteDataSource)

	private val _uiState = MutableStateFlow(TrainingUiState())
	val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

	init {
		loadCategories()
	}

	fun refreshCategories() {
		viewModelScope.launch {
			_uiState.update { it.copy(isRefreshing = true) }

			// Simulacion temporal: cuando exista endpoint real, aqui ira la llamada al repo.
			delay(700)
			loadCategories()

			_uiState.update { it.copy(isRefreshing = false) }
		}
	}

	private fun loadCategories() {
		_uiState.update {
			it.copy(categories = trainingCategoriesFromBackendMock())
		}
	}

	private fun trainingCategoriesFromBackendMock(): List<TrainingCategoryUi> {
		// Mock temporal para desacoplar la vista; se reemplaza por datos de backend sin tocar TrainingScreen.
		return listOf(
			TrainingCategoryUi(
				id = "recent",
				title = "Recientes",
				routines = listOf(
					TrainingRoutineUi("back", "Espalda", R.drawable.espalda),
					TrainingRoutineUi("fullbody", "Full Body", R.drawable.fullbody),
					TrainingRoutineUi("push", "Empujes", R.drawable.pushup)
				)
			),
			TrainingCategoryUi(
				id = "beginners",
				title = "Para Principiantes",
				routines = listOf(
					TrainingRoutineUi("stretch", "Estiramientos", R.drawable.estiramientos),
					TrainingRoutineUi("arm", "Brazo", R.drawable.brazo),
					TrainingRoutineUi("calves", "Gemelos", R.drawable.pierna)
				)
			),
			TrainingCategoryUi(
				id = "muscle_groups",
				title = "Por Grupo Muscular",
				routines = listOf(
					TrainingRoutineUi("calves", "Gemelos", R.drawable.pierna),
					TrainingRoutineUi("arm", "Brazo", R.drawable.brazo),
					TrainingRoutineUi("back", "Espalda", R.drawable.espalda)
				)
			),
			TrainingCategoryUi(
				id = "recommended",
				title = "Recomendados",
				routines = listOf(
					TrainingRoutineUi("fullbody", "Full Body", R.drawable.fullbody),
					TrainingRoutineUi("push", "Empujes", R.drawable.pushup),
					TrainingRoutineUi("stretch", "Estiramientos", R.drawable.estiramientos)
				)
			)
		)
	}
}

