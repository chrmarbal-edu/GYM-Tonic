package edu.gymtonic_app.data.remote.remoteDatasource.user

import edu.gymtonic_app.data.remote.services.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UsersRemoteDataSource {
    private val api = RetrofitClient.apiService

    suspend fun getUsers() = api.getUsers()

    suspend fun getUserById(id: Int) = api.getUserById(id)

    suspend fun updateUser(id: Int, request: Map<String, Any?>, token: String? = null) = api.updateUser(id, request, token)

    suspend fun updateUserWithFile(
        id: Int,
        username: String?,
        currentPassword: String?,
        newPassword: String?,
        height: Double?,
        weight: Double?,
        objective: Int?,
        pictureFile: File?
    ) = api.updateUserWithFile(
        id = id,
        username = username?.toRequestBody(),
        currentPassword = currentPassword?.toRequestBody(),
        newPassword = newPassword?.toRequestBody(),
        height = height?.toString()?.toRequestBody(),
        weight = weight?.toString()?.toRequestBody(),
        objective = objective?.toString()?.toRequestBody(),
        picture = pictureFile?.let {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("picture", it.name, requestFile)
        }
    )

    suspend fun deleteUser(id: Int) = api.deleteUser(id)
}

