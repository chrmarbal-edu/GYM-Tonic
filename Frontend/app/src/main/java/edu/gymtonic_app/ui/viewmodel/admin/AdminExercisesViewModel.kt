package edu.gymtonic_app.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseDto
import edu.gymtonic_app.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class AdminExercisesViewModel : ViewModel() {
    private val repository = AdminRepository()

    private val _listState = MutableStateFlow(AdminListUiState<ExerciseDto>())
    val listState: StateFlow<AdminListUiState<ExerciseDto>> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(AdminDetailUiState<ExerciseDto>())
    val detailState: StateFlow<AdminDetailUiState<ExerciseDto>> = _detailState.asStateFlow()

    fun loadList() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            repository.fetchExercises()
                .onSuccess { items ->
                    _listState.update { it.copy(items = items, isLoading = false) }
                }
                .onFailure { e ->
                    _listState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            repository.fetchExercise(id)
                .onSuccess { exercise ->
                    _detailState.update { it.copy(item = exercise, isLoading = false) }
                }
                .onFailure { e ->
                    _detailState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun saveExercise(
        id: Int?,
        name: String,
        description: String,
        type: Int,
        videoFile: File?,
        imageFile: File?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _detailState.update { it.copy(isSaving = true, error = null) }
            repository.saveExerciseWithFiles(id, name, description, type, videoFile, imageFile)
                .onSuccess { exercise ->
                    _detailState.update { state ->
                        state.copy(item = exercise, isSaving = false, message = "saved")
                    }
                    loadList()
                    onSuccess()
                }
                .onFailure { e ->
                    _detailState.update { it.copy(isSaving = false, error = e.message) }
                }
        }
    }

    fun deleteExercise(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteExercise(id)
                .onSuccess {
                    _detailState.update { it.copy(deleted = true) }
                    loadList()
                    onSuccess()
                }
                .onFailure { e ->
                    _detailState.update { it.copy(error = e.message) }
                }
        }
    }

    fun clearDetail() {
        _detailState.value = AdminDetailUiState()
    }
}
