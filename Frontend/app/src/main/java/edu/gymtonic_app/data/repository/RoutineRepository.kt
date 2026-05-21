package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.local.localDatasource.routine.RoutineLocalDataSource
import edu.gymtonic_app.data.remote.remoteDatasource.RoutineRemoteDataSource
import com.google.gson.Gson
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDto
import edu.gymtonic_app.data.remote.remoteModel.training.TrainingCategoryDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

class RoutineRepository(
    private val routineRemoteDataSource: RoutineRemoteDataSource,
    private val routineLocalDataSource: RoutineLocalDataSource? = null
) {
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
            ).map { category ->
                category.copy(
                    routines = category.routines.map { routine ->
                        routine.copy(
                            routine_name = routine.routine_name?.takeIf { it.isNotBlank() }
                                ?: "Rutina #${routine.routine_id}"
                        )
                    }
                )
            }
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

    suspend fun saveRoutineWithFiles(
        id: Int?,
        name: String,
        exercises: List<edu.gymtonic_app.data.remote.remoteModel.routine.RoutineExerciseDto>,
        imageFile: File?,
        isPersonal: Boolean = true
    ): Result<RoutineDetailDto> {
        return runCatching {
            val gson = Gson()
            val exercisesJson = gson.toJson(exercises.map {
                mapOf(
                    "exercise_id" to it.exercise_id,
                    "sets" to (it.series ?: 0),
                    "reps" to (it.reps ?: "")
                )
            })
            val exercisesBody =
                exercisesJson.toRequestBody("text/plain".toMediaTypeOrNull())
            val imagePart = imageFile?.let {
                MultipartBody.Part.createFormData(
                    "image",
                    it.name,
                    it.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            val isPersonalBody = (if (isPersonal) "1" else "0")
                .toRequestBody("text/plain".toMediaTypeOrNull())

            if (id == null) {
                unwrapOne(
                    response = routineRemoteDataSource.createRoutineMultipart(
                        name = name.toRequestBody("text/plain".toMediaTypeOrNull()),
                        exercises = exercisesBody,
                        isPersonal = isPersonalBody,
                        image = imagePart
                    ),
                    defaultMessage = "No se pudo crear la rutina"
                )
            } else {
                unwrapOne(
                    response = routineRemoteDataSource.updateRoutineMultipart(
                        routineId = id,
                        name = name.toRequestBody("text/plain".toMediaTypeOrNull()),
                        exercises = exercisesBody,
                        image = imagePart
                    ),
                    defaultMessage = "No se pudo actualizar la rutina con id=$id"
                )
            }
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
}
