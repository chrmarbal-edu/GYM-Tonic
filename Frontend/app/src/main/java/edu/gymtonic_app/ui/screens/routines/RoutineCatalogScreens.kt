package edu.gymtonic_app.ui.screens.routines

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.viewmodel.exercise.ExerciseViewModel
import edu.gymtonic_app.ui.viewmodel.exercise.ExerciseViewModelFactory
import edu.gymtonic_app.ui.viewmodel.exercise.FavoriteExercisePayload
import edu.gymtonic_app.ui.viewmodel.routine.RoutineCatalogUiState
import androidx.compose.material3.AlertDialog
import edu.gymtonic_app.ui.components.LocalAppSnackbarHostState
import edu.gymtonic_app.ui.components.ObserveToastMessage
import edu.gymtonic_app.ui.components.ToastErrorRetryContent
import edu.gymtonic_app.ui.components.showAppToast
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import edu.gymtonic_app.ui.viewmodel.routine.RoutineCatalogViewModel

@Composable
fun RoutineCatalogScreen(
    routineId: String,
    isLocal: Boolean = false,
    onBack: () -> Unit,
    onExerciseClick: (String, String) -> Unit,
    onOpenHome: () -> Unit = {},
    onOpenTraining: () -> Unit = {},
    onOpenChallenges: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    viewModel: RoutineCatalogViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = ExerciseViewModelFactory(application))
    val favoritesSet by exerciseViewModel.favoritesSet.collectAsState()
    val uiState by viewModel.catalogUiState.collectAsState()
    val showDeleteDialog = remember { mutableStateOf(false) }
    val snackbarHostState = LocalAppSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val localRoutineId = routineId.toIntOrNull()
    val canDeleteRoutine = isLocal && localRoutineId != null

    //Carga de rutina al iniciar la pantalla, dependiendo si es local o remota
    LaunchedEffect(routineId, isLocal) {
        viewModel.loadRoutine(routineId, isLocal)
    }

    //Abrir alert dialog para confirmar eliminación de rutina local
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text(strings.deleteRoutineTitle) },
            text = { Text(strings.deleteRoutineMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = localRoutineId ?: return@TextButton
                        viewModel.deleteUserRoutine(
                            routineId = id,
                            onSuccess = {
                                showDeleteDialog.value = false
                                showAppToast(snackbarHostState, scope, strings.routineDeleted)
                                onBack()
                            },
                            onError = { message ->
                                showDeleteDialog.value = false
                                showAppToast(snackbarHostState, scope, message)
                            }
                        )
                    }
                ) {
                    Text(strings.deleteRoutineConfirm)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog.value = false }
                ) {
                    Text(strings.deleteRoutineCancel)
                }
            }
        )
    }

    // Renderizado basado en el estado de carga de la rutina
    when (val state = uiState) {
        is RoutineCatalogUiState.Loading -> {
            TrainingShellScreen(
                title = strings.routineLoading,
                onBack = onBack,
                showBottomBar = true,
                selectedBottomItem = BottomNavItem.TRAINING,
                onOpenHome = onOpenHome,
                onOpenTraining = onOpenTraining,
                onOpenChallenges = onOpenChallenges,
                onOpenProfile = onOpenProfile,
                onDeleteClick = if (canDeleteRoutine) {
                    { showDeleteDialog.value = true }
                } else {
                    null
                }
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        is RoutineCatalogUiState.Success -> {
            TrainingShellScreen(
                title = state.routine.routine_name ?: strings.routineWorkoutsTitle,
                onBack = onBack,
                showBottomBar = true,
                selectedBottomItem = BottomNavItem.TRAINING,
                onOpenHome = onOpenHome,
                onOpenTraining = onOpenTraining,
                onOpenChallenges = onOpenChallenges,
                onOpenProfile = onOpenProfile,
                onDeleteClick = if (canDeleteRoutine) {
                    { showDeleteDialog.value = true }
                } else {
                    null
                }
            ) {
                RoutineTemplateScreen(
                    exercises = state.routine.safeExercises(),
                    onExerciseClick = onExerciseClick,
                    favoritesSet = favoritesSet,
                    onToggleFavorite = { routineExercise ->
                        exerciseViewModel.onToggleFavorite(
                            FavoriteExercisePayload(
                                id = routineExercise.exercise_id,
                                name = routineExercise.exercise_name ?: "Ejercicio",
                                description = routineExercise.exercise_description ?: routineExercise.reps ?: "",
                                type = routineExercise.exercise_type,
                                video = routineExercise.exercise_video,
                                image = routineExercise.exercise_image
                            )
                        )
                    }
                )
            }
        }

        is RoutineCatalogUiState.Error -> {
            val fallback = state.fallbackRoutine
            if (fallback != null) {
                TrainingShellScreen(
                    title = fallback.routine_name ?: strings.routineWorkoutsTitle,
                    onBack = onBack,
                    showBottomBar = true,
                    selectedBottomItem = BottomNavItem.TRAINING,
                    onOpenHome = onOpenHome,
                    onOpenTraining = onOpenTraining,
                    onOpenChallenges = onOpenChallenges,
                    onOpenProfile = onOpenProfile,
                    onDeleteClick = if (canDeleteRoutine) {
                        { showDeleteDialog.value = true }
                    } else {
                        null
                    }
                ) {
                    RoutineTemplateScreen(
                        exercises = fallback.safeExercises(),
                        onExerciseClick = onExerciseClick,
                        favoritesSet = favoritesSet,
                        onToggleFavorite = { routineExercise ->
                            exerciseViewModel.onToggleFavorite(
                                FavoriteExercisePayload(
                                    id = routineExercise.exercise_id,
                                    name = routineExercise.exercise_name ?: "Ejercicio",
                                    description = routineExercise.reps ?: "",
                                    type = routineExercise.exercise_type,
                                    video = routineExercise.exercise_video,
                                    image = routineExercise.exercise_image
                                )
                            )
                        }
                    )
                }
            } else {
                TrainingShellScreen(
                    title = strings.routineWorkoutsTitle,
                    onBack = onBack,
                    showBottomBar = true,
                    selectedBottomItem = BottomNavItem.TRAINING,
                    onOpenHome = onOpenHome,
                    onOpenTraining = onOpenTraining,
                    onOpenChallenges = onOpenChallenges,
                    onOpenProfile = onOpenProfile,
                    onDeleteClick = if (canDeleteRoutine) {
                        { showDeleteDialog.value = true }
                    } else {
                        null
                    }
                ) {
                    ObserveToastMessage(message = state.message)
                    ToastErrorRetryContent(
                        retryLabel = strings.discountsRetry,
                        onRetry = { viewModel.loadRoutine(routineId, isLocal) }
                    )
                }
            }
        }
    }
}
