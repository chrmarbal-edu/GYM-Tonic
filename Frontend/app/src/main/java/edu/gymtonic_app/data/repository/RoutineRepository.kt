package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.local.localDatasource.routine.RoutineLocalDataSource
import edu.gymtonic_app.data.local.localModel.rutine.RoutineEntity
import edu.gymtonic_app.data.remote.remoteDatasource.RoutineRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDetailDto
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDto
import edu.gymtonic_app.data.remote.remoteModel.training.TrainingCategoryDto
import retrofit2.Response

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
    // region Retrofit

    suspend fun getRoutinesFromApi(): Result<List<RoutineDto>> {
        return runCatching {
            unwrapList(
                response = routineRemoteDataSource.getRoutines(),
                defaultMessage = "No se pudieron obtener las rutinas"
            )
        }
    }

    suspend fun getRoutineCategoriesFromApi(): Result<List<TrainingCategoryDto>> {
        return runCatching {
            unwrapList(
                response = routineRemoteDataSource.getRoutineCategories(),
                defaultMessage = "No se pudieron obtener las categorías de rutinas"
            )
        }
    }

    suspend fun getRoutineByNameFromApi(name: String): Result<RoutineDto> {
        return runCatching {
            unwrapOne(
                response = routineRemoteDataSource.getRoutineByName(name),
                defaultMessage = "No se pudo obtener la rutina por nombre: $name"
            )
        }
    }

    suspend fun getRoutineWithExercisesByIdFromApi(routineId: String): Result<RoutineDetailDto> {
        return runCatching {
            unwrapOne(
                response = routineRemoteDataSource.getRoutineWithExercisesById(routineId),
                defaultMessage = "No se pudo obtener el detalle de la rutina con id=$routineId"
            )
        }
    }

    suspend fun getRoutineByIdFromApi(routineId: String): Result<RoutineDto> {
        return runCatching {
            unwrapOne(
                response = routineRemoteDataSource.getRoutineById(routineId),
                defaultMessage = "No se pudo obtener la rutina con id=$routineId"
            )
        }
    }

    suspend fun createRoutineFromApi(request: Map<String, Any>): Result<RoutineDto> {
        return runCatching {
            unwrapOne(
                response = routineRemoteDataSource.createRoutine(request),
                defaultMessage = "No se pudo crear la rutina"
            )
        }
    }

    suspend fun updateRoutineFromApi(
        routineId: String,
        request: Map<String, Any?>
    ): Result<RoutineDto> {
        return runCatching {
            unwrapOne(
                response = routineRemoteDataSource.updateRoutine(routineId, request),
                defaultMessage = "No se pudo actualizar la rutina con id=$routineId"
            )
        }
    }

    suspend fun deleteRoutineFromApi(routineId: String): Result<Unit> {
        return runCatching {
            val response = routineRemoteDataSource.deleteRoutine(routineId)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string().orEmpty()
                throw Exception(
                    "Error al eliminar rutina (HTTP ${response.code()}): ${response.message()} $errorBody"
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
    }
    //endregion
}
