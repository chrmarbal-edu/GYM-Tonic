package edu.gymtonic_app.data.remote.remoteModel.group

data class CreateGroupRequest(
    val name: String,
    val description: String = ""
)
