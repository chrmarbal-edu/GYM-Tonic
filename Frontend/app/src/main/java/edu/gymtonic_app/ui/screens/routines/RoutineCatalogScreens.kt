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
import edu.gymtonic_app.ui.viewmodel.ExerciseViewModel
import edu.gymtonic_app.ui.viewmodel.ExerciseViewModelFactory
import edu.gymtonic_app.ui.viewmodel.FavoriteExercisePayload
import edu.gymtonic_app.ui.viewmodel.RoutineCatalogUiState
import edu.gymtonic_app.ui.viewmodel.RoutineCatalogViewModel

@Composable
fun RoutineCatalogScreen(
    routineId: String,
    onBack: () -> Unit,
    onExerciseClick: (String) -> Unit,
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

    LaunchedEffect(routineId) {
        viewModel.loadRoutine(routineId)
    }

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
                onOpenProfile = onOpenProfile
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        is RoutineCatalogUiState.Success -> {
            TrainingShellScreen(
                title = state.routine.title,
                onBack = onBack,
                showBottomBar = true,
                selectedBottomItem = BottomNavItem.TRAINING,
                onOpenHome = onOpenHome,
                onOpenTraining = onOpenTraining,
                onOpenChallenges = onOpenChallenges,
                onOpenProfile = onOpenProfile
            ) {
                RoutineTemplateScreen(
                    exercises = state.routine.exercises,
                    onExerciseClick = onExerciseClick,
                    favoritesSet = favoritesSet,
                    onToggleFavorite = { routineExercise ->
                        exerciseViewModel.onToggleFavorite(
                            FavoriteExercisePayload(
                                id = routineExercise.id,
                                name = routineExercise.name,
                                description = routineExercise.reps,
                                type = 0,
                                video = null,
                                image = null
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
                    title = fallback.title,
                    onBack = onBack,
                    showBottomBar = true,
                    selectedBottomItem = BottomNavItem.TRAINING,
                    onOpenHome = onOpenHome,
                    onOpenTraining = onOpenTraining,
                    onOpenChallenges = onOpenChallenges,
                    onOpenProfile = onOpenProfile
                ) {
                    RoutineTemplateScreen(
                        exercises = fallback.exercises,
                        onExerciseClick = onExerciseClick,
                        favoritesSet = favoritesSet,
                        onToggleFavorite = { routineExercise ->
                            exerciseViewModel.onToggleFavorite(
                                FavoriteExercisePayload(
                                    id = routineExercise.id,
                                    name = routineExercise.name,
                                    description = routineExercise.reps,
                                    type = 0,
                                    video = null,
                                    image = null
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
                    onOpenProfile = onOpenProfile
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
