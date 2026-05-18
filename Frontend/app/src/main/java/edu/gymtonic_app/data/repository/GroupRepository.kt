package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.remoteDatasource.GroupRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.group.CreateGroupRequest
import edu.gymtonic_app.data.remote.remoteModel.group.CreateGroupRoutineRequest
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import edu.gymtonic_app.data.remote.remoteModel.group.GroupUserDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

class GroupRepository(
	private val groupRemoteDataSource: GroupRemoteDataSource = GroupRemoteDataSource()
) {

	suspend fun getGroups(): Result<List<GroupDto>> {
		return runCatching {
			unwrapList(groupRemoteDataSource.getGroups(), "No se pudieron obtener los grupos")
		}
	}

	suspend fun getUserGroups(): Result<List<GroupDto>> {
		return runCatching {
			unwrapList(groupRemoteDataSource.getMyGroups(), "No se pudieron obtener tus grupos")
		}
	}

	suspend fun getGroupById(id: Int): Result<GroupDto> {
		return runCatching {
			unwrapOne(groupRemoteDataSource.getGroupById(id), "No se pudo obtener el grupo con id=$id")
		}
	}

	suspend fun getGroupMembers(groupId: Int): Result<List<GroupUserDto>> {
		return runCatching {
			unwrapList(
				groupRemoteDataSource.getGroupMembers(groupId),
				"No se pudieron obtener los miembros del grupo"
			)
		}
	}

	suspend fun getGroupRoutines(groupId: Int): Result<List<RoutineDto>> {
		return runCatching {
			unwrapList(
				groupRemoteDataSource.getGroupRoutines(groupId),
				"No se pudieron obtener las rutinas del grupo"
			)
		}
	}

	suspend fun createGroup(name: String, description: String): Result<GroupDto> {
		return runCatching {
			val request = CreateGroupRequest(
				name = name.trim(),
				description = description.trim()
			)
			val response = groupRemoteDataSource.createGroup(request)
			if (!response.isSuccessful) {
				val errorBody = response.errorBody()?.string().orEmpty()
				throw Exception(
					"No se pudo crear el grupo (HTTP ${response.code()}): ${response.message()} $errorBody"
				)
			}
			response.body()?.toGroupDto()
				?: throw Exception("No se pudo crear el grupo (respuesta vacía)")
		}
	}

	suspend fun joinGroup(groupId: Int): Result<GroupUserDto> {
		return runCatching {
			unwrapOne(groupRemoteDataSource.joinGroup(groupId), "No se pudo unir al grupo")
		}
	}

	suspend fun leaveGroup(groupId: Int): Result<Unit> {
		return runCatching {
			val response = groupRemoteDataSource.leaveGroup(groupId)
			if (!response.isSuccessful) {
				val errorBody = response.errorBody()?.string().orEmpty()
				throw Exception("Error al salir del grupo (HTTP ${response.code()}): ${response.message()} $errorBody")
			}
			Unit
		}
	}

	suspend fun addGroupRoutine(
		groupId: Int,
		name: String,
		exerciseIds: List<Int>,
		imageFile: File? = null
	): Result<RoutineDto> {
		return runCatching {
			if (imageFile == null) {
				val request = CreateGroupRoutineRequest(
					name = name.trim(),
					exercise_ids = exerciseIds
				)
				unwrapOne(
					groupRemoteDataSource.addGroupRoutine(groupId, request),
					"No se pudo añadir la rutina al grupo"
				)
			} else {
				val gson = com.google.gson.Gson()
				val exerciseIdsBody = gson.toJson(exerciseIds)
					.toRequestBody("application/json".toMediaTypeOrNull())
				val imagePart = okhttp3.MultipartBody.Part.createFormData(
					"image",
					imageFile.name,
					imageFile.asRequestBody("image/*".toMediaTypeOrNull())
				)
				unwrapOne(
					groupRemoteDataSource.addGroupRoutineMultipart(
						id = groupId,
						name = name.trim().toRequestBody("text/plain".toMediaTypeOrNull()),
						exerciseIds = exerciseIdsBody,
						image = imagePart
					),
					"No se pudo añadir la rutina al grupo"
				)
			}
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
