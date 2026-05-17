package edu.gymtonic_app.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDto
import edu.gymtonic_app.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminRoutinesViewModel : ViewModel() {
    private val repository = AdminRepository()

    private val _listState = MutableStateFlow(AdminListUiState<RoutineDto>())
    val listState: StateFlow<AdminListUiState<RoutineDto>> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(AdminDetailUiState<RoutineDetailDto>())
    val detailState: StateFlow<AdminDetailUiState<RoutineDetailDto>> = _detailState.asStateFlow()

    fun loadList() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            repository.fetchRoutines()
                .onSuccess { routines ->
                    _listState.update { it.copy(items = routines, isLoading = false) }
                }
                .onFailure { e ->
                    _listState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            repository.fetchRoutine(id)
                .onSuccess { routine ->
                    _detailState.update { it.copy(item = routine, isLoading = false) }
                }
                .onFailure { e ->
                    _detailState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun createRoutine(name: String, exerciseIds: List<Int>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _detailState.update { it.copy(isSaving = true, error = null) }
            repository.createRoutine(name, exerciseIds)
                .onSuccess {
                    _detailState.update { it.copy(isSaving = false, message = "created") }
                    loadList()
                    onSuccess()
                }
                .onFailure { e ->
                    _detailState.update { it.copy(isSaving = false, error = e.message) }
                }
        }
    }

    fun updateRoutine(id: Int, name: String, exerciseIds: List<Int>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _detailState.update { it.copy(isSaving = true, error = null) }
            repository.updateRoutine(id, name, exerciseIds)
                .onSuccess {
                    _detailState.update { it.copy(isSaving = false, message = "saved") }
                    loadDetail(id)
                    onSuccess()
                }
                .onFailure { e ->
                    _detailState.update { it.copy(isSaving = false, error = e.message) }
                }
        }
    }

    fun deleteRoutine(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteRoutine(id)
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
