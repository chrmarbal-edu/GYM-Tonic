package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import edu.gymtonic_app.data.remote.remoteModel.group.GroupUserDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserSummaryDto
import edu.gymtonic_app.data.repository.GroupRepository
import edu.gymtonic_app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class GroupsListData(
    val allGroups: List<GroupDto>,
    val myGroupIds: Set<Int>,
    val currentUserId: Int?
)

data class GroupDetailData(
    val group: GroupDto,
    val members: List<GroupUserDto>,
    val routines: List<RoutineDto>,
    val isMember: Boolean,
    val isCreator: Boolean,
    val currentUserId: Int?
)

sealed class GroupsListUiState {
    object Loading : GroupsListUiState()
    data class Success(val data: GroupsListData) : GroupsListUiState()
    data class Error(val message: String) : GroupsListUiState()
}

sealed class GroupDetailUiState {
    object Loading : GroupDetailUiState()
    data class Success(val data: GroupDetailData) : GroupDetailUiState()
    data class Error(val message: String) : GroupDetailUiState()
}

class GroupViewModel(application: Application) : AndroidViewModel(application) {

    private val groupRepository = GroupRepository()
    private val userRepository = UserRepository()
    private val sessionManager = SessionManager(application.sessionDataStore)

    private val _listState = MutableStateFlow<GroupsListUiState>(GroupsListUiState.Loading)
    val listState: StateFlow<GroupsListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<GroupDetailUiState>(GroupDetailUiState.Loading)
    val detailState: StateFlow<GroupDetailUiState> = _detailState.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    fun loadGroupsList() {
        viewModelScope.launch {
            _listState.value = GroupsListUiState.Loading
            val session = sessionManager.sessionFlow.first()
            val userId = session.userId

            val allResult = groupRepository.getGroups()
            val myResult = groupRepository.getUserGroups()

            if (allResult.isFailure || myResult.isFailure) {
                _listState.value = GroupsListUiState.Error(
                    allResult.exceptionOrNull()?.message
                        ?: myResult.exceptionOrNull()?.message
                        ?: "No se pudieron cargar los grupos"
                )
                return@launch
            }

            val myGroupIds = myResult.getOrNull().orEmpty().map { it.group_id }.toSet()
            _listState.value = GroupsListUiState.Success(
                GroupsListData(
                    allGroups = allResult.getOrNull().orEmpty(),
                    myGroupIds = myGroupIds,
                    currentUserId = userId
                )
            )
        }
    }

    fun createGroup(name: String, description: String, onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            val result = groupRepository.createGroup(name, description)
            result.fold(
                onSuccess = { group ->
                    _actionMessage.value = "Grupo creado correctamente"
                    loadGroupsList()
                    onSuccess(group.group_id)
                },
                onFailure = { error ->
                    _actionMessage.value = error.message ?: "No se pudo crear el grupo"
                }
            )
        }
    }

    fun joinGroup(groupId: Int) {
        viewModelScope.launch {
            val result = groupRepository.joinGroup(groupId)
            result.fold(
                onSuccess = {
                    _actionMessage.value = "Te has unido al grupo"
                    loadGroupsList()
                    loadGroupDetail(groupId)
                },
                onFailure = { error ->
                    _actionMessage.value = error.message ?: "No se pudo unir al grupo"
                }
            )
        }
    }

    fun loadGroupDetail(groupId: Int) {
        viewModelScope.launch {
            _detailState.value = GroupDetailUiState.Loading
            val session = sessionManager.sessionFlow.first()
            val userId = session.userId

            val groupResult = groupRepository.getGroupById(groupId)
            if (groupResult.isFailure) {
                _detailState.value = GroupDetailUiState.Error(
                    groupResult.exceptionOrNull()?.message ?: "No se pudo cargar el grupo"
                )
                return@launch
            }

            val group = groupResult.getOrThrow()
            val myGroupsResult = groupRepository.getUserGroups()
            val myGroupIds = myGroupsResult.getOrNull().orEmpty().map { it.group_id }.toSet()
            val isMember = myGroupIds.contains(groupId)

            val membersResult = if (isMember) {
                groupRepository.getGroupMembers(groupId)
            } else {
                Result.success(emptyList<GroupUserDto>())
            }

            val usersResult = if (isMember) {
                userRepository.getUsers()
            } else {
                Result.success(emptyList<UserSummaryDto>())
            }

            val members = membersResult.getOrElse { emptyList() }.map { member ->
                val userInfo = usersResult.getOrNull()?.find { it.userId == member.userId }
                member.copy(
                    userUsername = member.userUsername ?: userInfo?.userUsername,
                    userName = member.userName ?: userInfo?.userName,
                    userPicture = member.userPicture ?: userInfo?.userPicture
                )
            }

            val routines = if (isMember) {
                groupRepository.getGroupRoutines(groupId).getOrElse { emptyList() }
            } else {
                emptyList()
            }

            _detailState.value = GroupDetailUiState.Success(
                GroupDetailData(
                    group = group,
                    members = members,
                    routines = routines,
                    isMember = isMember,
                    isCreator = userId != null && group.group_creator_id == userId,
                    currentUserId = userId
                )
            )
        }
    }

    fun leaveGroup(groupId: Int, onLeft: () -> Unit) {
        viewModelScope.launch {
            val result = groupRepository.leaveGroup(groupId)
            result.fold(
                onSuccess = {
                    _actionMessage.value = "Has abandonado el grupo"
                    onLeft()
                },
                onFailure = { error ->
                    _actionMessage.value = error.message ?: "No se pudo salir del grupo"
                }
            )
        }
    }

    fun addGroupRoutine(
        groupId: Int,
        name: String,
        exercises: List<edu.gymtonic_app.data.remote.remoteModel.routine.RoutineExerciseDto>,
        imageFile: java.io.File? = null,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = groupRepository.addGroupRoutine(groupId, name, exercises, imageFile)
            result.fold(
                onSuccess = {
                    _actionMessage.value = "Rutina compartida con el grupo"
                    loadGroupDetail(groupId)
                    onSuccess()
                },
                onFailure = { error ->
                    val message = error.message ?: "No se pudo añadir la rutina"
                    _actionMessage.value = message
                    onFailure(message)
                }
            )
        }
    }
}
