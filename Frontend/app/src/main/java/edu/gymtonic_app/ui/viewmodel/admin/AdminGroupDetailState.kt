package edu.gymtonic_app.ui.viewmodel.admin

import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto

data class AdminGroupDetailState(
    val group: GroupDto? = null,
    val members: List<AdminGroupMemberUi> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)
