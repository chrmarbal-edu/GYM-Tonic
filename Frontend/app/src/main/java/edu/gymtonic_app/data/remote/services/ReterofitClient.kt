package edu.gymtonic_app.data.remote.services

import android.util.Log
import edu.gymtonic_app.BuildConfig
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginRequest
import edu.gymtonic_app.data.remote.remoteModel.auth.GoogleLoginRequest
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
import retrofit2.http.PUT
import retrofit2.http.Path

object RetrofitClient {
    private val BASE_URL = BuildConfig.API_BASE_URL
    private var sessionManager: SessionManager? = null
    private val tag = "RetrofitClient"

    fun setSessionManager(manager: SessionManager) {
        sessionManager = manager
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()

        val isLoginEndpoint = url.contains("users/login") && originalRequest.method == "POST"
        val isRegisterEndpoint = url.endsWith("users") && originalRequest.method == "POST"
        val isGoogleLogin = url.contains("auth/googleLogin") && originalRequest.method == "POST"
        val isFacebookLogin = url.contains("auth/facebookLogin") && originalRequest.method == "POST"

        val newRequest = if (!isLoginEndpoint && !isRegisterEndpoint && !isGoogleLogin && !isFacebookLogin && sessionManager != null) {
            try {
                val token = runBlocking {
                    sessionManager?.sessionFlow?.first()?.token
                }

                if (!token.isNullOrEmpty()) {
                    originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    originalRequest
                }
            } catch (e: Exception) {
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

    @POST("auth/googleLogin")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): Response<Any>

    @POST("auth/facebookLogin")
    suspend fun facebookLogin(@Body request: Map<String, Any>): Response<LoginResponse>

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
    suspend fun getUserById(@Path("id") id: Int): Response<UserDto>

    @PATCH("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body request: Map<String, Any?>
    ): Response<UserDto>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    // MISSIONS
    @GET("missions")
    suspend fun getMissions(): Response<List<MissionDto>>

    @GET("missions/{id}")
    suspend fun getMissionById(@Path("id") id: Int): Response<MissionDto>

    // USER MISSIONS
    @GET("users/missions")
    suspend fun getUserMissions(): Response<List<UserMissionDto>>

    @GET("users/missions/user/{userId}")
    suspend fun getUserMissionByUserId(@Path("userId") userId: Int): Response<List<UserMissionDto>>

    @GET("users/missions/mission/{missionId}")
    suspend fun getUserMissionByMissionId(@Path("missionId") missionId: Int): Response<List<UserMissionDto>>

    @GET("users/missions/{id}")
    suspend fun getUserMissionById(@Path("id") id: Int): Response<UserMissionDto>

    @POST("users/missions")
    suspend fun createUserMission(@Body request: Map<String, Any>): Response<UserMissionDto>

    @PATCH("users/missions/{id}")
    suspend fun updateUserMission(
        @Path("id") id: Int,
        @Body request: Map<String, Any?>
    ): Response<UserMissionDto>

    @DELETE("users/missions/{id}")
    suspend fun deleteUserMission(@Path("id") id: Int): Response<Unit>

    // EXERCISES
    @GET("exercises")
    suspend fun getExercises(): Response<List<ExerciseDto>>

    @GET("exercises/type/{typeName}")
    suspend fun getExercisesByType(@Path("typeName") typeName: String): Response<List<ExerciseDto>>

    @GET("exercises/{id}")
    suspend fun getExerciseById(@Path("id") id: Int): Response<ExerciseDto>

    @POST("exercises")
    suspend fun createExercise(@Body request: edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseRequest): Response<Unit>

    @PUT("exercises/{id}")
    suspend fun updateExercise(
        @Path("id") id: Int,
        @Body request: edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseRequest
    ): Response<Unit>

    @DELETE("exercises/{id}")
    suspend fun deleteExercise(@Path("id") id: Int): Response<Unit>

    // ROUTINES
    @GET("routines/routines")
    suspend fun getRoutines(): Response<List<RoutineDto>>

    @GET("routines/routines/categories")
    suspend fun getRoutineCategories(): Response<List<TrainingCategoryDto>>

    @GET("routines/routine/by-name/{name}")
    suspend fun getRoutineByName(@Path("name") name: String): Response<RoutineDto>

    @GET("routines/routine/{routineId}/with-exercises")
    suspend fun getRoutineWithExercisesById(
        @Path("routineId") routineId: Int
    ): Response<RoutineDetailDto>

    @GET("routines/routine/{routineId}")
    suspend fun getRoutineById(@Path("routineId") routineId: Int): Response<RoutineDto>

    @POST("routines/routine/new")
    suspend fun createRoutine(@Body request: Map<String, Any>): Response<RoutineDto>

    @PATCH("routines/routine/{routineId}")
    suspend fun updateRoutine(
        @Path("routineId") routineId: Int,
        @Body request: Map<String, Any?>
    ): Response<RoutineDto>

    @DELETE("routines/routine/{routineId}")
    suspend fun deleteRoutine(@Path("routineId") routineId: Int): Response<Unit>

    // GROUPS
    @GET("groups")
    suspend fun getGroups(): Response<List<GroupDto>>

    @GET("groups/{id}")
    suspend fun getGroupById(@Path("id") id: Int): Response<GroupDto>

    @POST("groups/new")
    suspend fun createGroup(@Body request: Map<String, Any>): Response<GroupDto>

    @PATCH("groups/{id}")
    suspend fun updateGroup(
        @Path("id") id: Int,
        @Body request: Map<String, Any?>
    ): Response<GroupDto>

    @DELETE("groups/{id}")
    suspend fun deleteGroup(@Path("id") id: Int): Response<Unit>

    // FRIENDS
    @GET("friends")
    suspend fun getFriends(): Response<List<FriendDto>>

    @GET("friends/{id}")
    suspend fun getFriendById(@Path("id") id: Int): Response<FriendDto>

    @GET("friends/user/{userId}")
    suspend fun getFriendsByUserId(@Path("userId") userId: Int): Response<List<FriendDto>>

    @POST("friends")
    suspend fun createFriend(@Body request: Map<String, Any>): Response<FriendDto>

    @DELETE("friends/{id}")
    suspend fun deleteFriend(@Path("id") id: Int): Response<Unit>

    // FRIEND REQUESTS
    @GET("friendRequests")
    suspend fun getFriendRequests(): Response<List<FrequestDto>>

    @GET("friendRequests/{id}")
    suspend fun getFriendRequestById(@Path("id") id: Int): Response<FrequestDto>

    @POST("friendRequests")
    suspend fun createFriendRequest(@Body request: Map<String, Any>): Response<FrequestDto>

    @PATCH("friendRequests/accept/{id}")
    suspend fun acceptFriendRequest(@Path("id") id: Int): Response<Unit>

    @PATCH("friendRequests/reject/{id}")
    suspend fun rejectFriendRequest(@Path("id") id: Int): Response<Unit>

    @DELETE("friendRequests/{id}")
    suspend fun deleteFriendRequest(@Path("id") id: Int): Response<Unit>
}
