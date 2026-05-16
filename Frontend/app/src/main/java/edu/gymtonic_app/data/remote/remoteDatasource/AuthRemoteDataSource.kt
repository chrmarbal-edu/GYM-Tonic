package edu.gymtonic_app.data.remote.remoteDatasource

import android.util.Log
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginRequest
import edu.gymtonic_app.data.remote.remoteModel.auth.GoogleLoginRequest
import edu.gymtonic_app.data.remote.remoteModel.auth.FacebookLoginRequest
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginResponse
import edu.gymtonic_app.data.remote.services.RetrofitClient
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterRequest
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AuthRemoteDataSource {
    private val tag = AuthRemoteDataSource::class.java.simpleName
    private val api = RetrofitClient.apiService

    suspend fun login(request: LoginRequest): LoginResponse {
        val response = api.login(request)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta vacia del servidor")
        }

        val errorBody = response.errorBody()?.string()
        Log.e(tag, "Error login: ${response.code()} ${response.message()} | $errorBody")
        throw Exception("Error en login: ${response.message()}")
    }

    suspend fun googleLogin(idToken: String): Any {
        Log.d(tag, "Enviando token a googleLogin: ${idToken.take(10)}...")
        val response = api.googleLogin(GoogleLoginRequest(idToken))
        if (response.isSuccessful) {
            Log.d(tag, "googleLogin exitoso")
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }

        val errorBody = response.errorBody()?.string()
        Log.e(tag, "Error googleLogin: ${response.code()} ${response.message()} | $errorBody")
        throw Exception("Error en googleLogin: ${response.code()} - $errorBody")
    }

    suspend fun facebookLogin(accessToken: String): Any {
        Log.d(tag, "Enviando token a facebookLogin: ${accessToken.take(10)}...")
        val response = api.facebookLogin(FacebookLoginRequest(accessToken))
        if (response.isSuccessful) {
            Log.d(tag, "facebookLogin exitoso")
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }

        val errorBody = response.errorBody()?.string()
        Log.e(tag, "Error facebookLogin: ${response.code()} ${response.message()} | $errorBody")
        throw Exception("Error en facebookLogin: ${response.code()} - $errorBody")
    }

    suspend fun register(
        username: String,
        name: String,
        password: String?,
        birthdate: String,
        email: String,
        height: Double,
        weight: Double,
        objective: Int,
        oauth: String?,
        pictureFile: File?,
        pictureUrl: String? = null
    ): RegisterResponse {
        val response = if (pictureFile != null) {
            // CASO 1: Subida de archivo (Multipart)
            val picPart = pictureFile.let {
                val requestFile = it.asRequestBody("image/*".toRequestBody().contentType())
                MultipartBody.Part.createFormData("picture", it.name, requestFile)
            }

            api.registerWithFile(
                username = username.toRequestBody(),
                name = name.toRequestBody(),
                password = password?.toRequestBody(),
                birthdate = birthdate.toRequestBody(),
                email = email.toRequestBody(),
                height = height.toString().toRequestBody(),
                weight = weight.toString().toRequestBody(),
                objective = objective.toString().toRequestBody(),
                oauth = oauth?.toRequestBody(),
                picture = picPart
            )
        } else {
            // CASO 2: Sin archivo (JSON normal, puede llevar URL de social login o null)
            val request = RegisterRequest(
                username = username,
                name = name,
                password = password,
                birthdate = birthdate,
                email = email,
                height = height,
                weight = weight,
                objective = objective,
                oauth = oauth,
                picture = pictureUrl
            )
            api.register(request)
        }

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }

        val errorBody = response.errorBody()?.string()
        Log.e(tag, "Error register: ${response.code()} ${response.message()} | $errorBody")
        throw Exception("Error en register: ${response.message()}")
    }

    suspend fun logout() {
        val response = api.logout()
        if (response.isSuccessful) return

        val errorBody = response.errorBody()?.string()
        Log.e(tag, "Error logout: ${response.code()} ${response.message()} | $errorBody")
        throw Exception("Error en logout: ${response.message()}")
    }
}



