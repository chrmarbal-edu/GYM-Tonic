package edu.gymtonic_app.data.remote.datasource

import android.util.Log
import edu.gymtonic_app.data.remote.model.routine.RoutineDetailDto
import edu.gymtonic_app.data.remote.model.routine.RoutineDto
import edu.gymtonic_app.data.remote.model.routine.RoutineExerciseDto
import edu.gymtonic_app.data.remote.services.RetrofitClient

class RoutineRemoteDataSource {
    private val tag = RoutineRemoteDataSource::class.java.simpleName
    private val api = RetrofitClient.apiService

    private val mockRoutineDetails: List<RoutineDetailDto> = listOf(
        RoutineDetailDto(
            routineId = "fullbody",
            routineName = "FullBody",
            exercises = listOf(
                RoutineExerciseDto(name = "ESTOCADAS", reps = "x10", imageKey = "estocadas"),
                RoutineExerciseDto(name = "PRESS BANCA", reps = "x10", imageKey = "pressbanca"),
                RoutineExerciseDto(name = "PULL OVER", reps = "x12", imageKey = "pullover"),
                RoutineExerciseDto(name = "REMO", reps = "x15", imageKey = "remo"),
                RoutineExerciseDto(name = "SENTADILLA", reps = "x15", imageKey = "sentadilla"),
                RoutineExerciseDto(name = "PESO MUERTO", reps = "x20", imageKey = "pesomuerto")
            )
        ),
        RoutineDetailDto(
            routineId = "back",
            routineName = "Espalda",
            exercises = listOf(
                RoutineExerciseDto(name = "JALON AL PECHO", reps = "x12", imageKey = "remo"),
                RoutineExerciseDto(name = "REMO CON BARRA", reps = "x10", imageKey = "remo"),
                RoutineExerciseDto(name = "PESO MUERTO", reps = "x8", imageKey = "pesomuerto"),
                RoutineExerciseDto(name = "PULL OVER", reps = "x12", imageKey = "pullover")
            )
        ),
        RoutineDetailDto(
            routineId = "arm",
            routineName = "Brazo",
            exercises = listOf(
                RoutineExerciseDto(name = "CURL BICEPS", reps = "x12", imageKey = "brazo"),
                RoutineExerciseDto(name = "EXTENSION TRICEPS", reps = "x12", imageKey = "brazo"),
                RoutineExerciseDto(name = "MARTILLO", reps = "x10", imageKey = "brazo"),
                RoutineExerciseDto(name = "FONDOS", reps = "x10", imageKey = "pushup")
            )
        ),
        RoutineDetailDto(
            routineId = "calves",
            routineName = "Gemelos",
            exercises = listOf(
                RoutineExerciseDto(name = "ELEVACION TALONES", reps = "x20", imageKey = "pierna"),
                RoutineExerciseDto(name = "SENTADILLA", reps = "x12", imageKey = "sentadilla"),
                RoutineExerciseDto(name = "ESTOCADAS", reps = "x12", imageKey = "estocadas"),
                RoutineExerciseDto(name = "PRENSA", reps = "x10", imageKey = "pierna")
            )
        ),
        RoutineDetailDto(
            routineId = "push",
            routineName = "Empujes",
            exercises = listOf(
                RoutineExerciseDto(name = "PUSH UPS", reps = "x15", imageKey = "pushup"),
                RoutineExerciseDto(name = "PRESS BANCA", reps = "x10", imageKey = "pressbanca"),
                RoutineExerciseDto(name = "PRESS MILITAR", reps = "x10", imageKey = "pushup"),
                RoutineExerciseDto(name = "FONDOS", reps = "x12", imageKey = "pushup")
            )
        ),
        RoutineDetailDto(
            routineId = "stretch",
            routineName = "Estiramientos",
            exercises = listOf(
                RoutineExerciseDto(name = "MOVILIDAD HOMBRO", reps = "x30s", imageKey = "estiramientos"),
                RoutineExerciseDto(name = "ISQUIOS", reps = "x30s", imageKey = "estiramientos"),
                RoutineExerciseDto(name = "CADERA", reps = "x30s", imageKey = "estiramientos"),
                RoutineExerciseDto(name = "LUMBAR", reps = "x30s", imageKey = "estiramientos")
            )
        )
    )

    suspend fun getRoutines(): List<RoutineDto> {
        return mockRoutineDetails.map { detail ->
            RoutineDto(
                routineId = detail.routineId,
                routineName = detail.routineName,
                imageKey = detail.safeExercises().firstOrNull()?.imageKey
            )
        }
    }

    suspend fun getRoutineById(routineId: String): RoutineDetailDto {
        return mockRoutineDetails.firstOrNull { it.routineId == routineId }
            ?: mockRoutineDetails.first { it.routineId == "fullbody" }
    }

    fun getMockRoutineDetails(): List<RoutineDetailDto> {
        return mockRoutineDetails
    }

    suspend fun getRoutinesFromApi(): List<RoutineDto> {
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
        return try {
            val response = if (isNumericRoutineId(routineId)) {
                api.getRoutineById(routineId)
            } else {
                api.getRoutineByName(routineId)
            }
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Respuesta vacia del servidor")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error routine detail: ${response.code()} ${response.message()} | $errorBody")
                getRoutineById(routineId)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error routine detail exception: ${e.message}")
            getRoutineById(routineId)
        }
    }

    private fun isNumericRoutineId(routineId: String): Boolean {
        return routineId.matches(Regex("^\\d+$"))
    }
}

