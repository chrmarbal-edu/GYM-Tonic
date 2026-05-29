package edu.gymtonic_app.data.remote.services

import android.util.Log
import edu.gymtonic_app.BuildConfig
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginRequest
import edu.gymtonic_app.data.remote.remoteModel.auth.GoogleLoginRequest
import edu.gymtonic_app.data.remote.remoteModel.auth.FacebookLoginRequest
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginResponse
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseDto
import edu.gymtonic_app.data.remote.remoteModel.group.CreateGroupRequest
import edu.gymtonic_app.data.remote.remoteModel.group.CreateGroupResponse
import edu.gymtonic_app.data.remote.remoteModel.group.CreateGroupRoutineRequest
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import edu.gymtonic_app.data.remote.remoteModel.group.GroupUserDto
import edu.gymtonic_app.data.remote.remoteModel.mission.MissionDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDto
import edu.gymtonic_app.data.remote.remoteModel.social.FrequestDto
import edu.gymtonic_app.data.remote.remoteModel.social.FriendDto
import edu.gymtonic_app.data.remote.remoteModel.social.FriendRequestsByUserResponse
import edu.gymtonic_app.data.remote.remoteModel.training.TrainingCategoryDto
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterRequest
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterResponse
import edu.gymtonic_app.data.remote.remoteModel.user.UserDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserMissionDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserMissionsResponseDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserSummaryDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.IOException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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
        
        Log.d("RetrofitClient", "--> ${originalRequest.method} $url")

        val isLoginEndpoint = url.contains("users/login") && originalRequest.method == "POST"
        val isRegisterEndpoint = url.endsWith("users") && originalRequest.method == "POST"
        val isGoogleLogin = url.contains("auth/googleLogin") && originalRequest.method == "POST"
        val isFacebookLogin = url.contains("auth/facebookLogin") && originalRequest.method == "POST"
        val isRecoverEndpoint = url.contains("users/recover-account") && originalRequest.method == "POST"
        val isChangePasswordEndpoint = url.contains("users/change-password") && originalRequest.method == "POST"
        val isCheckEndpoint = url.contains("users/check-") && originalRequest.method == "GET"

        val isExcluded = isLoginEndpoint || isRegisterEndpoint || isGoogleLogin || isFacebookLogin || 
                         isRecoverEndpoint || isChangePasswordEndpoint || isCheckEndpoint

        val newRequest = if (!isExcluded && sessionManager != null) {
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

    private val networkErrorInterceptor = Interceptor { chain ->
        try {
            chain.proceed(chain.request())
        } catch (e: IOException) {
            val msg = e.message ?: ""
            if (msg.contains("Failed to connect", ignoreCase = true) ||
                msg.contains("Unable to resolve host", ignoreCase = true) ||
                msg.contains("route to host", ignoreCase = true) ||
                msg.contains("Connection refused", ignoreCase = true)) {
                throw IOException("No tienes conexión a internet", e)
            }
            throw e
        }
    }

    val apiService: ApiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(networkErrorInterceptor)
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

    @GET("users/check-username/{username}")
    suspend fun checkUsername(@Path("username") username: String): Response<Map<String, Boolean>>

    @GET("users/check-email/{email}")
    suspend fun checkEmail(@Path("email") email: String): Response<Map<String, Boolean>>

    @POST("auth/googleLogin")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): Response<Any>

    @POST("auth/facebookLogin")
    suspend fun facebookLogin(@Body request: FacebookLoginRequest): Response<Any>

    // USERS
    @POST("users")
    @Headers("Content-Type: application/json")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @Multipart
    @POST("users")
    suspend fun registerWithFile(
        @Part("username") username: okhttp3.RequestBody,
        @Part("name") name: okhttp3.RequestBody,
        @Part("password") password: okhttp3.RequestBody?,
        @Part("birthdate") birthdate: okhttp3.RequestBody,
        @Part("email") email: okhttp3.RequestBody,
        @Part("height") height: okhttp3.RequestBody,
        @Part("weight") weight: okhttp3.RequestBody,
        @Part("objective") objective: okhttp3.RequestBody,
        @Part("oauth") oauth: okhttp3.RequestBody?,
        @Part picture: okhttp3.MultipartBody.Part?
    ): Response<RegisterResponse>

    @POST("users/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("users/recover-account")
    @Headers("Content-Type: application/json")
    suspend fun recoverAccount(@Body request: edu.gymtonic_app.data.remote.remoteModel.user.ResetPasswordRequest): Response<edu.gymtonic_app.data.remote.remoteModel.user.RecoverResponse>

    @POST("users/change-password")
    @Headers("Content-Type: application/json")
    suspend fun changePassword(@Body request: edu.gymtonic_app.data.remote.remoteModel.user.ChangePasswordRequest): Response<Unit>

    @GET("users/logout")
    suspend fun logout(): Response<Unit>

    @GET("users")
    suspend fun getUsers(): Response<List<UserSummaryDto>>

    @GET("users")
    suspend fun getUsersFull(): Response<List<UserDto>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<UserDto>

    @PATCH("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body request: @JvmSuppressWildcards Map<String, Any?>,
        @retrofit2.http.Header("Authorization") token: String? = null
    ): Response<LoginResponse>

    @Multipart
    @PATCH("users/{id}")
    suspend fun updateUserWithFile(
        @Path("id") id: Int,
        @Part("username") username: okhttp3.RequestBody?,
        @Part("currentPassword") currentPassword: okhttp3.RequestBody?,
        @Part("newPassword") newPassword: okhttp3.RequestBody?,
        @Part("height") height: okhttp3.RequestBody?,
        @Part("weight") weight: okhttp3.RequestBody?,
        @Part("objective") objective: okhttp3.RequestBody?,
        @Part picture: okhttp3.MultipartBody.Part?
    ): Response<LoginResponse>

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
    suspend fun getUserMissionByUserId(@Path("userId") userId: Int): Response<UserMissionsResponseDto>

    @GET("users/missions/mission/{missionId}")
    suspend fun getUserMissionByMissionId(@Path("missionId") missionId: Int): Response<List<UserMissionDto>>

    @GET("users/missions/{id}")
    suspend fun getUserMissionById(@Path("id") id: Int): Response<UserMissionDto>

    @POST("users/missions")
    suspend fun createUserMission(@Body request: @JvmSuppressWildcards Map<String, Any>): Response<UserMissionDto>

    @PATCH("users/missions/{id}")
    suspend fun updateUserMission(
        @Path("id") id: Int,
        @Body request: @JvmSuppressWildcards Map<String, Any?>
    ): Response<UserMissionDto>

    @PATCH("users/missions/{id}/complete")
    suspend fun completeMission(@Path("id") id: Int): Response<UserMissionDto>

    @PATCH("users/missions/{id}/progress")
    suspend fun updateMissionProgress(
        @Path("id") id: Int,
        @Body request: @JvmSuppressWildcards Map<String, Any>
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

    @Multipart
    @POST("exercises")
    suspend fun createExerciseMultipart(
        @Part("name") name: okhttp3.RequestBody,
        @Part("description") description: okhttp3.RequestBody,
        @Part("type") type: okhttp3.RequestBody,
        @Part video: okhttp3.MultipartBody.Part?,
        @Part image: okhttp3.MultipartBody.Part?
    ): Response<ExerciseDto>

    @PATCH("exercises/{id}")
    suspend fun updateExercise(
        @Path("id") id: Int,
        @Body request: edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseRequest
    ): Response<Unit>

    @Multipart
    @PATCH("exercises/{id}")
    suspend fun updateExerciseMultipart(
        @Path("id") id: Int,
        @Part("name") name: okhttp3.RequestBody?,
        @Part("description") description: okhttp3.RequestBody?,
        @Part("type") type: okhttp3.RequestBody?,
        @Part video: okhttp3.MultipartBody.Part?,
        @Part image: okhttp3.MultipartBody.Part?
    ): Response<ExerciseDto>

    @DELETE("exercises/{id}")
    suspend fun deleteExercise(@Path("id") id: Int): Response<Unit>

    @POST("missions/new")
    suspend fun createMission(@Body request: @JvmSuppressWildcards Map<String, Any>): Response<MissionDto>

    @PATCH("missions/{id}")
    suspend fun updateMission(
        @Path("id") id: Int,
        @Body request: @JvmSuppressWildcards Map<String, Any?>
    ): Response<MissionDto>

    @DELETE("missions/{id}")
    suspend fun deleteMission(@Path("id") id: Int): Response<Unit>

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
    suspend fun createRoutine(@Body request: @JvmSuppressWildcards Map<String, Any>): Response<RoutineDto>

    @Multipart
    @POST("routines/routine/new")
    suspend fun createRoutineMultipart(
        @Part("name") name: okhttp3.RequestBody,
        @Part("exercises") exercises: okhttp3.RequestBody,
        @Part("is_personal") isPersonal: okhttp3.RequestBody,
        @Part image: okhttp3.MultipartBody.Part?
    ): Response<RoutineDetailDto>

    @PATCH("routines/routine/{routineId}")
    suspend fun updateRoutine(
        @Path("routineId") routineId: Int,
        @Body request: @JvmSuppressWildcards Map<String, Any?>
    ): Response<RoutineDto>

    @Multipart
    @PATCH("routines/routine/{routineId}")
    suspend fun updateRoutineMultipart(
        @Path("routineId") routineId: Int,
        @Part("name") name: okhttp3.RequestBody?,
        @Part("exercises") exercises: okhttp3.RequestBody?,
        @Part image: okhttp3.MultipartBody.Part?
    ): Response<RoutineDetailDto>

    @DELETE("routines/routine/{routineId}")
    suspend fun deleteRoutine(@Path("routineId") routineId: Int): Response<Unit>

    // GROUPS
    @GET("groups")
    suspend fun getGroups(): Response<List<GroupDto>>

    @GET("groups/my")
    suspend fun getMyGroups(): Response<List<GroupDto>>

    @GET("groups/{id}")
    suspend fun getGroupById(@Path("id") id: Int): Response<GroupDto>

    @GET("groups/{id}/members")
    suspend fun getGroupMembers(@Path("id") id: Int): Response<List<GroupUserDto>>

    @GET("groups/{id}/routines")
    suspend fun getGroupRoutines(@Path("id") id: Int): Response<List<RoutineDto>>

    @POST("groups/new")
    @Headers("Content-Type: application/json")
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<CreateGroupResponse>

    @POST("groups/{id}/join")
    suspend fun joinGroup(@Path("id") id: Int): Response<GroupUserDto>

    @DELETE("groups/{id}/leave")
    suspend fun leaveGroup(@Path("id") id: Int): Response<Map<String, String>>

    @POST("groups/{id}/routines")
    suspend fun addGroupRoutine(
        @Path("id") id: Int,
        @Body request: CreateGroupRoutineRequest
    ): Response<RoutineDto>

    @Multipart
    @PATCH("groups/{id}/routines/{routineId}")
    suspend fun updateGroupRoutineMultipart(
        @Path("id") id: Int,
        @Path("routineId") routineId: Int,
        @Part("name") name: okhttp3.RequestBody?,
        @Part("exercises") exercises: okhttp3.RequestBody?,
        @Part image: okhttp3.MultipartBody.Part?
    ): Response<RoutineDetailDto>

    @Multipart
    @POST("groups/{id}/routines")
    suspend fun addGroupRoutineMultipart(
        @Path("id") id: Int,
        @Part("name") name: okhttp3.RequestBody,
        @Part("exercises") exercises: okhttp3.RequestBody,
        @Part image: okhttp3.MultipartBody.Part?
    ): Response<RoutineDto>

    @PATCH("groups/{id}")
    suspend fun updateGroup(
        @Path("id") id: Int,
        @Body request: @JvmSuppressWildcards Map<String, Any?>
    ): Response<GroupDto>

    @DELETE("groups/{id}")
    suspend fun deleteGroup(@Path("id") id: Int): Response<Unit>

    // FRIENDS
    @GET("friends")
    suspend fun getFriends(): Response<List<FriendDto>>

    @GET("friends/{id}")
    suspend fun getFriendById(@Path("id") id: Int): Response<FriendDto>

    @GET("friends/user/{userId}")
    suspend fun getFriendsByUserId(@Path("userId") userId: Int): Response<List<UserSummaryDto>>

    @POST("friends")
    suspend fun createFriend(@Body request: @JvmSuppressWildcards Map<String, Any>): Response<FriendDto>

    @DELETE("friends/{id}")
    suspend fun deleteFriend(@Path("id") id: Int): Response<Unit>

    // FRIEND REQUESTS
    @GET("friendRequests")
    suspend fun getFriendRequests(): Response<List<FrequestDto>>

    @GET("friendRequests/user/{userId}")
    suspend fun getFriendRequestsByUserId(
        @Path("userId") userId: Int
    ): Response<FriendRequestsByUserResponse>

    @GET("friendRequests/{id}")
    suspend fun getFriendRequestById(@Path("id") id: Int): Response<FrequestDto>

    @POST("friendRequests")
    suspend fun createFriendRequest(@Body request: @JvmSuppressWildcards Map<String, Any>): Response<FrequestDto>

    @PATCH("friendRequests/accept/{id}")
    suspend fun acceptFriendRequest(@Path("id") id: Int): Response<Unit>

    @PATCH("friendRequests/reject/{id}")
    suspend fun rejectFriendRequest(@Path("id") id: Int): Response<Unit>

    @DELETE("friendRequests/{id}")
    suspend fun deleteFriendRequest(@Path("id") id: Int): Response<Unit>
}
