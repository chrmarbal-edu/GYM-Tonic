package edu.gymtonic_app.data.remote

import edu.gymtonic_app.data.remote.model.LoginRequest
import edu.gymtonic_app.data.remote.model.LoginResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

object RetrofitClient {
    private val BASE_URL = "http://localhost:3010/api/v1/users/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

interface ApiService {
    // Función para obtener el login, se pasa un objeto LoginRequest en el body.
    @POST("login") // https://www.javiercarrasco.es/api/coffee/login
    @Headers("Content-Type: application/json") // Indica que el contenido es JSON.
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}