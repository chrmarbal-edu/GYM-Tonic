package edu.gymtonic_app.data.repository

import android.content.Context
import android.util.Log
import edu.gymtonic_app.data.local.localDatasource.exercise.ExerciseLocalDataSource
import edu.gymtonic_app.data.local.localDatasource.routine.RoutineLocalDataSource
import edu.gymtonic_app.data.local.localDatasource.routineExercise.RoutineExerciseInsert
import edu.gymtonic_app.data.local.localDatasource.routineExercise.RoutineExerciseLocalDataSource
import edu.gymtonic_app.data.mapper.toDto
import edu.gymtonic_app.data.mapper.toEntity
import edu.gymtonic_app.data.remote.remoteDatasource.RoutineRemoteDataSource
import com.google.gson.Gson
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDto
import edu.gymtonic_app.data.remote.remoteModel.training.TrainingCategoryDto
import edu.gymtonic_app.data.util.MediaCacheManager
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

class RoutineRepository(
    private val routineRemoteDataSource: RoutineRemoteDataSource,
    private val routineLocalDataSource: RoutineLocalDataSource? = null,
    private val exerciseLocalDataSource: ExerciseLocalDataSource? = null,
    private val routineExerciseLocalDataSource: RoutineExerciseLocalDataSource? = null,
    private val context: Context? = null
) {
    suspend fun getRoutinesFromApi(userId: Int? = null): Result<List<RoutineDto>> {
        return runCatching {
            try {
                val remoteRoutines = unwrapList(
                    response = routineRemoteDataSource.getRoutines(),
                    defaultMessage = "No se pudieron obtener las rutinas"
                )
                // Cache personal routines with actual owner ID
                userId?.let { uid ->
                    routineLocalDataSource?.insertRoutines(remoteRoutines.map { it.toEntity(uid) })
                }
                remoteRoutines
            } catch (e: Exception) {
                Log.d("RoutineRepository", "Offline: loading local routines for user $userId")
                val cached = if (userId != null) {
                    routineLocalDataSource?.getRoutinesByOwner(userId)?.first() ?: emptyList()
                } else {
                    routineLocalDataSource?.getAllRoutines() ?: emptyList()
                }
                cached.map { it.toDto() }
            }
        }
    }

    suspend fun getRoutineCategoriesFromApi(): Result<List<TrainingCategoryDto>> {
        return runCatching {
            try {
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
            } catch (e: Exception) {
                Log.d("RoutineRepository", "Offline: building cached category")
                val localRoutines = routineLocalDataSource?.getAllRoutines() ?: emptyList()
                if (localRoutines.isNotEmpty()) {
                    listOf(
                        TrainingCategoryDto(
                            id = "recent",
                            title = "Mis Rutinas (Offline)",
                            routines = localRoutines.map { entity ->
                                edu.gymtonic_app.data.remote.remoteModel.training.TrainingRoutineDto(
                                    routine_id = entity.routine_id,
                                    routine_name = entity.routine_name,
                                    routine_image = entity.routine_image
                                )
                            }
                        )
                    )
                } else {
                    throw e
                }
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

    suspend fun getRoutineWithExercisesByIdFromApi(routineId: Int, userId: Int? = null): Result<RoutineDetailDto> {
        return runCatching {
            try {
                val response = routineRemoteDataSource.getRoutineWithExercisesById(routineId)
                if (response.isSuccessful) {
                    val remoteRoutine = response.body() ?: throw Exception("Body vacío")

                    // Cache only if user can edit (is their routine)
                    if (remoteRoutine.can_edit || remoteRoutine.routine_id > 0) { 
                        Log.d("RoutineRepository", "Caching routine: ${remoteRoutine.routine_id}")
                        context?.let { ctx ->
                            val localRoutineImg = MediaCacheManager.downloadAndCache(ctx, remoteRoutine.routine_image)
                            val routineToCache = remoteRoutine.copy(routine_image = localRoutineImg)
                            
                            routineLocalDataSource?.insertRoutines(listOf(routineToCache.toEntity(userId ?: 0)))
                            
                            remoteRoutine.exercises?.forEach { exDto ->
                                val localExImg = MediaCacheManager.downloadAndCache(ctx, exDto.exercise_image)
                                val localExVid = MediaCacheManager.downloadAndCache(ctx, exDto.exercise_video)
                                
                                val exEntity = edu.gymtonic_app.data.local.localModel.ExerciseEntity(
                                    exercise_id = exDto.exercise_id,
                                    exercise_name = exDto.exercise_name ?: "",
                                    exercise_description = exDto.exercise_description ?: "",
                                    exercise_type = exDto.exercise_type,
                                    exercise_image = localExImg,
                                    exercise_video = localExVid,
                                    is_favorite = exerciseLocalDataSource?.getExerciseById(exDto.exercise_id)?.is_favorite ?: false
                                )
                                exerciseLocalDataSource?.insertExercise(exEntity)
                                
                                routineExerciseLocalDataSource?.linkExerciseToRoutine(
                                    routineId = routineId,
                                    exerciseId = exDto.exercise_id,
                                    reps = exDto.reps ?: ""
                                )
                            }
                        }
                    }
                    remoteRoutine
                } else {
                    // Fallback on HTTP error
                    loadRoutineFromCache(routineId) ?: throw Exception("Error API ${response.code()}")
                }
            } catch (e: Exception) {
                Log.d("RoutineRepository", "Error fetching routine $routineId, loading from cache")
                loadRoutineFromCache(routineId) ?: throw e
            }
        }
    }

    private suspend fun loadRoutineFromCache(routineId: Int): RoutineDetailDto? {
        val localRoutine = routineLocalDataSource?.getRoutineById(routineId) ?: return null
        val routineDto = localRoutine.toDto()
        
        // Fetch linked exercises from Room
        // Note: RoutineExerciseLocalDataSource.getExercisesForRoutine returns a Flow. 
        // We need to collect it or get the first emission.
        val linkedExercises = routineExerciseLocalDataSource?.getExercisesForRoutine(routineId)?.first() ?: emptyList()
        
        val exercisesDto = linkedExercises.map { rel ->
            edu.gymtonic_app.data.remote.remoteModel.routine.RoutineExerciseDto(
                exercise_id = rel.exercise.exercise_id,
                exercise_name = rel.exercise.exercise_name,
                exercise_description = rel.exercise.exercise_description,
                exercise_type = rel.exercise.exercise_type,
                exercise_image = rel.exercise.exercise_image,
                exercise_video = rel.exercise.exercise_video,
                reps = rel.reps,
                series = 0
            )
        }

        return RoutineDetailDto(
            routine_id = routineDto.routine_id,
            routine_name = routineDto.routine_name,
            routine_image = routineDto.routine_image,
            can_edit = true,
            exercises = exercisesDto
        )
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
        isPersonal: Boolean = true,
        userId: Int? = null
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
                ).also { detail ->
                    // Cache the new routine
                    context?.let { ctx ->
                        val localImg = MediaCacheManager.downloadAndCache(ctx, detail.routine_image)
                        val entity = detail.copy(routine_image = localImg).toEntity(userId ?: 0)
                        routineLocalDataSource?.insertRoutines(listOf(entity))
                    }
                }
            } else {
                unwrapOne(
                    response = routineRemoteDataSource.updateRoutineMultipart(
                        routineId = id,
                        name = name.toRequestBody("text/plain".toMediaTypeOrNull()),
                        exercises = exercisesBody,
                        image = imagePart
                    ),
                    defaultMessage = "No se pudo actualizar la rutina con id=$id"
                ).also { detail ->
                    // Cache the updated routine
                    context?.let { ctx ->
                        val localImg = MediaCacheManager.downloadAndCache(ctx, detail.routine_image)
                        val entity = detail.copy(routine_image = localImg).toEntity(userId ?: 0)
                        routineLocalDataSource?.insertRoutines(listOf(entity))
                    }
                }
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
