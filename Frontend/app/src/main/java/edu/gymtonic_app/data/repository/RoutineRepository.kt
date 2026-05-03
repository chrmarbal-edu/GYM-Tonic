package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.mapper.normalizeRoutineKey
import edu.gymtonic_app.data.mapper.toDomain
import edu.gymtonic_app.data.remote.datasource.RoutineRemoteDataSource
import edu.gymtonic_app.data.remote.model.routine.RoutineDetailDto
import edu.gymtonic_app.domain.model.routine.RoutineDetail
import edu.gymtonic_app.domain.model.routine.RoutineExercise

class RoutineRepository(
	private val routineRemoteDataSource: RoutineRemoteDataSource = RoutineRemoteDataSource()
) {

	// Construye el indice por id a partir del mock remoto temporal para evitar duplicidad de datos.
	private fun routinesById(details: List<RoutineDetailDto>): Map<String, RoutineDetail> {
		return details.associate { dto ->
			dto.routineId to dto.toDomain()
		}
	}

	private fun routinesBySlug(details: List<RoutineDetailDto>): Map<String, RoutineDetail> {
		return details.associate { dto ->
			normalizeRoutineKey(dto.routineName) to dto.toDomain()
		}
	}

	suspend fun getRoutineFromMock(routineId: String): RoutineDetail {
		val byId = routinesById(routineRemoteDataSource.getMockRoutineDetails())
		return byId[routineId] ?: byId["fullbody"] ?: RoutineDetail(
			id = "fullbody",
			title = "FullBody",
			exercises = emptyList()
		)
	}

	suspend fun getAllRoutinesFromMock(): List<RoutineDetail> {
		return routinesById(routineRemoteDataSource.getMockRoutineDetails()).values.toList()
	}

	// Ruta remote-first para listado de rutinas, con fallback temporal al hardcode local.
	suspend fun getRoutinesFromApi(): Result<List<RoutineDetail>> {
		return runCatching {
			val fallbackById = routinesById(routineRemoteDataSource.getMockRoutineDetails())
			routineRemoteDataSource.getRoutinesFromApi().map { dto ->
				val fallbackRoutine = fallbackById[dto.routineId] ?: fallbackById["fullbody"]
				RoutineDetail(
					id = dto.routineId,
					title = dto.routineName,
					exercises = fallbackRoutine?.exercises ?: emptyList()
				)
			}
		}
	}

	// Ruta remote-first para detalle por id, con fallback progresivo al catalogo local.
	suspend fun getRoutineByIdFromApi(routineId: String): Result<RoutineDetail> {
		return runCatching {
			val mockDetails = routineRemoteDataSource.getMockRoutineDetails()
			val fallbackById = routinesById(mockDetails)
			val fallbackBySlug = routinesBySlug(mockDetails)
			val dto = routineRemoteDataSource.getRoutineByIdFromApi(routineId)
			val routineData = dto.toDomain()
			val shouldUseExerciseFallback =
				routineData.exercises.isEmpty() && !isNumericRoutineId(routineId)

			if (!shouldUseExerciseFallback) {
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

	private fun isNumericRoutineId(routineId: String): Boolean {
		return routineId.matches(Regex("^\\d+$"))
	}

	private fun resolveExercisesFallback(
		requestedRoutineId: String,
		routineName: String,
		fallbackById: Map<String, RoutineDetail>,
		fallbackBySlug: Map<String, RoutineDetail>
	): List<RoutineExercise> {
		return (
			fallbackById[requestedRoutineId]
				?: fallbackBySlug[normalizeRoutineKey(routineName)]
				?: fallbackById["fullbody"]
				?: fallbackBySlug["fullbody"]
			)?.exercises ?: emptyList()
	}

}
