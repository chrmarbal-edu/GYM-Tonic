package edu.gymtonic_app.data.remote.remoteDatasource

import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseRequest
import edu.gymtonic_app.data.remote.services.RetrofitClient

class ExerciseRemoteDataSource {
    private val api = RetrofitClient.apiService

    suspend fun getExercises() = api.getExercises()

    suspend fun getExercisesByType(type: String) = api.getExercisesByType(type)

    suspend fun getExerciseById(id: Int) = api.getExerciseById(id)

    suspend fun createExercise(request: ExerciseRequest) = api.createExercise(request)

    suspend fun updateExercise(id: Int, request: ExerciseRequest) = api.updateExercise(id, request)

    suspend fun deleteExercise(id: Int)= api.deleteExercise(id)
}
