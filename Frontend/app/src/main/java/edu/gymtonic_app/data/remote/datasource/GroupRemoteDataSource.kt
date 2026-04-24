package edu.gymtonic_app.data.remote.datasource

import edu.gymtonic_app.data.remote.model.group.GroupDto

class GroupRemoteDataSource {
    fun getUserGroups(userId: Int?): List<GroupDto> {
        val groups = listOf(
            GroupDto(groupId = 1, groupName = "Los Frutis"),
            GroupDto(groupId = 2, groupName = "Cardaduras"),
            GroupDto(groupId = 3, groupName = "Power Team")
        )

        // Mock temporal: si no hay sesion cargada mostramos una lista reducida.
        return if (userId == null) groups.take(2) else groups
    }
}


