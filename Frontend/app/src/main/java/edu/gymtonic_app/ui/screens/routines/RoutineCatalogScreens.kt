package edu.gymtonic_app.ui.screens.routines

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import edu.gymtonic_app.ui.screens.admin.resolveRoutineImageUrl
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
    onOpenTraining: () -> Unit = {},
    onOpenGroups: () -> Unit = {},
    onOpenFriends: () -> Unit = {},
    onOpenChallenges: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onEdit: (Int, Boolean) -> Unit = { _, _ -> },
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
    val canDeleteRoutine = when (val state = uiState) {
        is RoutineCatalogUiState.Success -> state.routine.can_edit == true && localRoutineId != null
        else -> false
    }
    val canEditRoutine = when (val state = uiState) {
        is RoutineCatalogUiState.Success -> state.routine.can_edit == true
        else -> false
    }
    val editRoutineId = routineId.toIntOrNull()
    val onEditClick =
        if (canEditRoutine && editRoutineId != null) {
            { onEdit(editRoutineId, isLocal) }
        } else {
            null
        }

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
                onOpenTraining = onOpenTraining,
                onOpenGroups = onOpenGroups,
                onOpenFriends = onOpenFriends,
                onOpenChallenges = onOpenChallenges,
                onOpenProfile = onOpenProfile,
                onDeleteClick = if (canDeleteRoutine) {
                    { showDeleteDialog.value = true }
                } else {
                    null
                },
                onEditClick = onEditClick
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
                onOpenTraining = onOpenTraining,
                onOpenGroups = onOpenGroups,
                onOpenFriends = onOpenFriends,
                onOpenChallenges = onOpenChallenges,
                onOpenProfile = onOpenProfile,
                onDeleteClick = if (canDeleteRoutine) {
                    { showDeleteDialog.value = true }
                } else {
                    null
                },
                onEditClick = onEditClick
            ) {
                val routineImageUrl = resolveRoutineImageUrl(state.routine.routine_image)
                Column(Modifier.fillMaxSize()) {
                    if (!routineImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = routineImageUrl,
                            contentDescription = state.routine.routine_name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .height(160.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                    }
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
        }

        is RoutineCatalogUiState.Error -> {
            val fallback = state.fallbackRoutine
            if (fallback != null) {
                TrainingShellScreen(
                    title = fallback.routine_name ?: strings.routineWorkoutsTitle,
                    onBack = onBack,
                    showBottomBar = true,
                    selectedBottomItem = BottomNavItem.TRAINING,
                    onOpenTraining = onOpenTraining,
                    onOpenGroups = onOpenGroups,
                    onOpenFriends = onOpenFriends,
                    onOpenChallenges = onOpenChallenges,
                    onOpenProfile = onOpenProfile,
                    onDeleteClick = if (canDeleteRoutine) {
                        { showDeleteDialog.value = true }
                    } else {
                        null
                    },
                    onEditClick = onEditClick
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
                    onOpenTraining = onOpenTraining,
                    onOpenGroups = onOpenGroups,
                    onOpenFriends = onOpenFriends,
                    onOpenChallenges = onOpenChallenges,
                    onOpenProfile = onOpenProfile,
                    onDeleteClick = if (canDeleteRoutine) {
                        { showDeleteDialog.value = true }
                    } else {
                        null
                    },
                    onEditClick = onEditClick
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
