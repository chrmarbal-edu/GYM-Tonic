package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.mapper.toDomain
import edu.gymtonic_app.data.remote.datasource.RoutineRemoteDataSource
import edu.gymtonic_app.data.remote.model.routine.RoutineDetailDto
import edu.gymtonic_app.domain.model.routine.RoutineDetail

class RoutineRepository(
    private val routineRemoteDataSource: RoutineRemoteDataSource = RoutineRemoteDataSource()
) {

    private fun routinesById(details: List<RoutineDetailDto>): Map<String, RoutineDetail> {
        return details.associate { dto ->
            dto.routineId to dto.toDomain()
        }
    }

    // FALLBACK TEMPORAL: solo si la API falla.
    suspend fun getRoutineFromMock(routineId: String): RoutineDetail {
        val byId = routinesById(routineRemoteDataSource.getMockRoutineDetails())
        val fallbackRoutine = byId[routineId] ?: byId.values.firstOrNull()

        return fallbackRoutine ?: RoutineDetail(
            id = routineId.ifBlank { "0" },
            title = "Rutina",
            exercises = emptyList()
        )
    }

    suspend fun getAllRoutinesFromMock(): List<RoutineDetail> {
        return routinesById(routineRemoteDataSource.getMockRoutineDetails()).values.toList()
    }

    // Ruta remote-first para listado de rutinas.
    suspend fun getRoutinesFromApi(): Result<List<RoutineDetail>> {
        return runCatching {
            val fallbackById = routinesById(routineRemoteDataSource.getMockRoutineDetails())
            routineRemoteDataSource.getRoutinesFromApi().map { dto ->
                RoutineDetail(
                    id = dto.routineId,
                    title = dto.routineName,
                    exercises = fallbackById[dto.routineId]?.exercises ?: emptyList()
                )
            }
        }
    }

    // Ruta remote-first para detalle por id real de backend.
    suspend fun getRoutineByIdFromApi(routineId: String): Result<RoutineDetail> {
        return runCatching {
            routineRemoteDataSource.getRoutineByIdFromApi(routineId).toDomain()
        }.recoverCatching {
            getRoutineFromMock(routineId)
        }
    }
}
