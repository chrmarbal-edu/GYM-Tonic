package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.mapper.toDomain
import edu.gymtonic_app.data.remote.datasource.ExerciseRemoteDataSource
import edu.gymtonic_app.domain.model.exercise.ExerciseDetail

class ExerciseRepository(
	private val exerciseRemoteDataSource: ExerciseRemoteDataSource = ExerciseRemoteDataSource()
) {

	suspend fun getExerciseById(exerciseId: String): Result<ExerciseDetail> {
		return runCatching {
			exerciseRemoteDataSource.getExerciseById(exerciseId).toDomain(exerciseId)
		}.recoverCatching {
			buildFallback(exerciseId)
		}
	}

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
			imageKey = "fullbody",
			instructions = listOf(
				"Mantén tecnica controlada durante toda la serie.",
				"Respira de forma constante y evita compensaciones.",
				"Ajusta la carga para completar repeticiones con buena forma."
			)
		)
	}
}
