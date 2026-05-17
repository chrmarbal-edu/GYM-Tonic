package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseDto
import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseRequest
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import edu.gymtonic_app.data.remote.remoteModel.mission.MissionDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserDto
import edu.gymtonic_app.core.UserRoles
import edu.gymtonic_app.data.remote.remoteModel.group.GroupUserDto
import edu.gymtonic_app.data.remote.services.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AdminRepository {

    private val api = RetrofitClient.apiService

    suspend fun fetchUsers(): Result<List<UserDto>> = runCatching {
        val response = api.getUsersFull()
        if (!response.isSuccessful) throw Exception(response.message() ?: "Error al cargar usuarios")
        response.body()
            .orEmpty()
            .filter { it.userRole == UserRoles.USER }
    }

    suspend fun fetchUser(id: Int): Result<UserDto> = runCatching {
        val response = api.getUserById(id)
        if (response.isSuccessful && response.body() != null) response.body()!!
        else throw Exception(response.message() ?: "Usuario no encontrado")
    }

    suspend fun deleteUser(id: Int): Result<Unit> = runCatching {
        val response = api.deleteUser(id)
        if (!response.isSuccessful) throw Exception(response.message() ?: "No se pudo eliminar")
    }

    suspend fun fetchRoutines(): Result<List<RoutineDto>> = runCatching {
        val response = api.getRoutines()
        if (response.isSuccessful) response.body().orEmpty()
        else throw Exception(response.message() ?: "Error al cargar rutinas")
    }

    suspend fun fetchRoutine(id: Int): Result<RoutineDetailDto> = runCatching {
        val response = api.getRoutineWithExercisesById(id)
        if (response.isSuccessful && response.body() != null) response.body()!!
        else throw Exception(response.message() ?: "Rutina no encontrada")
    }

    suspend fun createRoutine(name: String, exerciseIds: List<Int>): Result<RoutineDto> = runCatching {
        val response = api.createRoutine(mapOf("name" to name, "exercise_ids" to exerciseIds))
        if (response.isSuccessful && response.body() != null) response.body()!!
        else throw Exception(response.message() ?: "No se pudo crear la rutina")
    }

    suspend fun updateRoutine(id: Int, name: String, exerciseIds: List<Int>): Result<RoutineDto> = runCatching {
        val response = api.updateRoutine(id, mapOf("name" to name, "exercise_ids" to exerciseIds))
        if (response.isSuccessful && response.body() != null) response.body()!!
        else throw Exception(response.message() ?: "No se pudo actualizar")
    }

    suspend fun deleteRoutine(id: Int): Result<Unit> = runCatching {
        val response = api.deleteRoutine(id)
        if (!response.isSuccessful) throw Exception(response.message() ?: "No se pudo eliminar")
    }

    suspend fun fetchExercises(): Result<List<ExerciseDto>> = runCatching {
        val response = api.getExercises()
        if (response.isSuccessful) response.body().orEmpty()
        else throw Exception(response.message() ?: "Error al cargar ejercicios")
    }

    suspend fun fetchExercise(id: Int): Result<ExerciseDto> = runCatching {
        val response = api.getExerciseById(id)
        if (response.isSuccessful && response.body() != null) response.body()!!
        else throw Exception(response.message() ?: "Ejercicio no encontrado")
    }

    suspend fun saveExerciseWithFiles(
        id: Int?,
        name: String,
        description: String,
        type: Int,
        videoFile: File?,
        imageFile: File?
    ): Result<ExerciseDto> = runCatching {
        val videoPart = videoFile?.let {
            MultipartBody.Part.createFormData(
                "video",
                it.name,
                it.asRequestBody("video/*".toMediaTypeOrNull())
            )
        }
        val imagePart = imageFile?.let {
            MultipartBody.Part.createFormData(
                "image",
                it.name,
                it.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        if (id == null) {
            val response = api.createExerciseMultipart(
                name = name.toRequestBody(),
                description = description.toRequestBody(),
                type = type.toString().toRequestBody(),
                video = videoPart,
                image = imagePart
            )
            if (response.isSuccessful && response.body() != null) response.body()!!
            else throw Exception(response.message() ?: "No se pudo crear")
        } else {
            val response = api.updateExerciseMultipart(
                id = id,
                name = name.toRequestBody(),
                description = description.toRequestBody(),
                type = type.toString().toRequestBody(),
                video = videoPart,
                image = imagePart
            )
            if (response.isSuccessful && response.body() != null) response.body()!!
            else throw Exception(response.message() ?: "No se pudo actualizar")
        }
    }

    suspend fun fetchGroupMembers(groupId: Int): Result<List<GroupUserDto>> = runCatching {
        val response = api.getGroupMembers(groupId)
        if (response.isSuccessful) response.body().orEmpty()
        else throw Exception(response.message() ?: "Error al cargar miembros")
    }

    suspend fun deleteExercise(id: Int): Result<Unit> = runCatching {
        val response = api.deleteExercise(id)
        if (!response.isSuccessful) throw Exception(response.message() ?: "No se pudo eliminar")
    }

    suspend fun fetchGroups(): Result<List<GroupDto>> = runCatching {
        val response = api.getGroups()
        if (response.isSuccessful) response.body().orEmpty()
        else throw Exception(response.message() ?: "Error al cargar grupos")
    }

    suspend fun fetchGroup(id: Int): Result<GroupDto> = runCatching {
        val response = api.getGroupById(id)
        if (response.isSuccessful && response.body() != null) response.body()!!
        else throw Exception(response.message() ?: "Grupo no encontrado")
    }

    suspend fun updateGroup(
        id: Int,
        name: String?,
        description: String?,
        points: Int?
    ): Result<GroupDto> = runCatching {
        val body = mutableMapOf<String, Any?>()
        if (name != null) body["name"] = name
        if (description != null) body["description"] = description
        if (points != null) body["points"] = points
        val response = api.updateGroup(id, body)
        if (response.isSuccessful && response.body() != null) response.body()!!
        else throw Exception(response.message() ?: "No se pudo actualizar")
    }

    suspend fun deleteGroup(id: Int): Result<Unit> = runCatching {
        val response = api.deleteGroup(id)
        if (!response.isSuccessful) throw Exception(response.message() ?: "No se pudo eliminar")
    }

    suspend fun fetchMissions(): Result<List<MissionDto>> = runCatching {
        val response = api.getMissions()
        if (response.isSuccessful) response.body().orEmpty()
        else throw Exception(response.message() ?: "Error al cargar misiones")
    }

    suspend fun fetchMission(id: Int): Result<MissionDto> = runCatching {
        val response = api.getMissionById(id)
        if (response.isSuccessful && response.body() != null) response.body()!!
        else throw Exception(response.message() ?: "Misión no encontrada")
    }

    suspend fun createMission(
        name: String,
        type: Int,
        points: Int,
        objective: Int
    ): Result<MissionDto> = runCatching {
        val response = api.createMission(
            mapOf(
                "name" to name,
                "type" to type,
                "points" to points,
                "objective" to objective
            )
        )
        if (response.isSuccessful && response.body() != null) response.body()!!
        else throw Exception(response.message() ?: "No se pudo crear")
    }

    suspend fun updateMission(
        id: Int,
        name: String?,
        type: Int?,
        points: Int?,
        objective: Int?
    ): Result<MissionDto> = runCatching {
        val body = mutableMapOf<String, Any?>()
        if (name != null) body["name"] = name
        if (type != null) body["type"] = type
        if (points != null) body["points"] = points
        if (objective != null) body["objective"] = objective
        val response = api.updateMission(id, body)
        if (response.isSuccessful && response.body() != null) response.body()!!
        else throw Exception(response.message() ?: "No se pudo actualizar")
    }

    suspend fun deleteMission(id: Int): Result<Unit> = runCatching {
        val response = api.deleteMission(id)
        if (!response.isSuccessful) throw Exception(response.message() ?: "No se pudo eliminar")
    }
}
