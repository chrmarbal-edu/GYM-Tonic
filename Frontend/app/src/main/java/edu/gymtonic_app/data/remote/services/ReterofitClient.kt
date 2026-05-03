package edu.gymtonic_app.data.remote.services

import android.util.Log
import edu.gymtonic_app.BuildConfig
import edu.gymtonic_app.data.remote.model.auth.LoginRequest
import edu.gymtonic_app.data.remote.model.auth.LoginResponse
import edu.gymtonic_app.data.remote.model.auth.SessionManager
import edu.gymtonic_app.data.remote.model.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.model.routine.RoutineDto
import edu.gymtonic_app.data.remote.model.user.RegisterRequest
import edu.gymtonic_app.data.remote.model.user.RegisterResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

object RetrofitClient {
    private val BASE_URL = BuildConfig.BASE_URL
    private var sessionManager: SessionManager? = null
    private val tag = "RetrofitClient"

    // Métodos para inicializar SessionManager desde la aplicación
    fun setSessionManager(manager: SessionManager) {
        sessionManager = manager
    }

    // Interceptor que agrega Authorization header
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()

        // NO agregar Authorization en POST users/login y POST users (register)
        val isLoginEndpoint = url.contains("users/login") && originalRequest.method == "POST"
        val isRegisterEndpoint = url.endsWith("users") && originalRequest.method == "POST"

        val newRequest = if (!isLoginEndpoint && !isRegisterEndpoint && sessionManager != null) {
            try {
                // Leer token de forma bloqueante (única forma de hacerlo en Interceptor)
                val token = runBlocking {
                    sessionManager?.sessionFlow?.first()?.token
                }

                if (!token.isNullOrEmpty()) {
                    Log.d(tag, "Agregando Authorization header para: $url")
                    originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    Log.d(tag, "Token nulo, no agregando Authorization para: $url")
                    originalRequest
                }
            } catch (e: Exception) {
                Log.e(tag, "Error al leer token: ${e.message}")
                originalRequest
            }
        } else {
            originalRequest
        }

        chain.proceed(newRequest)
    }

    val apiService: ApiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .callFactory(okHttpClient)
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
    suspend fun logout(): Response<Unit>

    // ROUTINES
    @GET("routines/routines")
    suspend fun getRoutines(): Response<List<RoutineDto>>

    @GET("routines/routine/{routineId}")
    suspend fun getRoutineById(@Path("routineId") routineId: String): Response<RoutineDetailDto>

    @GET("routines/routine/by-name/{name}")
    suspend fun getRoutineByName(@Path("name") name: String): Response<RoutineDetailDto>

}
