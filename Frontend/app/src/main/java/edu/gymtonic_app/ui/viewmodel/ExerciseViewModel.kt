package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.local.GymTonicDatabase
import edu.gymtonic_app.data.local.datasource.local.exercise.ExerciseLocalDataSource
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.remote.datasource.ExerciseRemoteDataSource
import edu.gymtonic_app.data.repository.ExerciseRepository
import edu.gymtonic_app.ui.mapper.ImageResourceMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//region data clas para almacenar los datos del ejercicio de la ui
data class ExerciseDetailUi(
    val id: String,
    val name: String,
    val durationSeconds: Int,
    val imageRes: Int,
    val instructions: List<String>
)
//endregion

//region data clas para almacenar los datos del ejercicio que va a la bd
data class FavoriteExercisePayload(
    val id: String,
    val name: String,
    val description: String = "",
    val type: Int = 0,
    val video: String? = null,
    val image: String? = null
)
//endregion


// region sealed class para almacenar el estado del UI
sealed class ExerciseUiState {
    object Idle : ExerciseUiState() // Estado inicial
    object Loading : ExerciseUiState() // Estado de carga
    data class Success(val exercise: ExerciseDetailUi) : ExerciseUiState() // Estado de éxito
    data class Error(val message: String) : ExerciseUiState() // Estado de error
}
//endregion

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    //region Declarar Acceso a Datos
    private val exerciseRepository: ExerciseRepository
    private val exerciseRemoteDataSource : ExerciseRemoteDataSource
    private val exerciseLocalDataSource : ExerciseLocalDataSource
    //endregion

    // region flow para almacenar el estado del UI
    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Idle)
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()
    // endregion

    //region flow para almacenar el conjunto de favoritos
    private val _favoritesSet = MutableStateFlow<Set<Int>>(emptySet())
    val favoritesSet: StateFlow<Set<Int>> = _favoritesSet.asStateFlow()
    //endregion

    init {
        //Inicializamos el repositorio
        val database = GymTonicDatabase.getInstance(application)
        val dao = database.exerciseDao()

        exerciseRemoteDataSource = ExerciseRemoteDataSource()
        exerciseLocalDataSource = ExerciseLocalDataSource(dao)
        exerciseRepository = ExerciseRepository(exerciseRemoteDataSource, exerciseLocalDataSource)

        //Cargamos los datos de los ejercicios
        observeFavoritesFromRoom()

    }
    //region Metodos

    //Metodo que observa los cambios en la tabla de favoritos de la base de datos
    private fun observeFavoritesFromRoom() {
        viewModelScope.launch {
            exerciseRepository.getFavExercises().collect { favorites ->
                _favoritesSet.value = favorites.map { it.exercise_id }.toSet()
            }
        }
    }

    //Método para saber si un ejercicio es favorito
    fun isFavorite(exerciseId: String): Boolean {
        val parsedId = exerciseId.toIntOrNull() ?: return false
        return _favoritesSet.value.contains(parsedId)
    }

    //Método para alternar el estado de favorito de un ejercicio
    fun onToggleFavorite(payload: FavoriteExercisePayload) {
        // Parseamos el id del ejercicio
        val parsedId = payload.id.toIntOrNull()
        if (parsedId == null) {
            Log.w(TAG, "No se pudo parsear exerciseId=${payload.id} para toggle de favorito")
            return
        }
        // Actualizamos el conjunto de favoritos
        val previous = _favoritesSet.value
        // Si el ejercicio ya estaba en favoritos, lo quitamos, si no, lo añadimos
        val optimistic =
            if (previous.contains(parsedId)) previous - parsedId
            else previous + parsedId
        // Actualizamos el conjunto de favoritos
        _favoritesSet.value = optimistic

        // Construimos el objeto ExerciseEntity
        val entity = ExerciseEntity(
            exercise_id = parsedId,
            exercise_name = payload.name,
            exercise_description = payload.description.ifBlank { "Sin descripción" },
            exercise_type = payload.type,
            exercise_video = payload.video,
            exercise_image = payload.image,
            is_favorite = true
        )
        // Llamamos al repositorio para actualizar la base de datos
        viewModelScope.launch {
            runCatching {
                exerciseRepository.updateFavWord(entity)
            }.onFailure { error ->
                Log.e(TAG, "Error al alternar favorito para exerciseId=${payload.id}", error)
                _favoritesSet.value = previous
            }
        }
    }


    //Funcion para cargar el ejercicio especifico
    fun loadSpecificExercise(exerciseId: String) {
        viewModelScope.launch {
            // marcamos el estado de la ui a cargando
            _uiState.value = ExerciseUiState.Loading

            //realizamos la llamada al repositorio para obtener el ejercicio
            exerciseRepository.getExerciseById(exerciseId)
                //Si es exitoso, actualizamos el estado de la ui a success
                .onSuccess { detail ->
                    _uiState.value = ExerciseUiState.Success(
                        ExerciseDetailUi(
                            id = detail.id,
                            name = detail.name,
                            durationSeconds = detail.durationSeconds,
                            imageRes = ImageResourceMapper.fromKey(detail.imageKey),
                            instructions = detail.instructions
                        )
                    )
                }
                //Si da fallo actualizamos el estado de la ui a error
                .onFailure { error ->
                    _uiState.value = ExerciseUiState.Error(
                        error.message ?: "No se pudo cargar el ejercicio"
                    )
                }
        }
    }
    
    // Este companion object es para poder usar el TAG en el Log
    companion object {
        private const val TAG = "ExerciseViewModel"
    }

}
