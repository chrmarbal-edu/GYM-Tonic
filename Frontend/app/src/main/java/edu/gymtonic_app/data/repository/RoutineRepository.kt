package edu.gymtonic_app.data.repository

import edu.gymtonic_app.R
import edu.gymtonic_app.data.remote.RemoteDataSource
import edu.gymtonic_app.data.remote.RemoteTrainingCategory
import edu.gymtonic_app.data.remote.datasource.model.RoutineDetailDto
import edu.gymtonic_app.data.remote.datasource.model.RoutineExerciseDto
import edu.gymtonic_app.ui.viewmodel.RoutineDetailUi
import edu.gymtonic_app.ui.viewmodel.RoutineExerciseUi

class RoutineRepository(
	private val remoteDataSource: RemoteDataSource
) {

	// Devuelve las categorias de entrenamientos desde capa remota (actualmente payload temporal).
	suspend fun getTrainingCategories(): Result<List<RemoteTrainingCategory>> {
		return runCatching {
			remoteDataSource.getTrainingCategories()
		}
	}

	// Construye el indice por id a partir del mock remoto temporal para evitar duplicidad de datos.
	private fun routinesById(details: List<RoutineDetailDto>): Map<String, RoutineDetailUi> {
		return details.associate { dto ->
			dto.routineId to mapRoutineDetailDtoToUi(dto)
		}
	}

	suspend fun getRoutineFromMock(routineId: String): RoutineDetailUi {
		val byId = routinesById(remoteDataSource.getMockRoutineDetails())
		return byId[routineId] ?: byId["fullbody"] ?: RoutineDetailUi(
			id = "fullbody",
			title = "FullBody",
			exercises = emptyList()
		)
	}

	suspend fun getAllRoutinesFromMock(): List<RoutineDetailUi> {
		return routinesById(remoteDataSource.getMockRoutineDetails()).values.toList()
	}

	// Ruta remote-first para listado de rutinas, con fallback temporal al hardcode local.
	suspend fun getRoutinesFromApi(): Result<List<RoutineDetailUi>> {
		return runCatching {
			val fallbackById = routinesById(remoteDataSource.getMockRoutineDetails())
			remoteDataSource.getRoutinesFromApi().map { dto ->
				val fallbackRoutine = fallbackById[dto.routineId] ?: fallbackById["fullbody"]
				RoutineDetailUi(
					id = dto.routineId,
					title = dto.routineName,
					exercises = fallbackRoutine?.exercises ?: emptyList()
				)
			}
		}
	}

	// Ruta remote-first para detalle por id, con fallback progresivo al catálogo local.
	suspend fun getRoutineByIdFromApi(routineId: String): Result<RoutineDetailUi> {
		return runCatching {
			mapRoutineDetailDtoToUi(remoteDataSource.getRoutineByIdFromApi(routineId))
		}.recoverCatching {
			getRoutineFromMock(routineId)
		}
	}

	private fun mapRoutineDetailDtoToUi(dto: RoutineDetailDto): RoutineDetailUi {
		return RoutineDetailUi(
			id = dto.routineId,
			title = dto.routineName,
			exercises = dto.exercises.map { exercise ->
				mapRoutineExerciseDtoToUi(exercise)
			}
		)
	}

	private fun mapRoutineExerciseDtoToUi(dto: RoutineExerciseDto): RoutineExerciseUi {
		return RoutineExerciseUi(
			name = dto.name,
			reps = dto.reps,
			imageRes = imageResFromKey(dto.imageKey)
		)
	}

	private fun imageResFromKey(imageKey: String?): Int {
		return when (imageKey) {
			"espalda" -> R.drawable.espalda
			"fullbody" -> R.drawable.fullbody
			"pushup" -> R.drawable.pushup
			"estiramientos" -> R.drawable.estiramientos
			"brazo" -> R.drawable.brazo
			"pierna" -> R.drawable.pierna
			"estocadas" -> R.drawable.estocadas
			"pressbanca" -> R.drawable.pressbanca
			"pullover" -> R.drawable.pullover
			"remo" -> R.drawable.remo
			"sentadilla" -> R.drawable.sentadilla
			"pesomuerto" -> R.drawable.pesomuerto
			else -> R.drawable.fullbody
		}
	}
}
