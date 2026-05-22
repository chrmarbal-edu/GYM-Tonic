package edu.gymtonic_app.data.repository

import android.content.Context
import android.util.Log
import edu.gymtonic_app.data.local.localDatasource.user.UserLocalDataSource
import edu.gymtonic_app.data.mapper.toDto
import edu.gymtonic_app.data.mapper.toEntity
import edu.gymtonic_app.data.remote.remoteDatasource.user.UsersRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginResponse
import edu.gymtonic_app.data.remote.remoteModel.user.UserDto
import edu.gymtonic_app.data.util.MediaCacheManager
import java.io.File

class UserRepository(
    private val usersRemoteDataSource: UsersRemoteDataSource = UsersRemoteDataSource(),
    private val userLocalDataSource: UserLocalDataSource? = null,
    private val context: Context? = null
) {
    suspend fun getUserById(id: Int): Result<UserDto> = runCatching {
        try {
            val response = usersRemoteDataSource.getUserById(id)
            if (response.isSuccessful) {
                var userDto = response.body()!!
                
                // Caching with media
                context?.let { ctx ->
                    val localPicture = MediaCacheManager.downloadAndCache(ctx, userDto.userPicture)
                    userDto = userDto.copy(userPicture = localPicture)
                }

                Log.d("UserRepository", "Caching user data for ID: ${userDto.userId}")
                userLocalDataSource?.upsertUser(userDto.toEntity())
                userDto
            } else {
                userLocalDataSource?.getUserById(id)?.toDto() ?: throw Exception("Error al obtener usuario: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.d("UserRepository", "Error fetching from API, loading from cache for ID: $id")
            userLocalDataSource?.getUserById(id)?.toDto() ?: throw e
        }
    }

    suspend fun updateUser(id: Int, data: Map<String, Any?>, token: String? = null): Result<LoginResponse> = runCatching {
        val response = usersRemoteDataSource.updateUser(id, data, token)
        if (response.isSuccessful) {
            val loginResponse = response.body()!!
            loginResponse.data?.let { userData ->
                // Mapping LoginUserData to UserEntity manually as we don't have a mapper for it yet
                val userEntity = edu.gymtonic_app.data.local.localModel.user.UserEntity(
                    user_id = userData.user_id,
                    user_username = userData.user_username,
                    user_name = userData.user_name,
                    user_birthdate = userData.user_birthdate,
                    user_email = userData.user_email,
                    user_picture = userData.user_picture,
                    user_height = userData.user_height,
                    user_weight = userData.user_weight,
                    user_objetive = userData.user_objetive,
                    user_points = userData.user_points,
                    user_role = userData.user_role
                )
                userLocalDataSource?.upsertUser(userEntity)
            }
            loginResponse
        }
        else throw Exception("Error al actualizar usuario: ${response.code()}")
    }

    suspend fun updateUserWithFile(
        id: Int,
        username: String?,
        password: String?,
        height: Double?,
        weight: Double?,
        pictureFile: File?
    ): Result<LoginResponse> = runCatching {
        val response = usersRemoteDataSource.updateUserWithFile(id, username, password, height, weight, pictureFile)
        if (response.isSuccessful) {
            val loginResponse = response.body()!!
            loginResponse.data?.let { userData ->
                val userEntity = edu.gymtonic_app.data.local.localModel.user.UserEntity(
                    user_id = userData.user_id,
                    user_username = userData.user_username,
                    user_name = userData.user_name,
                    user_birthdate = userData.user_birthdate,
                    user_email = userData.user_email,
                    user_picture = userData.user_picture,
                    user_height = userData.user_height,
                    user_weight = userData.user_weight,
                    user_objetive = userData.user_objetive,
                    user_points = userData.user_points,
                    user_role = userData.user_role
                )
                userLocalDataSource?.upsertUser(userEntity)
            }
            loginResponse
        }
        else throw Exception("Error al actualizar usuario con archivo: ${response.code()}")
    }

    suspend fun deleteUser(id: Int): Result<Unit> = runCatching {
        val response = usersRemoteDataSource.deleteUser(id)
        if (response.isSuccessful) {
            userLocalDataSource?.deleteUserById(id)
            Unit
        }
        else throw Exception("Error al eliminar usuario: ${response.code()}")
    }
}
