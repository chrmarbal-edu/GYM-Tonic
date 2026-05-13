package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.remoteDatasource.GroupRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import retrofit2.Response

class GroupRepository(
	private val groupRemoteDataSource: GroupRemoteDataSource = GroupRemoteDataSource()
) {

	suspend fun getGroups(): Result<List<GroupDto>> {
		return runCatching {
			unwrapList(groupRemoteDataSource.getGroups(), "No se pudieron obtener los grupos")
		}
	}

	suspend fun getUserGroups(userId: Int?): Result<List<GroupDto>> {
		return runCatching {
			val groups = unwrapList(groupRemoteDataSource.getGroups(), "No se pudieron obtener los grupos")

			if (userId == null) {
				groups
			} else {
				groups.filter { it.group_creator_id == userId }
			}
		}
	}

	suspend fun getGroupById(id: Int): Result<GroupDto> {
		return runCatching {
			unwrapOne(groupRemoteDataSource.getGroupById(id), "No se pudo obtener el grupo con id=$id")
		}
	}

	suspend fun createGroup(request: Map<String, Any>): Result<GroupDto> {
		return runCatching {
			unwrapOne(groupRemoteDataSource.createGroup(request), "No se pudo crear el grupo")
		}
	}

	suspend fun updateGroup(id: Int, request: Map<String, Any?>): Result<GroupDto> {
		return runCatching {
			unwrapOne(groupRemoteDataSource.updateGroup(id, request), "No se pudo actualizar el grupo con id=$id")
		}
	}

	suspend fun deleteGroup(id: Int): Result<Unit> {
		return runCatching {
			val response = groupRemoteDataSource.deleteGroup(id)
			if (!response.isSuccessful) {
				val errorBody = response.errorBody()?.string().orEmpty()
				throw Exception("Error al eliminar grupo (HTTP ${response.code()}): ${response.message()} $errorBody")
			}
			Unit
		}
	}

	private fun <T> unwrapOne(response: Response<T>, defaultMessage: String): T {
		if (response.isSuccessful) {
			return response.body() ?: throw Exception("$defaultMessage (body vacío)")
		}

		val errorBody = response.errorBody()?.string().orEmpty()
		throw Exception("$defaultMessage (HTTP ${response.code()}): ${response.message()} $errorBody")
	}

	private fun <T> unwrapList(response: Response<List<T>>, defaultMessage: String): List<T> {
		if (response.isSuccessful) {
			return response.body() ?: emptyList()
		}

		val errorBody = response.errorBody()?.string().orEmpty()
		throw Exception("$defaultMessage (HTTP ${response.code()}): ${response.message()} $errorBody")
	}
}