package edu.gymtonic_app.data.remote.services

import android.util.Log
import edu.gymtonic_app.BuildConfig
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginRequest
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginResponse
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseDto
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import edu.gymtonic_app.data.remote.remoteModel.mission.MissionDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDto
import edu.gymtonic_app.data.remote.remoteModel.social.FrequestDto
import edu.gymtonic_app.data.remote.remoteModel.social.FriendDto
import edu.gymtonic_app.data.remote.remoteModel.training.TrainingCategoryDto
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterRequest
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterResponse
import edu.gymtonic_app.data.remote.remoteModel.user.UserDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserMissionDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
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

    // ADVERTENCIA TEMPORAL:
    // Hay endpoints usan Map<String, Any> / Map<String, Any?> porque no hay
    // DTOs de request consolidados en el frontend para estas operaciones.
    // Si ya existen DTOs tipados para crear o actualizar recursos, conviene
    // sustituir estos mapas por clases concretas.

    // AUTH
    @POST("auth/googleLogin")
    suspend fun googleLogin(@Body request: Map<String, Any>): Response<LoginResponse>

    @POST("auth/facebookLogin")
    suspend fun facebookLogin(@Body request: Map<String, Any>): Response<LoginResponse>


    // ADVERTENCIA TEMPORAL:
    // En el backend, users.routes.js declara GET /:id antes que GET /missions.
    // Eso puede hacer que users/missions se interprete como id="missions".
    // Si falla esta llamada, hay que mover /missions antes que /:id en el backend.


    // USERS
    @POST("users")
    @Headers("Content-Type: application/json")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("users/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("users/logout")
    suspend fun logout(): Response<Unit>

    @GET("users")
    suspend fun getUsers(): Response<List<UserDto>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<UserDto>

    @PATCH("users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: Map<String, Any?>
    ): Response<UserDto>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>


    // MISSIONS
    @GET("missions")
    suspend fun getMissions(): Response<List<MissionDto>>

    @GET("missions/{id}")
    suspend fun getMissionById(@Path("id") id: String): Response<MissionDto>


    // USER MISSIONS
    @GET("users/missions")
    suspend fun getUserMissions(): Response<List<UserMissionDto>>

    @GET("users/missions/user/{userId}")
    suspend fun getUserMissionByUserId(@Path("userId") userId: String): Response<List<UserMissionDto>>

    @GET("users/missions/mission/{missionId}")
    suspend fun getUserMissionByMissionId(@Path("missionId") missionId: String): Response<List<UserMissionDto>>

    @GET("users/missions/{id}")
    suspend fun getUserMissionById(@Path("id") id: String): Response<UserMissionDto>

    @POST("users/missions")
    suspend fun createUserMission(@Body request: Map<String, Any>): Response<UserMissionDto>

    @PATCH("users/missions/{id}")
    suspend fun updateUserMission(
        @Path("id") id: String,
        @Body request: Map<String, Any?>
    ): Response<UserMissionDto>

    @DELETE("users/missions/{id}")
    suspend fun deleteUserMission(@Path("id") id: String): Response<Unit>


    // ADVERTENCIA TEMPORAL:
    // En el backend, exercises.routes.js tiene una colisión de rutas:
    // GET /:type está antes que GET /:id, así que una petición como exercises/12
    // puede entrar en la ruta de tipo. Lo correcto sería separar la ruta de tipo,
    // por ejemplo exercises/type/{type}, o reordenar las rutas en el backend.


    // EXERCISES
    @GET("exercises")
    suspend fun getExercises(): Response<List<ExerciseDto>>

    @GET("exercises/{type}")
    suspend fun getExercisesByType(@Path("type") type: String): Response<List<ExerciseDto>>

    @GET("exercises/{id}")
    suspend fun getExerciseById(@Path("id") id: String): Response<ExerciseDto>

    @POST("exercises")
    suspend fun createExercise(@Body request: Map<String, Any>): Response<Unit>

    @PATCH("exercises/{id}")
    suspend fun updateExercise(
        @Path("id") id: String,
        @Body request: Map<String, Any?>
    ): Response<Unit>

    @DELETE("exercises/{id}")
    suspend fun deleteExercise(@Path("id") id: String): Response<Unit>






    // ROUTINES
    @GET("routines/routines")
    suspend fun getRoutines(): Response<List<RoutineDto>>

    @GET("routines/routines/categories")
    suspend fun getRoutineCategories(): Response<List<TrainingCategoryDto>>

    @GET("routines/routine/by-name/{name}")
    suspend fun getRoutineByName(@Path("name") name: String): Response<RoutineDto>

    @GET("routines/routine/{routineId}/with-exercises")
    suspend fun getRoutineWithExercisesById(
        @Path("routineId") routineId: String
    ): Response<RoutineDetailDto>

    @GET("routines/routine/{routineId}")
    suspend fun getRoutineById(@Path("routineId") routineId: String): Response<RoutineDto>

    @POST("routines/routine/new")
    suspend fun createRoutine(@Body request: Map<String, Any>): Response<RoutineDto>

    @PATCH("routines/routine/{routineId}")
    suspend fun updateRoutine(
        @Path("routineId") routineId: String,
        @Body request: Map<String, Any?>
    ): Response<RoutineDto>

    @DELETE("routines/routine/{routineId}")
    suspend fun deleteRoutine(@Path("routineId") routineId: String): Response<Unit>






    // GROUPS
    @GET("groups")
    suspend fun getGroups(): Response<List<GroupDto>>

    @GET("groups/{id}")
    suspend fun getGroupById(@Path("id") id: String): Response<GroupDto>

    @POST("groups/new")
    suspend fun createGroup(@Body request: Map<String, Any>): Response<GroupDto>

    @PATCH("groups/{id}")
    suspend fun updateGroup(
        @Path("id") id: String,
        @Body request: Map<String, Any?>
    ): Response<GroupDto>

    @DELETE("groups/{id}")
    suspend fun deleteGroup(@Path("id") id: String): Response<Unit>






    // FRIENDS
    @GET("friends")
    suspend fun getFriends(): Response<List<FriendDto>>

    @GET("friends/{id}")
    suspend fun getFriendById(@Path("id") id: String): Response<FriendDto>

    @GET("friends/user/{userId}")
    suspend fun getFriendsByUserId(@Path("userId") userId: String): Response<List<FriendDto>>

    @POST("friends")
    suspend fun createFriend(@Body request: Map<String, Any>): Response<FriendDto>

    @DELETE("friends/{id}")
    suspend fun deleteFriend(@Path("id") id: String): Response<Unit>






    // FRIEND REQUESTS
    @GET("friendRequests")
    suspend fun getFriendRequests(): Response<List<FrequestDto>>

    @GET("friendRequests/{id}")
    suspend fun getFriendRequestById(@Path("id") id: String): Response<FrequestDto>

    @POST("friendRequests")
    suspend fun createFriendRequest(@Body request: Map<String, Any>): Response<FrequestDto>

    @PATCH("friendRequests/accept/{id}")
    suspend fun acceptFriendRequest(@Path("id") id: String): Response<Unit>

    @PATCH("friendRequests/reject/{id}")
    suspend fun rejectFriendRequest(@Path("id") id: String): Response<Unit>

    @DELETE("friendRequests/{id}")
    suspend fun deleteFriendRequest(@Path("id") id: String): Response<Unit>
}
