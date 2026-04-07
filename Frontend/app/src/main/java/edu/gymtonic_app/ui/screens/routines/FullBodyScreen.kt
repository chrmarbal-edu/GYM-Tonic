package edu.gymtonic_app.ui.screens.routines

import androidx.compose.runtime.Composable
import edu.gymtonic_app.R

@Composable
fun FullBodyScreen(
    onBack: () -> Unit
) {
    // Datos mock temporales: en siguiente iteración vendrán de backend por routineId.
    val exercises = listOf(
        RoutineExerciseUi("ESTOCADAS", "x10", R.drawable.estocadas),
        RoutineExerciseUi("PRESS BANCA", "x10", R.drawable.pressbanca),
        RoutineExerciseUi("PULL OVER", "x12", R.drawable.pullover),
        RoutineExerciseUi("REMO", "x15", R.drawable.remo),
        RoutineExerciseUi("SENTADILLA", "x15", R.drawable.sentadilla),
        RoutineExerciseUi("PESO MUERTO", "x20", R.drawable.pesomuerto)
    )

    RoutineTemplateScreen(
        title = "FullBody",
        exercises = exercises,
        onBack = onBack
    )
}
