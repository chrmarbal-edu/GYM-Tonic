package edu.gymtonic_app.data.remote

import android.util.Log
import edu.gymtonic_app.data.remote.datasource.model.Login.LoginRequest
import edu.gymtonic_app.data.remote.datasource.model.Login.LoginResponse
import edu.gymtonic_app.data.remote.datasource.model.RegisterRequest
import edu.gymtonic_app.data.remote.datasource.model.RegisterResponse
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

class RemoteDataSource {
    //Para logear
    private val TAG = RemoteDataSource::class.java.simpleName
    private val api = RetrofitClient.apiService

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
}