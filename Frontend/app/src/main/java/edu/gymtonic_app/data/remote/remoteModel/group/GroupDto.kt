package edu.gymtonic_app.data.remote.remoteModel.group

data class GroupDto(
    val group_id: Int,
    val group_name: String? = null,
    val group_description: String? = null,
    val group_image: String? = null,
    val group_points: Int = 0,
    val group_creator_id: Int = 0
)
