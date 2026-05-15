package edu.gymtonic_app.data.remote.remoteDatasource

import edu.gymtonic_app.data.remote.remoteModel.group.CreateGroupRequest
import edu.gymtonic_app.data.remote.remoteModel.group.CreateGroupRoutineRequest
import edu.gymtonic_app.data.remote.services.RetrofitClient

class GroupRemoteDataSource {
    private val api = RetrofitClient.apiService

    suspend fun getGroups() = api.getGroups()

    suspend fun getMyGroups() = api.getMyGroups()

    suspend fun getGroupById(id: Int) = api.getGroupById(id)

    suspend fun getGroupMembers(id: Int) = api.getGroupMembers(id)

    suspend fun getGroupRoutines(id: Int) = api.getGroupRoutines(id)

    suspend fun createGroup(request: CreateGroupRequest) = api.createGroup(request)

    suspend fun joinGroup(id: Int) = api.joinGroup(id)

    suspend fun leaveGroup(id: Int) = api.leaveGroup(id)

    suspend fun addGroupRoutine(id: Int, request: CreateGroupRoutineRequest) =
        api.addGroupRoutine(id, request)

    suspend fun updateGroup(id: Int, request: Map<String, Any?>) = api.updateGroup(id, request)

    suspend fun deleteGroup(id: Int) = api.deleteGroup(id)
}
