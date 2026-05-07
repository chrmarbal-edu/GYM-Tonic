package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.gymtonic_app.data.repository.ExerciseRepository
import edu.gymtonic_app.data.repository.FavoritesRepository
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

class ExerciseViewModel(
    application: Application,
    private val exerciseRepository: ExerciseRepository = ExerciseRepository(),
    private val favoritesRepository: FavoritesRepository = FavoritesRepository(application)
) : AndroidViewModel(application) {

    // region flow para almacenar el estado del UI
    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Idle)
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()
    // endregion

    //region flow para almacenar el conjunto de favoritos
    private val _favoritesSet = MutableStateFlow<Set<Int>>(emptySet())
    val favoritesSet: StateFlow<Set<Int>> = _favoritesSet.asStateFlow()
    //endregion

    init {
        viewModelScope.launch {
            // Escuchamos los cambios en el conjunto de favoritos
            favoritesRepository.observeFavoriteIds().collect { favoriteIds ->
                // Actualizamos el conjunto de favoritos con los nuevos valores
                _favoritesSet.value = favoriteIds
            }

        }
    }

    //region Metodos

    //Funcion para cargar el ejercicio
    fun loadExercise(exerciseId: String) {
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

        // Llamamos al repositorio para alternar favorito
        viewModelScope.launch {
            runCatching {
                favoritesRepository.toggleFavorite(parsedId)
            }.onFailure { error ->
                Log.e(TAG, "Error al alternar favorito para exerciseId=$exerciseId", error)
                _favoritesSet.value = previous
            }
        }
    }
    //endregion

    /*
    //region Carga de datos
    fun fetchWords() { //(carga remota + marcado de favoritas del repository)
        viewModelScope.launch { // ejecuta mi corrutina

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            ) // Da el valor de true a la variable del estado de carga

            try {
                //rellenado de la lista
                val listWords = repository.getAllWords(_uiState.value.sortAscending)
                _words.value = listWords ?: emptyList()

            } catch (e: Exception) {    //Cualquier error es escrito en la pantralla
                _uiState.value = _uiState.value.copy(errorMessage = "ERROR: ${e.message}")

            } finally { // si o si pone le modo de carga a falso
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun fetchFavouritesWords() {
        viewModelScope.launch {
            try {
                //1-Obtenemos la lista de fav del room ordenada segun sort
                val flujoDeFavoritas: Flow<List<Word>> =
                    repository.getFavWords(_uiState.value.sortAscending)

                //2-De la lista que recibimos que es "Flow", .collect es el escucha que obtiene los datos de la bd si estos cambian
                flujoDeFavoritas.collect { listaDeFavoritasRecibida ->

                    //2.1-Limpiamos el Flow
                    val listaLimpiaDeFavoritas: List<Word> =
                        listaDeFavoritasRecibida.map { palabra ->
                            //Aseguramos que cada palabra recibida tenga a true el isfavourite
                            palabra.copy(isFavorite = true)
                        }
                    //2.2-cargamos nuestra lista con la lista limpia
                    _favWords.value = listaLimpiaDeFavoritas

                    //3 Set con todos los ID de las palabras favoritas
                    val conjuntoDeIdsFavoritas: Set<Int> =
                        listaDeFavoritasRecibida.map { palabra -> palabra.idWord }.toSet()

                    //4 Marcar las favoritas
                    val listaPrincipalActual: List<Word> =
                        _words.value // Obtenemos la lista actual de todas las palabras.

                    val listaMerge =
                        listaPrincipalActual.map { palabra ->
                            // Para cada palabra, comprobamos si su ID está en nuestro conjunto de IDs favoritas.
                            val esFavorita: Boolean =
                                conjuntoDeIdsFavoritas.contains(palabra.idWord)

                            // Creamos una copia de la palabra, actualizando su estado de "favorita".
                            palabra.copy(isFavorite = esFavorita)
                        }

                    // 5-actualizamos la lista principal con la versión ya sincronizada.
                    _words.value = listaMerge

                }
            } catch (e: Exception) {    //Cualquier error es escrito en la pantralla
                _uiState.value = _uiState.value.copy(errorMessage = "ERROR: ${e.message}")

            } finally { // si o si pone le modo de carga a falso
                _uiState.value = _uiState.value.copy(isLoading = false)            }

        }
    }

    //Marca o desmarca favorito
    fun toggleFavWord(word: Word) {
        viewModelScope.launch {
            repository.updateFavWord(word) //cambia el fav a la palabra recibida
        }
    }
    */








    // Este companion object es para poder usar el TAG en el Log
    companion object {
        private const val TAG = "ExerciseViewModel"
    }
}
