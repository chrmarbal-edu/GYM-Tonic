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
    fun getUserCreatedRoutines(): Flow<List<RoutineEntity>> {
        return routineLocalDataSource.getAllUserRoutines()
    }

    suspend fun createUserRoutine(
        routineName: String,
        imageKey: String? = null
    ): Result<Long> {
        return runCatching {
            val exists = routineLocalDataSource.existsRoutineWithName(routineName)
            if (exists) {
                throw IllegalArgumentException("Ya existe una rutina con ese nombre")
            }

            val routine = RoutineEntity(
                routine_name = routineName,
                routine_image = imageKey
            )
            routineLocalDataSource.createOrUpdateRoutine(routine)
        }
    }

    suspend fun updateUserRoutine(
        routineId: Int,
        routineName: String,
        imageKey: String? = null
    ): Result<Int> {
        return runCatching {
            val routine = RoutineEntity(
                routine_id = routineId,
                routine_name = routineName,
                routine_image = imageKey
            )
            routineLocalDataSource.updateRoutine(routine)
        }
    }

    suspend fun deleteUserRoutine(routineId: Int): Result<Int> {
        return runCatching {
            routineLocalDataSource.deleteRoutineById(routineId)
        }
    }

    suspend fun getUserRoutineWithExercises(routineId: Int): Result<RoutineEntity?> {
        return runCatching {
            routineLocalDataSource.getRoutineWithExercises(routineId)
        }
    }
    //endregion

    //region Retrofit
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

    suspend fun getRoutineWithExercisesByIdFromApi(routineId: Int): Result<RoutineDetailDto> {
        return runCatching {
            unwrapOne(
                response = routineRemoteDataSource.getRoutineWithExercisesById(routineId),
                defaultMessage = "No se pudo obtener el detalle de la rutina con id=$routineId"
            )
        }
    }

    suspend fun getRoutineByIdFromApi(routineId: Int): Result<RoutineDto> {
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
        routineId: Int,
        request: Map<String, Any?>
    ): Result<RoutineDto> {
        return runCatching {
            unwrapOne(
                response = routineRemoteDataSource.updateRoutine(routineId, request),
                defaultMessage = "No se pudo actualizar la rutina con id=$routineId"
            )
        }
    }

    suspend fun deleteRoutineFromApi(routineId: Int): Result<Unit> {
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
