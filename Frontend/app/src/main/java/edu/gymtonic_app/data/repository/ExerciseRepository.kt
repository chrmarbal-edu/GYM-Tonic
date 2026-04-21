package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.datasource.ExerciseRemoteDataSource
import edu.gymtonic_app.data.remote.model.exercise.ExerciseDetailDto

data class ExerciseDetailData(
	val id: String,
	val name: String,
	val durationSeconds: Int,
	val imageKey: String,
	val instructions: List<String>
)

class ExerciseRepository(
	private val exerciseRemoteDataSource: ExerciseRemoteDataSource = ExerciseRemoteDataSource()
) {

	suspend fun getExerciseById(exerciseId: String): Result<ExerciseDetailData> {
		return runCatching {
			mapDtoToData(exerciseRemoteDataSource.getExerciseById(exerciseId))
		}.recoverCatching {
			buildFallback(exerciseId)
		}
	}

	private fun mapDtoToData(dto: ExerciseDetailDto): ExerciseDetailData {
		return ExerciseDetailData(
			id = dto.id,
			name = dto.name,
			durationSeconds = dto.durationSeconds,
			imageKey = dto.imageKey,
			instructions = dto.instructions
		)
	}

	private fun buildFallback(exerciseId: String): ExerciseDetailData {
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

		return ExerciseDetailData(
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
