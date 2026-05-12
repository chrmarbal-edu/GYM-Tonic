package edu.gymtonic_app.data.remote.remoteDatasource

import android.util.Log
import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseDto
import edu.gymtonic_app.data.remote.services.RetrofitClient

class ExerciseRemoteDataSource {
    private val api = RetrofitClient.apiService

    suspend fun getExercises() = api.getExercises()

    suspend fun getExercisesByType(type: String) = api.getExercisesByType(type)

    suspend fun getExerciseById(id: String) = api.getExerciseById(id)

    suspend fun createExercise(request: Map<String, Any>) = api.createExercise(request)

    suspend fun updateExercise(id: String, request: Map<String, Any?>) = api.updateExercise(id, request)

    suspend fun deleteExercise(id: String)= api.deleteExercise(id)

}
