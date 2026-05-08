package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.local.datasource.local.exercise.ExerciseLocalDataSource
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.mapper.toDomain
import edu.gymtonic_app.data.remote.datasource.ExerciseRemoteDataSource
import edu.gymtonic_app.domain.model.exercise.ExerciseDetail
import kotlinx.coroutines.flow.Flow

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

	//Metodo para obtener un ejercicio por id del servidor
	suspend fun getExerciseById(exerciseId: String): Result<ExerciseDetail> {
		// Devuelve el resultado de la llamada al servidor
		return runCatching {
			//Si la llamada es exitosa, devuelve el ejercicio
			exerciseRemoteDataSource.getExerciseById(exerciseId).toDomain(exerciseId)
		// Si falla, devuelve un fallback
		}.recoverCatching {
			buildFallback(exerciseId)
		}
	}

	//Metodo de apoyo para fallbacks
	private fun buildFallback(exerciseId: String): ExerciseDetail {
		val normalized = exerciseId.lowercase()
		val inferredName = when {
			normalized.contains("estocadas") -> "ESTOCADAS"
			normalized.contains("press") -> "PRESS"
			normalized.contains("pull") -> "PULL OVER"
			normalized.contains("remo") -> "REMO"
			normalized.contains("sentadilla") -> "SENTADILLA"
			normalized.contains("peso") -> "PESO MUERTO"
			else -> "EJERCICIO"
		}

		return ExerciseDetail(
			id = exerciseId,
			name = inferredName,
			durationSeconds = 15,
			imageKey = "squat",
			instructions = listOf(
				"Mantén tecnica controlada durante toda la serie.",
				"Respira de forma constante y evita compensaciones.",
				"Ajusta la carga para completar repeticiones con buena forma."
			)
		)
	}
	//endregion
}
