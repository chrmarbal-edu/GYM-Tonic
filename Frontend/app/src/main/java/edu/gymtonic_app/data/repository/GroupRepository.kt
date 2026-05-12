package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.remoteDatasource.GroupRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import retrofit2.Response

data class GroupSummaryData(
	val id: Int,
	val name: String,
	val membersLabel: String
)

class GroupRepository(
	private val groupRemoteDataSource: GroupRemoteDataSource = GroupRemoteDataSource()
) {

	suspend fun getGroups(): Result<List<GroupSummaryData>> {
		return runCatching {
			val groups = unwrapList(groupRemoteDataSource.getGroups(), "No se pudieron obtener los grupos")
			groups.map(::mapDtoToData)
		}
	}

	suspend fun getUserGroups(userId: Int?): Result<List<GroupSummaryData>> {
		return runCatching {
			val groups = unwrapList(groupRemoteDataSource.getGroups(), "No se pudieron obtener los grupos")

			val filtered = if (userId == null) {
				groups
			} else {
				groups.filter { it.groupCreatorId == userId }
			}

			filtered.map(::mapDtoToData)
		}
	}

	suspend fun getGroupById(id: String): Result<GroupSummaryData> {
		return runCatching {
			val dto = unwrapOne(groupRemoteDataSource.getGroupById(id), "No se pudo obtener el grupo con id=$id")
			mapDtoToData(dto)
		}
	}

	suspend fun createGroup(request: Map<String, Any>): Result<GroupSummaryData> {
		return runCatching {
			val dto = unwrapOne(groupRemoteDataSource.createGroup(request), "No se pudo crear el grupo")
			mapDtoToData(dto)
		}
	}

	suspend fun updateGroup(id: String, request: Map<String, Any?>): Result<GroupSummaryData> {
		return runCatching {
			val dto = unwrapOne(groupRemoteDataSource.updateGroup(id, request), "No se pudo actualizar el grupo con id=$id")
			mapDtoToData(dto)
		}
	}

	suspend fun deleteGroup(id: String): Result<Unit> {
		return runCatching {
			val response = groupRemoteDataSource.deleteGroup(id)
			if (!response.isSuccessful) {
				val errorBody = response.errorBody()?.string().orEmpty()
				throw Exception("Error al eliminar grupo (HTTP ${response.code()}): ${response.message()} $errorBody")
			}
			Unit
		}
	}

	private fun mapDtoToData(dto: GroupDto): GroupSummaryData {
		// Si backend no expone número de miembros, evita hardcode falso.
		val membersLabel = "Miembros: N/D"

		return GroupSummaryData(
			id = dto.groupId,
			name = dto.groupName,
			membersLabel = membersLabel
		)
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