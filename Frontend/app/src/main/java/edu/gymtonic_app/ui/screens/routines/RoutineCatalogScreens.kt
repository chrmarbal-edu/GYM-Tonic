package edu.gymtonic_app.ui.screens.routines

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.ui.viewmodel.RoutineCatalogUiState
import edu.gymtonic_app.ui.viewmodel.RoutineCatalogViewModel
import androidx.compose.runtime.collectAsState
/*
RoutineCatalogScreen sí es una pantalla navegable real.
Es la pantalla que muestra el detalle de una rutina.
Aunque el nombre diga “Catalog”, en la práctica actúa como pantalla detalle de rutina.
*/
@Composable
fun RoutineCatalogScreen(
    routineId: String,
    onBack: () -> Unit,
    onExerciseClick: (String) -> Unit,
    onOpenHome: () -> Unit = {},
    onOpenTraining: () -> Unit = {},
    onOpenChallenges: () -> Unit = {},

    // Cada vez que cambia el id de ruta, recargamos su detalle.
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
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
                RoutineTemplateScreen(
                    title = fallback.title,
                    exercises = fallback.exercises,
                    onBack = onBack,
                    onExerciseClick = onExerciseClick
                )
            RoutineTemplateScreen(
                title = state.routine.title,
                exercises = state.routine.exercises,
                onBack = onBack,
                onExerciseClick = onExerciseClick
            )
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, textAlign = TextAlign.Center)
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

