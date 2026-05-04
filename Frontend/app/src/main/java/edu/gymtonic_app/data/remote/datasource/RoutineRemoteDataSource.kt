package edu.gymtonic_app.data.remote.datasource

import android.util.Log
import edu.gymtonic_app.data.remote.model.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.model.routine.RoutineDto
import edu.gymtonic_app.data.remote.model.routine.RoutineExerciseDto
import edu.gymtonic_app.data.remote.services.RetrofitClient

class RoutineRemoteDataSource {
    private val tag = RoutineRemoteDataSource::class.java.simpleName
    private val api = RetrofitClient.apiService

    // FALLBACK TEMPORAL: respaldo local minimo para no romper la UX si el backend falla.
    private val fallbackRoutineDetails: List<RoutineDetailDto> = listOf(
        RoutineDetailDto(
            routineId = "fullbody",
            routineName = "Full Body",
            exercises = listOf(
                RoutineExerciseDto(name = "SENTADILLA", reps = "x12", imageKey = "sentadilla"),
                RoutineExerciseDto(name = "PRESS BANCA", reps = "x12", imageKey = "pressbanca")
            )
        ),
        RoutineDetailDto(
            routineId = "back",
            routineName = "Espalda",
            exercises = listOf(
                RoutineExerciseDto(name = "REMO", reps = "x12", imageKey = "remo"),
                RoutineExerciseDto(name = "PESO MUERTO", reps = "x10", imageKey = "pesomuerto")
            )
        ),
        RoutineDetailDto(
            routineId = "arm",
            routineName = "Brazo",
            exercises = listOf(
                RoutineExerciseDto(name = "CURL BICEPS", reps = "x12", imageKey = "brazo"),
                RoutineExerciseDto(name = "EXTENSION TRICEPS", reps = "x12", imageKey = "brazo")
            )
        ),
        RoutineDetailDto(
            routineId = "calves",
            routineName = "Gemelos",
            exercises = listOf(
                RoutineExerciseDto(name = "ELEVACION TALONES", reps = "x20", imageKey = "pierna"),
                RoutineExerciseDto(name = "ESTOCADAS", reps = "x12", imageKey = "estocadas")
            )
        ),
        RoutineDetailDto(
            routineId = "push",
            routineName = "Empujes",
            exercises = listOf(
                RoutineExerciseDto(name = "PUSH UPS", reps = "x15", imageKey = "pushup"),
                RoutineExerciseDto(name = "FONDOS", reps = "x12", imageKey = "pushup")
            )
        ),
        RoutineDetailDto(
            routineId = "stretch",
            routineName = "Estiramientos",
            exercises = listOf(
                RoutineExerciseDto(name = "MOVILIDAD HOMBRO", reps = "x30s", imageKey = "estiramientos"),
                RoutineExerciseDto(name = "ISQUIOS", reps = "x30s", imageKey = "estiramientos")
            )
        )
    )

    // FALLBACK TEMPORAL: usado solo cuando falla el consumo principal.
    suspend fun getRoutines(): List<RoutineDto> {
        return fallbackRoutineDetails.map { detail ->
            RoutineDto(
                routineId = detail.routineId,
                routineName = detail.routineName,
                imageKey = detail.safeExercises().firstOrNull()?.resolvedImageKey()
            )
        }
    }

    // FALLBACK TEMPORAL: usado solo cuando falla el detalle remoto.
    suspend fun getRoutineById(routineId: String): RoutineDetailDto {
        return fallbackRoutineDetails.firstOrNull { it.routineId == routineId }
            ?: fallbackRoutineDetails.first { it.routineId == "fullbody" }
    }

    // FALLBACK TEMPORAL: dependencia de compatibilidad para el repositorio actual.
    fun getMockRoutineDetails(): List<RoutineDetailDto> {
        return fallbackRoutineDetails
    }

    suspend fun getRoutinesFromApi(): List<RoutineDto> {
        // PRIMARY: consumo real del backend.
        return try {
            val response = api.getRoutines()
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Respuesta vacia del servidor")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error routines: ${response.code()} ${response.message()} | $errorBody")
                // FALLBACK TEMPORAL: solo si el backend falla.
                getRoutines()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error routines exception: ${e.message}")
            // FALLBACK TEMPORAL: solo si el backend falla.
            getRoutines()
        }
    }

    suspend fun getRoutineByIdFromApi(routineId: String): RoutineDetailDto {
        // PRIMARY: siempre intenta detalle remoto por /routine/{id}/with-exercises.
        return try {
            val resolvedRoutineId = resolveRoutineIdForDetail(routineId)
            val detailResponse = api.getRoutineWithExercisesById(resolvedRoutineId)

            if (detailResponse.isSuccessful) {
                detailResponse.body() ?: throw Exception("Respuesta vacia del servidor")
            } else {
                val errorBody = detailResponse.errorBody()?.string()
                Log.e(
                    tag,
                    "Error routine detail: ${detailResponse.code()} ${detailResponse.message()} | $errorBody"
                )
                // FALLBACK TEMPORAL: solo si el backend falla.
                getRoutineById(routineId)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error routine detail exception: ${e.message}")
            // FALLBACK TEMPORAL: solo si el backend falla.
            getRoutineById(routineId)
        }
    }

    private suspend fun resolveRoutineIdForDetail(routineId: String): String {
        if (isNumericRoutineId(routineId)) {
            return routineId
        }

        val response = api.getRoutineByName(routineId)
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            Log.e(tag, "Error routine by name: ${response.code()} ${response.message()} | $errorBody")
            throw Exception("No se pudo resolver la rutina por nombre")
        }

        val routineByName = response.body()
            ?: throw Exception("Respuesta vacia al buscar rutina por nombre")
        val resolvedRoutineId = routineByName.routineId

        if (!isNumericRoutineId(resolvedRoutineId)) {
            throw Exception("La rutina resuelta no tiene id numerico")
        }

        return resolvedRoutineId
    }

    private fun isNumericRoutineId(routineId: String): Boolean {
        return routineId.matches(Regex("^\\d+$"))
    }
}
