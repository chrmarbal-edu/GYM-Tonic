package edu.gymtonic_app.ui.viewmodel.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDto
import edu.gymtonic_app.data.repository.AdminRepository
import edu.gymtonic_app.data.repository.GroupRepository
import edu.gymtonic_app.data.repository.RepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class AdminRoutinesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RepositoryProvider.getAdminRepository(application)
    private val groupRepository = GroupRepository()
    private val sessionManager = SessionManager(application.sessionDataStore)

    private val _listState = MutableStateFlow(AdminListUiState<RoutineDto>())
    val listState: StateFlow<AdminListUiState<RoutineDto>> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(AdminDetailUiState<RoutineDetailDto>())
    val detailState: StateFlow<AdminDetailUiState<RoutineDetailDto>> = _detailState.asStateFlow()

    private val _currentUserId = MutableStateFlow<Int?>(null)
    val currentUserId: StateFlow<Int?> = _currentUserId.asStateFlow()

    private val _userGroupIds = MutableStateFlow<Set<Int>>(emptySet())
    val userGroupIds: StateFlow<Set<Int>> = _userGroupIds.asStateFlow()

    init {
        viewModelScope.launch {
            val session = sessionManager.sessionFlow.first()
            _currentUserId.value = session.userId
            
            groupRepository.getUserGroups().onSuccess { groups ->
                _userGroupIds.value = groups.map { it.group_id }.toSet()
            }
        }
    }

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

    fun saveRoutine(
        id: Int?,
        name: String,
        exercises: List<edu.gymtonic_app.data.remote.remoteModel.routine.RoutineExerciseDto>,
        imageFile: File?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _detailState.update { it.copy(isSaving = true, error = null) }
            val groupId = _detailState.value.item?.routine_groupid
            repository.saveRoutineWithFiles(id, name, exercises, imageFile, groupId)
                .onSuccess { routine ->
                    _detailState.update {
                        it.copy(
                            item = routine,
                            isSaving = false,
                            message = if (id == null) "created" else "saved"
                        )
                    }
                    loadList()
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
