package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.local.localDatasource.exercise.ExerciseLocalDataSource
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.remote.remoteDatasource.ExerciseRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseDto
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class ExerciseRepository(
	private val exerciseRemoteDataSource: ExerciseRemoteDataSource ,
	private val exerciseLocalDataSource: ExerciseLocalDataSource
) {

	//region Room
	//devuelve le listado de favoritas desde room para crear las rutinas con ellos
	fun getFavExercises(): Flow<List<ExerciseEntity>> {
		return exerciseLocalDataSource.getExercises()
	}

	//Si esta en room, elimina y si no esta anyade
	suspend fun updateFavWord(exercise: ExerciseEntity) {
		val state: ExerciseEntity? = exerciseLocalDataSource.getFavExerciseById(exercise.exercise_id)
		if (state == null) { //no ta
			exerciseLocalDataSource.insertExercise(exercise)
		} else {
			exerciseLocalDataSource.deleteExercise(exercise)
		}
	}
	//endregion

	//region Retrofit
	suspend fun getExercises(): Result<List<ExerciseDto>> {
		return runCatching {
			unwrapList(
				response = exerciseRemoteDataSource.getExercises(),
				defaultMessage = "No se pudieron obtener los ejercicios"
			)
		}
	}

	suspend fun getExercisesByType(type: String): Result<List<ExerciseDto>> {
		return runCatching {
			unwrapList(
				response = exerciseRemoteDataSource.getExercisesByType(type),
				defaultMessage = "No se pudieron obtener los ejercicios del tipo $type"
			)
		}
	}

	suspend fun getExerciseById(exerciseId: String): Result<ExerciseDto> {
		return runCatching {
			unwrapOne(
				response = exerciseRemoteDataSource.getExerciseById(exerciseId),
				defaultMessage = "No se pudo obtener el ejercicio con id=$exerciseId"
			)
		}
	}

	suspend fun createExercise(request: Map<String, Any>): Result<Unit> {
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

	suspend fun updateExercise(id: String, request: Map<String, Any?>): Result<Unit> {
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

	suspend fun deleteExercise(id: String): Result<Unit> {
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

// endregion

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

		//endregion
	}
}
