package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.datasource.RoutineRemoteDataSource
import edu.gymtonic_app.data.remote.model.routine.RoutineDetailData
import edu.gymtonic_app.data.remote.model.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.model.routine.RoutineExerciseData
import edu.gymtonic_app.data.remote.model.routine.RoutineExerciseDto
import java.text.Normalizer

class RoutineRepository(
	private val routineRemoteDataSource: RoutineRemoteDataSource = RoutineRemoteDataSource()
) {

	// Construye el indice por id a partir del mock remoto temporal para evitar duplicidad de datos.
	private fun routinesById(details: List<RoutineDetailDto>): Map<String, RoutineDetailData> {
		return details.associate { dto ->
			dto.routineId to mapRoutineDetailDtoToData(dto)
		}
	}

	private fun routinesBySlug(details: List<RoutineDetailDto>): Map<String, RoutineDetailData> {
		return details.associate { dto ->
			normalizeRoutineKey(dto.routineName) to mapRoutineDetailDtoToData(dto)
		}
	}

	suspend fun getRoutineFromMock(routineId: String): RoutineDetailData {
		val byId = routinesById(routineRemoteDataSource.getMockRoutineDetails())
		return byId[routineId] ?: byId["fullbody"] ?: RoutineDetailData(
			id = "fullbody",
			title = "FullBody",
			exercises = emptyList()
		)
	}

	suspend fun getAllRoutinesFromMock(): List<RoutineDetailData> {
		return routinesById(routineRemoteDataSource.getMockRoutineDetails()).values.toList()
	}

	// Ruta remote-first para listado de rutinas, con fallback temporal al hardcode local.
	suspend fun getRoutinesFromApi(): Result<List<RoutineDetailData>> {
		return runCatching {
			val fallbackById = routinesById(routineRemoteDataSource.getMockRoutineDetails())
			routineRemoteDataSource.getRoutinesFromApi().map { dto ->
				val fallbackRoutine = fallbackById[dto.routineId] ?: fallbackById["fullbody"]
				RoutineDetailData(
					id = dto.routineId,
					title = dto.routineName,
					exercises = fallbackRoutine?.exercises ?: emptyList()
				)
			}
		}
	}

	// Ruta remote-first para detalle por id, con fallback progresivo al catalogo local.
	suspend fun getRoutineByIdFromApi(routineId: String): Result<RoutineDetailData> {
		return runCatching {
			val mockDetails = routineRemoteDataSource.getMockRoutineDetails()
			val fallbackById = routinesById(mockDetails)
			val fallbackBySlug = routinesBySlug(mockDetails)
			val dto = routineRemoteDataSource.getRoutineByIdFromApi(routineId)
			val routineData = mapRoutineDetailDtoToData(dto)

			if (dto.safeExercises().isNotEmpty()) {
				routineData
			} else {
				routineData.copy(
					exercises = resolveExercisesFallback(
						requestedRoutineId = routineId,
						routineName = dto.routineName,
						fallbackById = fallbackById,
						fallbackBySlug = fallbackBySlug
					)
				)
			}
		}.recoverCatching {
			getRoutineFromMock(routineId)
		}
	}

	private fun mapRoutineDetailDtoToData(dto: RoutineDetailDto): RoutineDetailData {
		return RoutineDetailData(
			id = dto.routineId,
			title = dto.routineName,
			exercises = dto.safeExercises().mapIndexed { index, exercise ->
				mapRoutineExerciseDtoToData(
					dto = exercise,
					routineId = dto.routineId,
					index = index
				)
			}
		)
	}

	private fun mapRoutineExerciseDtoToData(
		dto: RoutineExerciseDto,
		routineId: String,
		index: Int
	): RoutineExerciseData {
		val fallbackId = "$routineId-${slugify(dto.name)}-$index"
		return RoutineExerciseData(
			id = dto.exerciseId?.takeIf { it.isNotBlank() } ?: fallbackId,
			name = dto.name,
			reps = dto.reps,
			imageKey = dto.imageKey
		)
	}

	private fun resolveExercisesFallback(
		requestedRoutineId: String,
		routineName: String,
		fallbackById: Map<String, RoutineDetailData>,
		fallbackBySlug: Map<String, RoutineDetailData>
	): List<RoutineExerciseData> {
		return (
			fallbackById[requestedRoutineId]
				?: fallbackBySlug[normalizeRoutineKey(routineName)]
				?: fallbackById["fullbody"]
				?: fallbackBySlug["fullbody"]
			)?.exercises ?: emptyList()
	}

	private fun normalizeRoutineKey(raw: String): String {
		val normalized = Normalizer.normalize(raw, Normalizer.Form.NFD)
		return normalized
			.replace("\\p{M}+".toRegex(), "")
			.lowercase()
			.replace("[\\s_-]+".toRegex(), "")
			.replace("[^a-z0-9]".toRegex(), "")
	}

	private fun slugify(raw: String): String {
		return raw
			.lowercase()
			.replace(" ", "-")
			.replace("_", "-")
			.replace("[^a-z0-9-]".toRegex(), "")
	}

}
