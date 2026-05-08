package edu.gymtonic_app.data.remote.datasource

import android.util.Log
import edu.gymtonic_app.data.remote.model.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.model.routine.RoutineDto
import edu.gymtonic_app.data.remote.model.routine.RoutineExerciseDto
import edu.gymtonic_app.data.remote.services.RetrofitClient

class RoutineRemoteDataSource {
    private val tag = RoutineRemoteDataSource::class.java.simpleName
    private val api = RetrofitClient.apiService

    // FALLBACK TEMPORAL: solo para emergencia cuando backend no responde.
    private val fallbackRoutineDetails: List<RoutineDetailDto> = listOf(
        RoutineDetailDto(
            routineId = "1",
            routineName = "Full Body Principiante",
            exercises = listOf(
                RoutineExerciseDto(name = "Sentadilla", reps = "x12", imageKey = "squat"),
                RoutineExerciseDto(name = "Press de banca", reps = "x12", imageKey = "bench")
            )
        ),
        RoutineDetailDto(
            routineId = "2",
            routineName = "Tren Superior Avanzado",
            exercises = listOf(
                RoutineExerciseDto(name = "Dominadas", reps = "x12", imageKey = "pullup"),
                RoutineExerciseDto(name = "Remo con barra", reps = "x12", imageKey = "row")
            )
        ),
        RoutineDetailDto(
            routineId = "3",
            routineName = "Cardio Quema Grasa",
            exercises = listOf(
                RoutineExerciseDto(name = "Carrera continua", reps = "x20", imageKey = "running"),
                RoutineExerciseDto(name = "Burpees", reps = "x20", imageKey = "burpee")
            )
        ),
        RoutineDetailDto(
            routineId = "4",
            routineName = "Piernas y Gluteos",
            exercises = listOf(
                RoutineExerciseDto(name = "Sentadilla", reps = "x12", imageKey = "squat"),
                RoutineExerciseDto(name = "Peso muerto", reps = "x12", imageKey = "deadlift")
            )
        ),
        RoutineDetailDto(
            routineId = "5",
            routineName = "Flexibilidad y Movilidad",
            exercises = listOf(
                RoutineExerciseDto(name = "Estiramiento isquios", reps = "x30s", imageKey = "hamstring"),
                RoutineExerciseDto(name = "Yoga - Saludo al sol", reps = "x30s", imageKey = "sunsalute")
            )
        )
    )

    // FALLBACK TEMPORAL: solo si falla el consumo principal.
    suspend fun getRoutines(): List<RoutineDto> {
        return fallbackRoutineDetails.map { detail ->
            RoutineDto(
                routineId = detail.routineId,
                routineName = detail.routineName,
                imageKey = detail.safeExercises().firstOrNull()?.resolvedImageKey()
            )
        }
    }

    // FALLBACK TEMPORAL: solo si falla el detalle remoto.
    suspend fun getRoutineById(routineId: String): RoutineDetailDto {
        return fallbackRoutineDetails.firstOrNull { it.routineId == routineId }
            ?: fallbackRoutineDetails.first()
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
                getRoutines()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error routines exception: ${e.message}")
            getRoutines()
        }
    }

    suspend fun getRoutineByIdFromApi(routineId: String): RoutineDetailDto {
        // PRIMARY: solo endpoint por id real.
        return try {
            val detailResponse = api.getRoutineWithExercisesById(routineId)

            if (detailResponse.isSuccessful) {
                detailResponse.body() ?: throw Exception("Respuesta vacia del servidor")
            } else {
                val errorBody = detailResponse.errorBody()?.string()
                Log.e(
                    tag,
                    "Error routine detail: ${detailResponse.code()} ${detailResponse.message()} | $errorBody"
                )
                getRoutineById(routineId)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error routine detail exception: ${e.message}")
            getRoutineById(routineId)
        }
    }
}
