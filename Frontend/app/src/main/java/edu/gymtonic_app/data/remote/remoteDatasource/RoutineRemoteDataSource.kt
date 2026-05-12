package edu.gymtonic_app.data.remote.remoteDatasource

import edu.gymtonic_app.data.remote.services.RetrofitClient

class RoutineRemoteDataSource {
    private val api = RetrofitClient.apiService

    suspend fun getRoutines() = api.getRoutines()

    suspend fun getRoutineCategories() = api.getRoutineCategories()

    suspend fun getRoutineByName(name: String) = api.getRoutineByName(name)

    suspend fun getRoutineWithExercisesById(routineId: String) = api.getRoutineWithExercisesById(routineId)

    suspend fun getRoutineById(routineId: String) = api.getRoutineById(routineId)

    suspend fun createRoutine (request: Map<String, Any>) = api.createRoutine(request)

    suspend fun updateRoutine(routineId: String, request: Map<String, Any?>) = api.updateRoutine(routineId, request)

    suspend fun deleteRoutine( routineId: String) = api.deleteRoutine(routineId)
}
