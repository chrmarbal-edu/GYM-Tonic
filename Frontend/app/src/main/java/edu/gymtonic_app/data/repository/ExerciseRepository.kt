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

		// 1. Insertar el ejercicio base inmediatamente para no bloquear el guardado del favorito
		if (existing == null) {
			exerciseLocalDataSource.insertExercise(exercise)
		}

		// 2. Guardar/quitar el favorito de forma inmediata
		exerciseLocalDataSource.toggleFavorite(userId, exercise.exercise_id)

		// 3. Actualizar la caché de media en segundo plano (no bloquea el favorito)
		if (existing == null && context != null) {
			try {
				val localImg = MediaCacheManager.downloadAndCache(context, exercise.exercise_image)
				val localVid = MediaCacheManager.downloadAndCache(context, exercise.exercise_video)
				if (localImg != exercise.exercise_image || localVid != exercise.exercise_video) {
					// Actualizar solo los campos de media sin tocar user_favorite_exercises
					exerciseLocalDataSource.updateExerciseMedia(exercise.exercise_id, localImg, localVid)
				}
			} catch (e: Exception) {
				// Si falla el caché de media el favorito ya está guardado, no hacer rollback
			}
		}
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
