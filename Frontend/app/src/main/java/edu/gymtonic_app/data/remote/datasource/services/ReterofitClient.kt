package edu.gymtonic_app.data.remote.datasource.services

import edu.gymtonic_app.data.remote.datasource.model.Login.LoginRequest
import edu.gymtonic_app.data.remote.datasource.model.Login.LoginResponse
import edu.gymtonic_app.data.remote.datasource.model.RegisterRequest
import edu.gymtonic_app.data.remote.datasource.model.RegisterResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

object RetrofitClient {
    // En Android Emulator, 10.0.2.2 apunta al localhost de tu PC.
    private val BASE_URL = "http://10.0.2.2:3010/api/v1/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

interface ApiService {
    // LOGIN
    @POST("users/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // REGISTER
    @POST("users")
    @Headers("Content-Type: application/json")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    //LOGOUT
    @GET("users/logout")
    suspend fun logout()

}