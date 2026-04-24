package edu.gymtonic_app.ui.screens.routines

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
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
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
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(routineId) {
        viewModel.loadRoutine(routineId)
    }

    when (val state = uiState) {
        is RoutineCatalogUiState.Loading -> {
            TrainingShellScreen(
                title = "Cargando rutina...",
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
                    onExerciseClick = onExerciseClick
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
                        onExerciseClick = onExerciseClick
                    )
                }
            } else {
                TrainingShellScreen(
                    title = "Entrenamientos",
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

