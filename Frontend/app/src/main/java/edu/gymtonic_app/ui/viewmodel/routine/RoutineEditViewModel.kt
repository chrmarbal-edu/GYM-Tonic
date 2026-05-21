package edu.gymtonic_app.ui.viewmodel.routine



import android.app.Application

import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.viewModelScope

import edu.gymtonic_app.data.remote.remoteDatasource.RoutineRemoteDataSource

import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDetailDto

import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineExerciseDto

import edu.gymtonic_app.data.repository.RoutineRepository

import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.update

import kotlinx.coroutines.launch

import java.io.File



data class RoutineEditUiState(

    val isLoading: Boolean = true,

    val isSaving: Boolean = false,

    val routine: RoutineDetailDto? = null,

    val error: String? = null

)



class RoutineEditViewModel(application: Application) : AndroidViewModel(application) {



    private val routineRepository = RoutineRepository(

        routineRemoteDataSource = RoutineRemoteDataSource()

    )



    private val _state = MutableStateFlow(RoutineEditUiState())

    val state: StateFlow<RoutineEditUiState> = _state.asStateFlow()



    fun load(routineId: Int, @Suppress("UNUSED_PARAMETER") isLocal: Boolean) {

        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, error = null) }



            routineRepository.getRoutineWithExercisesByIdFromApi(routineId)

                .onSuccess { routine ->

                    _state.update { it.copy(isLoading = false, routine = routine) }

                }

                .onFailure { e ->

                    _state.update { it.copy(isLoading = false, error = e.message) }

                }

        }

    }



    fun save(

        routineId: Int,

        @Suppress("UNUSED_PARAMETER") isLocal: Boolean,

        name: String,

        exercises: List<RoutineExerciseDto>,

        imageFile: File?,

        onSuccess: () -> Unit

    ) {

        viewModelScope.launch {

            _state.update { it.copy(isSaving = true, error = null) }



            routineRepository.saveRoutineWithFiles(routineId, name, exercises, imageFile)

                .onSuccess { updated ->

                    _state.update { it.copy(isSaving = false, routine = updated) }

                    onSuccess()

                }

                .onFailure { e ->

                    _state.update { it.copy(isSaving = false, error = e.message) }

                }

        }

    }

}

