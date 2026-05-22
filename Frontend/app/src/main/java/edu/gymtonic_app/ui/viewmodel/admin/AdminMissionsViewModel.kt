package edu.gymtonic_app.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.mission.MissionDto
import edu.gymtonic_app.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminMissionsViewModel : ViewModel() {
    private val repository = AdminRepository()

    private val _listState = MutableStateFlow(AdminListUiState<MissionDto>())
    val listState: StateFlow<AdminListUiState<MissionDto>> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(AdminDetailUiState<MissionDto>())
    val detailState: StateFlow<AdminDetailUiState<MissionDto>> = _detailState.asStateFlow()

    fun loadList() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            repository.fetchMissions()
                .onSuccess { missions ->
                    _listState.update { it.copy(items = missions, isLoading = false) }
                }
                .onFailure { e ->
                    _listState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            repository.fetchMission(id)
                .onSuccess { mission ->
                    _detailState.update { it.copy(item = mission, isLoading = false) }
                }
                .onFailure { e ->
                    _detailState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun saveMission(
        id: Int?,
        name: String,
        type: Int,
        points: Int,
        objective: Int,
        goal: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _detailState.update { it.copy(isSaving = true, error = null) }
            val result = if (id == null) {
                repository.createMission(name, type, points, objective, goal)
            } else {
                repository.updateMission(id, name, type, points, objective, goal)
            }
            result
                .onSuccess { mission ->
                    _detailState.update { it.copy(item = mission, isSaving = false, message = "saved") }
                    loadList()
                    onSuccess()
                }
                .onFailure { e ->
                    _detailState.update { it.copy(isSaving = false, error = e.message) }
                }
        }
    }

    fun deleteMission(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteMission(id)
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
