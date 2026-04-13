package edu.gymtonic_app.ui.screens.routines

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.ui.viewmodel.RoutineCatalogViewModel

@Composable
fun RoutineCatalogScreen(
    routineId: String,
    onBack: () -> Unit,
    viewModel: RoutineCatalogViewModel = viewModel()
) {
    // La pantalla solo pinta: el ViewModel resuelve el routineId al detalle de rutina.
    val routine = viewModel.getRoutine(routineId)

    RoutineTemplateScreen(
        title = routine.title,
        exercises = routine.exercises,
        onBack = onBack
    )
}

