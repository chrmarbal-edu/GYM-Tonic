package edu.gymtonic_app.data.repository

import android.content.Context
import android.util.Log
import edu.gymtonic_app.data.local.localDatasource.mission.MissionLocalDataSource
import edu.gymtonic_app.data.local.localDatasource.userMission.UserMissionLocalDataSource
import edu.gymtonic_app.data.mapper.toDto
import edu.gymtonic_app.data.mapper.toEntity
import edu.gymtonic_app.data.mapper.toMissionEntity
import edu.gymtonic_app.data.remote.remoteDatasource.user.UserMissionsRemoteDatasource
import edu.gymtonic_app.data.remote.remoteModel.mission.MissionDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserMissionDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserMissionsResponseDto
import edu.gymtonic_app.data.remote.remoteModel.week.WeeklyCalendarDayDto
import edu.gymtonic_app.core.network.ErrorManager
import retrofit2.Response

class UserMissionsRepository(
    private val userMissionsRemoteDataSource: UserMissionsRemoteDatasource,
    private val missionLocalDataSource: MissionLocalDataSource? = null,
    private val userMissionLocalDataSource: UserMissionLocalDataSource? = null,
    private val context: Context? = null
) {

    // MISIONES
    suspend fun getMissions(): Result<List<MissionDto>> {
        return runCatching {
            try {
                unwrapList(
                    response = userMissionsRemoteDataSource.getMissions(),
                    defaultMessage = "No se pudieron obtener las misiones"
                )
            } catch (e: Exception) {
                Log.d("UserMissionsRepo", "Offline: loading all local missions")
                missionLocalDataSource?.getAllMissions()?.map { it.toDto() } ?: emptyList()
            }
        }
    }

    suspend fun getMissionById(id: Int): Result<MissionDto> {
        return runCatching {
            try {
                val remoteMission = unwrapOne(
                    response = userMissionsRemoteDataSource.getMissionById(id),
                    defaultMessage = "No se pudo obtener la misión con id=$id"
                )
                missionLocalDataSource?.insertMissions(listOf(remoteMission.toEntity()))
                remoteMission
            } catch (e: Exception) {
                missionLocalDataSource?.getMissionById(id)?.toDto() ?: throw e
            }
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
            try {
                val responseApi = userMissionsRemoteDataSource.getUserMissionByUserId(userId)
                if (responseApi.isSuccessful) {
                    val response = responseApi.body() ?: throw Exception("Body vacío")
                    
                    // Cache assigned misiones
                    val allUserMissions = response.missions + response.expiredMissions
                    Log.d("UserMissionsRepo", "Caching ${allUserMissions.size} user missions")
                    userMissionLocalDataSource?.insertUserMissions(allUserMissions.map { it.toEntity() })
                    
                    // Cache details
                    allUserMissions.forEach { userMissionDto ->
                        userMissionDto.toMissionEntity()?.let { missionEntity ->
                            missionLocalDataSource?.insertMissions(listOf(missionEntity))
                        }
                    }
                    response
                } else {
                    loadUserMissionsFromCache(userId) ?: throw Exception("Error API ${responseApi.code()}")
                }
            } catch (e: Exception) {
                Log.d("UserMissionsRepo", "Error fetching missions for $userId, loading cache")
                loadUserMissionsFromCache(userId) ?: throw e
            }
        }
    }

    private suspend fun loadUserMissionsFromCache(userId: Int): UserMissionsResponseDto? {
        val cachedUserMissions = userMissionLocalDataSource?.getUserMissions(userId) ?: emptyList()
        if (cachedUserMissions.isEmpty()) return null
        
        return UserMissionsResponseDto(
            missions = cachedUserMissions.map { entity ->
                UserMissionDto(
                    userMissionId = entity.user_x_mission_id,
                    userMissionUserid = entity.user_x_mission_userid,
                    missionId = entity.user_x_mission_missionid,
                    userMissionExpiration = entity.user_x_mission_expiration,
                    completed = entity.user_x_mission_completed,
                    progress = entity.user_x_mission_progress
                    // missionName can be reconstructed from missionLocalDataSource if needed
                )
            }
        )
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
            try {
                userMissionsRemoteDataSource.getWeeklyCalendarDays()
            } catch (e: Exception) {
                Log.d("UserMissionsRepo", "Offline: loading empty calendar days")
                emptyList()
            }
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