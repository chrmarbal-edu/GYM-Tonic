package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.remoteDatasource.user.UsersRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginResponse
import edu.gymtonic_app.data.remote.remoteModel.user.UserDto
import java.io.File

class UserRepository(
    private val usersRemoteDataSource: UsersRemoteDataSource = UsersRemoteDataSource()
) {
    suspend fun getUserById(id: Int): Result<UserDto> = runCatching {
        val response = usersRemoteDataSource.getUserById(id)
        if (response.isSuccessful) response.body()!!
        else throw Exception("Error al obtener usuario: ${response.code()}")
    }

    suspend fun updateUser(id: Int, data: Map<String, Any?>): Result<LoginResponse> = runCatching {
        val response = usersRemoteDataSource.updateUser(id, data)
        if (response.isSuccessful) response.body()!!
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
        if (response.isSuccessful) response.body()!!
        else throw Exception("Error al actualizar usuario con archivo: ${response.code()}")
    }

    suspend fun deleteUser(id: Int): Result<Unit> = runCatching {
        val response = usersRemoteDataSource.deleteUser(id)
        if (response.isSuccessful) Unit
        else throw Exception("Error al eliminar usuario: ${response.code()}")
    }
}
