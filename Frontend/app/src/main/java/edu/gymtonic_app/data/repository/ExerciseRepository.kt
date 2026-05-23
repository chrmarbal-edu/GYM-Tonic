package edu.gymtonic_app.data.repository

import android.content.Context
import android.util.Log
import edu.gymtonic_app.data.local.localDatasource.exercise.ExerciseLocalDataSource
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.mapper.toDto
import edu.gymtonic_app.data.mapper.toEntity
import edu.gymtonic_app.data.remote.remoteDatasource.ExerciseRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseDto
import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseRequest
import edu.gymtonic_app.data.util.MediaCacheManager
import edu.gymtonic_app.core.network.ErrorManager
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class ExerciseRepository(
	private val exerciseRemoteDataSource: ExerciseRemoteDataSource,
	private val exerciseLocalDataSource: ExerciseLocalDataSource,
	private val context: Context? = null
) {

	//region Room
	fun getFavExercises(userId: Int): Flow<List<ExerciseEntity>> {
		return exerciseLocalDataSource.getFavoriteExercises(userId)
	}

	suspend fun updateFavWord(userId: Int, exercise: ExerciseEntity) {
		val existing: ExerciseEntity? = exerciseLocalDataSource.getExerciseById(exercise.exercise_id)
		
		// 1. Aseguramos que el ejercicio base esté cacheado
		if (existing == null) {
			val cachedExercise = context?.let { ctx ->
				val localImg = MediaCacheManager.downloadAndCache(ctx, exercise.exercise_image)
				val localVid = MediaCacheManager.downloadAndCache(ctx, exercise.exercise_video)
				exercise.copy(exercise_image = localImg, exercise_video = localVid)
			} ?: exercise
			
			Log.d("ExerciseRepository", "Caching base exercise: ${exercise.exercise_id}")
			exerciseLocalDataSource.insertExercise(cachedExercise)
		}

		// 2. Alternamos el favorito específico para este usuario
		Log.d("ExerciseRepository", "Toggling favorite for user $userId and exercise ${exercise.exercise_id}")
		exerciseLocalDataSource.toggleFavorite(userId, exercise.exercise_id)
	}
	//endregion

	//region Retrofit
	suspend fun getExercises(): Result<List<ExerciseDto>> {
		return runCatching {
			try {
				unwrapList(
					response = exerciseRemoteDataSource.getExercises(),
					defaultMessage = "No se pudieron obtener los ejercicios"
				)
			} catch (e: Exception) {
				Log.d("ExerciseRepository", "Offline: loading all local exercises (favorites/routine members)")
				exerciseLocalDataSource.getAllExercises().map { it.toDto() }
			}
		}
	}

	suspend fun getExercisesByType(type: String): Result<List<ExerciseDto>> {
		return runCatching {
			try {
				unwrapList(
					response = exerciseRemoteDataSource.getExercisesByType(type),
					defaultMessage = "No se pudieron obtener los ejercicios del tipo $type"
				)
			} catch (e: Exception) {
				// Filter local by type if we had type info in entity. 
				// For now, return all cached as a fallback
				exerciseLocalDataSource.getAllExercises().map { it.toDto() }
			}
		}
	}

	suspend fun getExerciseById(exerciseId: Int): Result<ExerciseDto> {
		return runCatching {
			try {
				unwrapOne(
					response = exerciseRemoteDataSource.getExerciseById(exerciseId),
					defaultMessage = "No se pudo obtener el ejercicio con id=$exerciseId"
				)
			} catch (e: Exception) {
				exerciseLocalDataSource.getExerciseById(exerciseId)?.toDto() ?: throw e
			}
		}
	}

	suspend fun createExercise(request: ExerciseRequest): Result<Unit> {
		return runCatching {
			val response = exerciseRemoteDataSource.createExercise(request)
			if (!response.isSuccessful) {
				throw Exception(ErrorManager.parseResponseError(response))
			}
			Unit
		}
	}

	suspend fun updateExercise(id: Int, request: ExerciseRequest): Result<Unit> {
		return runCatching {
			val response = exerciseRemoteDataSource.updateExercise(id, request)
			if (!response.isSuccessful) {
				throw Exception(ErrorManager.parseResponseError(response))
			}
			Unit
		}
	}

	suspend fun deleteExercise(id: Int): Result<Unit> {
		return runCatching {
			val response = exerciseRemoteDataSource.deleteExercise(id)
			if (!response.isSuccessful) {
				throw Exception(ErrorManager.parseResponseError(response))
			}
			Unit
		}
	}

	private fun <T> unwrapOne(response: Response<T>, defaultMessage: String): T {
		if (response.isSuccessful) {
			return response.body() ?: throw Exception("$defaultMessage (body vacío)")
		}
		throw Exception(ErrorManager.parseResponseError(response))
	}

	private fun <T> unwrapList(response: Response<List<T>>, defaultMessage: String): List<T> {
		if (response.isSuccessful) {
			return response.body() ?: emptyList()
		}
		throw Exception(ErrorManager.parseResponseError(response))
	}
}
