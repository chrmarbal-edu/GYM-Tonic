package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.remoteDatasource.user.UserMissionsRemoteDatasource
import edu.gymtonic_app.data.remote.remoteModel.mission.MissionDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserMissionDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserMissionsResponseDto
import edu.gymtonic_app.data.remote.remoteModel.week.WeeklyCalendarDayDto
import retrofit2.Response

class UserMissionsRepository(
    private val userMissionsRemoteDataSource: UserMissionsRemoteDatasource
) {

    // MISIONES
    suspend fun getMissions(): Result<List<MissionDto>> {
        return runCatching {
            unwrapList(
                response = userMissionsRemoteDataSource.getMissions(),
                defaultMessage = "No se pudieron obtener las misiones"
            )
        }
    }

    suspend fun getMissionById(id: Int): Result<MissionDto> {
        return runCatching {
            unwrapOne(
                response = userMissionsRemoteDataSource.getMissionById(id),
                defaultMessage = "No se pudo obtener la misión con id=$id"
            )
        }
    }


    // MISIONES DEL USUARIO
    suspend fun getUserMissions(): Result<List<UserMissionDto>> {
        return runCatching {
            unwrapList(
                response = userMissionsRemoteDataSource.getUserMissions(),
                defaultMessage = "No se pudieron obtener las misiones del usuario"
            )
        }
    }

    suspend fun getUserMissionByUserId(userId: Int): Result<UserMissionsResponseDto> {
        return runCatching {
            unwrapOne(
                response = userMissionsRemoteDataSource.getUserMissionByUserId(userId),
                defaultMessage = "No se pudieron obtener las misiones del usuario $userId"
            )
        }
    }

    suspend fun getUserMissionByMissionId(missionId: Int): Result<List<UserMissionDto>> {
        return runCatching {
            unwrapList(
                response = userMissionsRemoteDataSource.getUserMissionByMissionId(missionId),
                defaultMessage = "No se pudieron obtener los usuarios de la misión $missionId"
            )
        }
    }

    suspend fun getUserMissionById(id: Int): Result<UserMissionDto> {
        return runCatching {
            unwrapOne(
                response = userMissionsRemoteDataSource.getUserMissionById(id),
                defaultMessage = "No se pudo obtener la misión de usuario con id=$id"
            )
        }
    }

    suspend fun createUserMission(request: Map<String, Any>): Result<UserMissionDto> {
        return runCatching {
            unwrapOne(
                response = userMissionsRemoteDataSource.createUserMission(request),
                defaultMessage = "No se pudo crear la misión de usuario"
            )
        }
    }

    suspend fun updateUserMission(id: Int, request: Map<String, Any?>): Result<UserMissionDto> {
        return runCatching {
            unwrapOne(
                response = userMissionsRemoteDataSource.updateUserMission(id, request),
                defaultMessage = "No se pudo actualizar la misión de usuario con id=$id"
            )
        }
    }

    suspend fun deleteUserMission(id: Int): Result<Unit> {
        return runCatching {
            val response = userMissionsRemoteDataSource.deleteUserMission(id)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string().orEmpty()
                throw Exception("Error al eliminar misión de usuario (HTTP ${response.code()}): ${response.message()} $errorBody")
            }
            Unit
        }
    }

    suspend fun completeMission(id: Int): Result<UserMissionDto> {
        return runCatching {
            unwrapOne(
                response = userMissionsRemoteDataSource.completeMission(id),
                defaultMessage = "No se pudo completar la misión con id=$id"
            )
        }
    }

    suspend fun updateMissionProgress(id: Int, progress: Int): Result<UserMissionDto> {
        return runCatching {
            unwrapOne(
                response = userMissionsRemoteDataSource.updateMissionProgress(id, mapOf("progress" to progress)),
                defaultMessage = "No se pudo actualizar el progreso de la misión con id=$id"
            )
        }
    }

    // EXCEPCION permitida: hardcode temporal porque backend no tiene endpoint aún
    suspend fun getWeeklyCalendarDays(): Result<List<WeeklyCalendarDayDto>> {
        return runCatching {
            userMissionsRemoteDataSource.getWeeklyCalendarDays()
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