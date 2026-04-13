package edu.gymtonic_app.data.remote

import android.util.Log
import edu.gymtonic_app.data.remote.datasource.model.Login.LoginRequest
import edu.gymtonic_app.data.remote.datasource.model.Login.LoginResponse
import edu.gymtonic_app.data.remote.datasource.model.RegisterRequest
import edu.gymtonic_app.data.remote.datasource.model.RegisterResponse
import edu.gymtonic_app.data.remote.datasource.model.RoutineDetailDto
import edu.gymtonic_app.data.remote.datasource.model.RoutineDto
import edu.gymtonic_app.data.remote.datasource.model.RoutineExerciseDto
import edu.gymtonic_app.data.remote.datasource.services.RetrofitClient

data class RemoteTrainingRoutine(
    val id: String,
    val title: String,
    val imageKey: String
)

data class RemoteTrainingCategory(
    val id: String,
    val title: String,
    val routines: List<RemoteTrainingRoutine>
)

data class RemoteWeeklyGoal(
    val title: String,
    val progressLabel: String,
    val pointsLabel: String,
    val progress: Float
)

data class RemoteCalendarDay(
    val dayIndex: Int,
    val didWorkout: Boolean,
    val isClosedDay: Boolean
)

class RemoteDataSource {
    //Para logear
    private val TAG = RemoteDataSource::class.java.simpleName
    private val api = RetrofitClient.apiService

    // Fuente temporal de detalle de rutinas; se usa como fallback mientras el backend se estabiliza.
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

    // Función para obtener el login, se pasa el objeto RequestLogin en el body.
    // Se devuelve un objeto LoginResponse.
    suspend fun login(request: LoginRequest): LoginResponse {
        val response = api.login(request)
        if (response.isSuccessful) { //si es 200
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error: ${response.message()} | $errorBody")
            throw Exception("Error en login: ${response.message()}")
        }
    }

    suspend fun register(request: RegisterRequest): RegisterResponse {
        val response = api.register(request)
        if(response.isSuccessful){
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        } else{
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error: ${response.message()} | $errorBody")
            throw Exception("Error en login: ${response.message()}")
        }
    }

    suspend fun logout() {
        val response = api.logout()
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error logout: ${response.code()} ${response.message()} | $errorBody")
            throw Exception("Error en logout: ${response.message()}")
        }
    }

    suspend fun getTrainingCategories(): List<RemoteTrainingCategory> {
        // Payload temporal en capa remota. Cuando el endpoint exista, esta función hará la petición real.
        return listOf(
            RemoteTrainingCategory(
                id = "recent",
                title = "Recientes",
                routines = listOf(
                    RemoteTrainingRoutine("back", "Espalda", "espalda"),
                    RemoteTrainingRoutine("fullbody", "Full Body", "fullbody"),
                    RemoteTrainingRoutine("push", "Empujes", "pushup")
                )
            ),
            RemoteTrainingCategory(
                id = "beginners",
                title = "Para Principiantes",
                routines = listOf(
                    RemoteTrainingRoutine("stretch", "Estiramientos", "estiramientos"),
                    RemoteTrainingRoutine("arm", "Brazo", "brazo"),
                    RemoteTrainingRoutine("calves", "Gemelos", "pierna")
                )
            ),
            RemoteTrainingCategory(
                id = "muscle_groups",
                title = "Por Grupo Muscular",
                routines = listOf(
                    RemoteTrainingRoutine("calves", "Gemelos", "pierna"),
                    RemoteTrainingRoutine("arm", "Brazo", "brazo"),
                    RemoteTrainingRoutine("back", "Espalda", "espalda")
                )
            ),
            RemoteTrainingCategory(
                id = "recommended",
                title = "Recomendados",
                routines = listOf(
                    RemoteTrainingRoutine("fullbody", "Full Body", "fullbody"),
                    RemoteTrainingRoutine("push", "Empujes", "pushup"),
                    RemoteTrainingRoutine("stretch", "Estiramientos", "estiramientos")
                )
            )
        )
    }

    // Mock temporal para listado de rutinas, útil cuando la API no esté disponible.
    suspend fun getRoutines(): List<RoutineDto> {
        return mockRoutineDetails.map { detail ->
            RoutineDto(
                routineId = detail.routineId,
                routineName = detail.routineName,
                imageKey = detail.exercises.firstOrNull()?.imageKey
            )
        }
    }

    // Mock temporal para detalle de rutina, mantiene fallback a fullbody.
    suspend fun getRoutineById(routineId: String): RoutineDetailDto {
        return mockRoutineDetails.firstOrNull { it.routineId == routineId }
            ?: mockRoutineDetails.first { it.routineId == "fullbody" }
    }

    // Expone la fuente mock para que el repositorio pueda mapearla a modelos de UI.
    fun getMockRoutineDetails(): List<RoutineDetailDto> {
        return mockRoutineDetails
    }

    // Llamada real de listado de rutinas con fallback a mock local.
    suspend fun getRoutinesFromApi(): List<RoutineDto> {
        return try {
            val response = api.getRoutines()
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Respuesta vacia del servidor")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Error routines: ${response.code()} ${response.message()} | $errorBody")
                getRoutines()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error routines exception: ${e.message}")
            getRoutines()
        }
    }

    // Llamada real de detalle por id con fallback a mock local.
    suspend fun getRoutineByIdFromApi(routineId: String): RoutineDetailDto {
        return try {
            val response = api.getRoutineById(routineId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Respuesta vacia del servidor")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Error routine detail: ${response.code()} ${response.message()} | $errorBody")
                getRoutineById(routineId)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error routine detail exception: ${e.message}")
            getRoutineById(routineId)
        }
    }

    suspend fun getWeeklyGoals(): List<RemoteWeeklyGoal> {
        // Payload temporal en capa remota para el módulo Semana.
        return listOf(
            RemoteWeeklyGoal(
                title = "Entrenar 5 dias a la semana",
                progressLabel = "2/5",
                pointsLabel = "+ 300 pts",
                progress = 0.40f
            ),
            RemoteWeeklyGoal(
                title = "Quemar 1000 kcal",
                progressLabel = "882/1000",
                pointsLabel = "+ 120 pts",
                progress = 0.88f
            ),
            RemoteWeeklyGoal(
                title = "Manten la racha 2 dias seguidos",
                progressLabel = "1/2",
                pointsLabel = "+ 50 pts",
                progress = 0.50f
            )
        )
    }

    suspend fun getWeeklyCalendarDays(): List<RemoteCalendarDay> {
        // dayIndex 0..27 para una grilla de 4x7.
        return listOf(
            RemoteCalendarDay(0, true, true),
            RemoteCalendarDay(1, false, true),
            RemoteCalendarDay(2, true, true),
            RemoteCalendarDay(3, true, true),
            RemoteCalendarDay(4, false, true),
            RemoteCalendarDay(5, false, true),
            RemoteCalendarDay(6, false, true),
            RemoteCalendarDay(7, false, true),
            RemoteCalendarDay(8, true, true),
            RemoteCalendarDay(9, true, true),
            RemoteCalendarDay(10, false, true),
            RemoteCalendarDay(11, true, true),
            RemoteCalendarDay(12, false, true),
            RemoteCalendarDay(13, false, true),
            RemoteCalendarDay(14, true, true),
            RemoteCalendarDay(15, false, true),
            RemoteCalendarDay(16, true, true),
            RemoteCalendarDay(17, false, false),
            RemoteCalendarDay(18, false, false),
            RemoteCalendarDay(19, false, false),
            RemoteCalendarDay(20, false, false),
            RemoteCalendarDay(21, false, false),
            RemoteCalendarDay(22, false, false),
            RemoteCalendarDay(23, false, false),
            RemoteCalendarDay(24, false, false),
            RemoteCalendarDay(25, false, false),
            RemoteCalendarDay(26, false, false),
            RemoteCalendarDay(27, false, false)
        )
    }
}