package edu.gymtonic_app.data.remote.remoteDatasource

import edu.gymtonic_app.data.remote.services.RetrofitClient

class RoutineRemoteDataSource {
    private val api = RetrofitClient.apiService

    suspend fun getRoutines() = api.getRoutines()

    suspend fun getRoutineCategories() = api.getRoutineCategories()

    suspend fun getRoutineByName(name: String) = api.getRoutineByName(name)

    suspend fun getRoutineWithExercisesById(routineId: Int) = api.getRoutineWithExercisesById(routineId)

    suspend fun getRoutineById(routineId: Int) = api.getRoutineById(routineId)

    suspend fun createRoutine (request: Map<String, Any>) = api.createRoutine(request)

    suspend fun updateRoutine(routineId: Int, request: Map<String, Any?>) = api.updateRoutine(routineId, request)

    suspend fun createRoutineMultipart(
        name: okhttp3.RequestBody,
        exerciseIds: okhttp3.RequestBody,
        isPersonal: okhttp3.RequestBody,
        image: okhttp3.MultipartBody.Part?
    ) = api.createRoutineMultipart(name, exerciseIds, isPersonal, image)

    suspend fun updateRoutineMultipart(
        routineId: Int,
        name: okhttp3.RequestBody?,
        exerciseIds: okhttp3.RequestBody?,
        image: okhttp3.MultipartBody.Part?
    ) = api.updateRoutineMultipart(routineId, name, exerciseIds, image)

    suspend fun deleteRoutine( routineId: Int) = api.deleteRoutine(routineId)
}
