package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.local.localDatasource.routine.RoutineLocalDataSource
import edu.gymtonic_app.data.local.localModel.rutine.RoutineEntity
import edu.gymtonic_app.data.mapper.toDomain
import edu.gymtonic_app.data.remote.remoteDatasource.RoutineRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDetailDto
import edu.gymtonic_app.domain.model.routine.RoutineDetail
import kotlinx.coroutines.flow.Flow

class RoutineRepository(
    private val routineRemoteDataSource: RoutineRemoteDataSource,
    private val routineLocalDataSource: RoutineLocalDataSource
) {
    //region Room
    /**
     * Obtiene todas las rutinas creadas por el usuario
     * Observa cambios en tiempo real (Flow)
     * Usado por: TrainingScreen para mostrar "Mis rutinas"
     */
    fun getUserCreatedRoutines(): Flow<List<RoutineEntity>> {
        return routineLocalDataSource.getAllUserRoutines()
    }

    /**
     * Crea una nueva rutina para el usuario
     * Retorna ID de la rutina creada
     * Usado por: CreateRoutineScreen al guardar
     */
    suspend fun createUserRoutine(
        routineName: String,
        imageKey: String? = null
    ): Result<Long> {
        return runCatching {
            // Validar nombre único antes de insertar
            val exists = routineLocalDataSource.existsRoutineWithName(routineName)
            if (exists) {
                throw IllegalArgumentException("Ya existe una rutina con ese nombre")
            }

            // Crear entidad y guardar
            val routine = RoutineEntity(
                routine_name = routineName,
                imageKey = imageKey
            )
            routineLocalDataSource.createOrUpdateRoutine(routine)
        }
    }

    /**
     * Actualiza una rutina existente
     * Usado por: pantalla de edición (futuro)
     */
    suspend fun updateUserRoutine(
        routineId: Int,
        routineName: String,
        imageKey: String? = null
    ): Result<Int> {
        return runCatching {
            val routine = RoutineEntity(
                routine_id = routineId,
                routine_name = routineName,
                imageKey = imageKey
            )
            routineLocalDataSource.updateRoutine(routine)
        }
    }

    /**
     * Elimina una rutina y sus ejercicios relacionados
     * Usado por: swipe para borrar en TrainingScreen
     */
    suspend fun deleteUserRoutine(routineId: Int): Result<Int> {
        return runCatching {
            // Nota: También debería borrar registros en routine_x_exercise
            // Lo haremos en RoutineExerciseRepository
            routineLocalDataSource.deleteRoutineById(routineId)
        }
    }

    /**
     * Obtiene los detalles de una rutina con sus ejercicios
     * Usado por: RoutineCatalogScreen si quieres ver detalles de rutina usuario
     */
    suspend fun getUserRoutineWithExercises(routineId: Int): Result<RoutineEntity?> {
        return runCatching {
            routineLocalDataSource.getRoutineWithExercises(routineId)
        }
    }
    //endregion
    //region Retrofit
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
    //endregion
}
