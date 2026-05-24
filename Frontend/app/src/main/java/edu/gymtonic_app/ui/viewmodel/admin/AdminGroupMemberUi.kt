package edu.gymtonic_app.ui.viewmodel.admin

data class AdminGroupMemberUi(
    val userId: Int,
    val range: Int,
    val displayName: String,
    val profilePicture: String? = null,
    val points: Int? = null
)
