package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.remoteDatasource.GroupRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.group.CreateGroupRequest
import edu.gymtonic_app.data.remote.remoteModel.group.CreateGroupRoutineRequest
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import edu.gymtonic_app.data.remote.remoteModel.group.GroupUserDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDto
import edu.gymtonic_app.core.network.ErrorManager
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
				throw Exception(ErrorManager.parseResponseError(response))
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
				throw Exception(ErrorManager.parseResponseError(response))
			}
			Unit
		}
	}

	suspend fun addGroupRoutine(
		groupId: Int,
		name: String,
		exercises: List<edu.gymtonic_app.data.remote.remoteModel.routine.RoutineExerciseDto>,
		imageFile: File? = null
	): Result<RoutineDto> {
		return runCatching {
			val gson = com.google.gson.Gson()
			// Enviamos la lista completa de ejercicios con sus series (sets) y repeticiones
			val exercisesJson = gson.toJson(exercises.map {
				mapOf(
					"exercise_id" to it.exercise_id,
					"exerciseId" to it.exercise_id,
					"id" to it.exercise_id,
					"series" to (it.series ?: 0),
					"sets" to (it.series ?: 0),
					"reps" to (it.reps ?: ""),
					"repetitions" to (it.reps ?: "")
				)
			})
			val exercisesBody =
				exercisesJson.toRequestBody("application/json".toMediaTypeOrNull())

			val imagePart = imageFile?.let {
				okhttp3.MultipartBody.Part.createFormData(
					"image",
					it.name,
					it.asRequestBody("image/*".toMediaTypeOrNull())
				)
			}

			unwrapOne(
				groupRemoteDataSource.addGroupRoutineMultipart(
					id = groupId,
					name = name.trim().toRequestBody("text/plain".toMediaTypeOrNull()),
					exercises = exercisesBody,
					image = imagePart
				),
				"No se pudo añadir la rutina al grupo"
			)
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
				throw Exception(ErrorManager.parseResponseError(response))
			}
			Unit
		}
	}

	private fun <T> unwrapOne(response: Response<T>, defaultMessage: String): T {
		if (response.isSuccessful) {
			return response.body() ?: throw Exception("$defaultMessage (body vacío)")
		}
		throw Exception(ErrorManager.parseResponseError(response))
	}

	private fun <T> unwrapList(response: Response<List<T>>, defaultMessage: String): List<T> {
		if (response.isSuccessful) {
			return response.body() ?: emptyList()
		}
		throw Exception(ErrorManager.parseResponseError(response))
	}
}
