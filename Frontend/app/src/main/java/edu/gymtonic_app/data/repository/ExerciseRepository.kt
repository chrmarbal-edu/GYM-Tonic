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
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class ExerciseRepository(
	private val exerciseRemoteDataSource: ExerciseRemoteDataSource,
	private val exerciseLocalDataSource: ExerciseLocalDataSource,
	private val context: Context? = null
) {

	//region Room
	fun getFavExercises(): Flow<List<ExerciseEntity>> {
		return exerciseLocalDataSource.getExercises()
	}

	suspend fun updateFavWord(exercise: ExerciseEntity) {
		val existing: ExerciseEntity? = exerciseLocalDataSource.getExerciseById(exercise.exercise_id)
		if (existing != null) {
			// Si ya existe, simplemente alternamos el flag de favorito. 
			// No lo borramos porque podría estar siendo usado por una rutina cacheada.
			val updated = existing.copy(is_favorite = !existing.is_favorite)
			Log.d("ExerciseRepository", "Updating favorite state for ${exercise.exercise_id} to ${updated.is_favorite}")
			exerciseLocalDataSource.insertExercise(updated)
		} else {
			// Si no existe, lo insertamos como favorito y descargamos media
			val cachedExercise = context?.let { ctx ->
				val localImg = MediaCacheManager.downloadAndCache(ctx, exercise.exercise_image)
				val localVid = MediaCacheManager.downloadAndCache(ctx, exercise.exercise_video)
				exercise.copy(exercise_image = localImg, exercise_video = localVid, is_favorite = true)
			} ?: exercise.copy(is_favorite = true)
			
			Log.d("ExerciseRepository", "Caching new favorite exercise: ${exercise.exercise_id}")
			exerciseLocalDataSource.insertExercise(cachedExercise)
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
				val errorBody = response.errorBody()?.string().orEmpty()
				throw Exception(
					"Error al crear ejercicio (HTTP ${response.code()}): ${response.message()} $errorBody"
				)
			}
			Unit
		}
	}

	suspend fun updateExercise(id: Int, request: ExerciseRequest): Result<Unit> {
		return runCatching {
			val response = exerciseRemoteDataSource.updateExercise(id, request)
			if (!response.isSuccessful) {
				val errorBody = response.errorBody()?.string().orEmpty()
				throw Exception(
					"Error al actualizar ejercicio (HTTP ${response.code()}): ${response.message()} $errorBody"
				)
			}
			Unit
		}
	}

	suspend fun deleteExercise(id: Int): Result<Unit> {
		return runCatching {
			val response = exerciseRemoteDataSource.deleteExercise(id)
			if (!response.isSuccessful) {
				val errorBody = response.errorBody()?.string().orEmpty()
				throw Exception(
					"Error al eliminar ejercicio (HTTP ${response.code()}): ${response.message()} $errorBody"
				)
			}
			Unit
		}
	}

	private fun <T> unwrapOne(response: Response<T>, defaultMessage: String): T {
		if (response.isSuccessful) {
			return response.body() ?: throw Exception("$defaultMessage (body vacío)")
		}
		val errorBody = response.errorBody()?.string().orEmpty()
		throw Exception("$defaultMessage (HTTP ${response.code()}): ${response.message()} $errorBody")
	}

	private fun <T> unwrapList(response: Response<List<T>>, defaultMessage: String): List<T> {
		if (response.isSuccessful) {
			return response.body() ?: emptyList()
		}
		val errorBody = response.errorBody()?.string().orEmpty()
		throw Exception("$defaultMessage (HTTP ${response.code()}): ${response.message()} $errorBody")
	}
}
