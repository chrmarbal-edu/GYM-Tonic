package edu.gymtonic_app.data.remote

import edu.gymtonic_app.data.remote.model.LoginRequest
import edu.gymtonic_app.data.remote.model.LoginResponse
import edu.gymtonic_app.data.remote.model.RegisterRequest
import edu.gymtonic_app.data.remote.model.RegisterResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

object RetrofitClient {
    private val BASE_URL = "http://192.168.195.109:3010/api/v1/"

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