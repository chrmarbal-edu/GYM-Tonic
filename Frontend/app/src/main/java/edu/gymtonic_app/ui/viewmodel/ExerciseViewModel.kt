package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.local.GymTonicDatabase
import edu.gymtonic_app.data.local.datasource.local.exercise.ExerciseLocalDataSource
import edu.gymtonic_app.data.remote.datasource.ExerciseRemoteDataSource
import edu.gymtonic_app.data.repository.ExerciseRepository
import edu.gymtonic_app.ui.mapper.ImageResourceMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//region data clas para almacenar los datos del ejercicio
data class ExerciseDetailUi(
    val id: String,
    val name: String,
    val durationSeconds: Int,
    val imageRes: Int,
    val instructions: List<String>
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

    }

    //region Metodos

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
    
    //Función para cargar todos los favoritos de room
    fun loadAllFavoritesFromRoom() {
        viewModelScope.launch {
            exerciseRepository.getFavExercises()
        }
    }
    
    //Funcion para saber si un ejercicio es favorito
    fun isFavorite(exerciseId: String): Boolean {
        //Primero declaramos el id del ejercicio ya que puede ser null
        val parsedId = exerciseId.toIntOrNull() ?: return false
        //Luego comprobamos si el id esta en el conjunto de favoritos
        return _favoritesSet.value.contains(parsedId)
    }
    

    // Metodo para alternar favorito
    fun onToggleFavorite(exerciseId: String) {
        //Primero comprobamos que el id es valido
        val parsedId = exerciseId.toIntOrNull()
        if (parsedId == null) {
            Log.w(TAG, "No se pudo parsear exerciseId=$exerciseId para toggle de favorito")
            return
        }
        // Luego actualizamos el conjunto de favoritos
        val previous = _favoritesSet.value
        // Si el id ya esta en el conjunto, lo quitamos, si no lo esta lo añadimos
        val optimistic = if (previous.contains(parsedId)) {
            previous - parsedId
        } else {
            previous + parsedId
        }
        // Actualizamos el estado de la ui
        _favoritesSet.value = optimistic

        //EN LA BD SE GUARDA EL EJERCICIO, LE ENTRA POR PARAMETRO UN EXERCISE
        //Actualizamos la base de datos
        viewModelScope.launch {
            exerciseRepository.updateFavWord(parsedId)
        }
    }
    
    // Este companion object es para poder usar el TAG en el Log
    companion object {
        private const val TAG = "ExerciseViewModel"
    }
}
