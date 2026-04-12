package edu.gymtonic_app.ui.screens.routines

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.ui.viewmodel.RoutineCatalogStoreViewModel

@Composable
fun BackScreen(onBack: () -> Unit, viewModel: RoutineCatalogStoreViewModel = viewModel()) {
    // Fuente de datos temporal del catalogo. Luego vendra del backend por routineId.
    val routine = viewModel.getRoutine("back")

    RoutineTemplateScreen(
        title = routine.title,
        exercises = routine.exercises,
        onBack = onBack
    )
}

@Composable
fun ArmScreen(onBack: () -> Unit, viewModel: RoutineCatalogStoreViewModel = viewModel()) {
    val routine = viewModel.getRoutine("arm")

    RoutineTemplateScreen(
        title = routine.title,
        exercises = routine.exercises,
        onBack = onBack
    )
}

@Composable
fun CalvesScreen(onBack: () -> Unit, viewModel: RoutineCatalogStoreViewModel = viewModel()) {
    val routine = viewModel.getRoutine("calves")

    RoutineTemplateScreen(
        title = routine.title,
        exercises = routine.exercises,
        onBack = onBack
    )
}

@Composable
fun PushScreen(onBack: () -> Unit, viewModel: RoutineCatalogStoreViewModel = viewModel()) {
    val routine = viewModel.getRoutine("push")

    RoutineTemplateScreen(
        title = routine.title,
        exercises = routine.exercises,
        onBack = onBack
    )
}

@Composable
fun StretchScreen(onBack: () -> Unit, viewModel: RoutineCatalogStoreViewModel = viewModel()) {
    val routine = viewModel.getRoutine("stretch")

    RoutineTemplateScreen(
        title = routine.title,
        exercises = routine.exercises,
        onBack = onBack
    )
}

@Composable
fun FullBodyScreen(onBack: () -> Unit, viewModel: RoutineCatalogStoreViewModel = viewModel()) {
    val routine = viewModel.getRoutine("fullbody")

    RoutineTemplateScreen(
        title = routine.title,
        exercises = routine.exercises,
        onBack = onBack
    )
}

