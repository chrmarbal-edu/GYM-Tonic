package edu.gymtonic_app.ui.screens.routines

import androidx.compose.runtime.Composable
import edu.gymtonic_app.R

@Composable
fun BackScreen(onBack: () -> Unit) {
    // Mock temporal de la rutina Espalda. El backend enviará estos ejercicios por routineId="back".
    val exercises = listOf(
        RoutineExerciseUi("JALON AL PECHO", "x12", R.drawable.remo),
        RoutineExerciseUi("REMO CON BARRA", "x10", R.drawable.remo),
        RoutineExerciseUi("PESO MUERTO", "x8", R.drawable.pesomuerto),
        RoutineExerciseUi("PULL OVER", "x12", R.drawable.pullover)
    )

    RoutineTemplateScreen(
        title = "Espalda",
        exercises = exercises,
        onBack = onBack
    )
}

@Composable
fun ArmScreen(onBack: () -> Unit) {
    // Mock temporal de Brazo para la ruta routineId="arm".
    val exercises = listOf(
        RoutineExerciseUi("CURL BICEPS", "x12", R.drawable.brazo),
        RoutineExerciseUi("EXTENSION TRICEPS", "x12", R.drawable.brazo),
        RoutineExerciseUi("MARTILLO", "x10", R.drawable.brazo),
        RoutineExerciseUi("FONDOS", "x10", R.drawable.pushup)
    )

    RoutineTemplateScreen(
        title = "Brazo",
        exercises = exercises,
        onBack = onBack
    )
}

@Composable
fun CalvesScreen(onBack: () -> Unit) {
    // Mock temporal de Gemelos para la ruta routineId="calves".
    val exercises = listOf(
        RoutineExerciseUi("ELEVACION TALONES", "x20", R.drawable.pierna),
        RoutineExerciseUi("SENTADILLA", "x12", R.drawable.sentadilla),
        RoutineExerciseUi("ESTOCADAS", "x12", R.drawable.estocadas),
        RoutineExerciseUi("PRENSA", "x10", R.drawable.pierna)
    )

    RoutineTemplateScreen(
        title = "Gemelos",
        exercises = exercises,
        onBack = onBack
    )
}

@Composable
fun PushScreen(onBack: () -> Unit) {
    // Mock temporal de Empujes para la ruta routineId="push".
    val exercises = listOf(
        RoutineExerciseUi("PUSH UPS", "x15", R.drawable.pushup),
        RoutineExerciseUi("PRESS BANCA", "x10", R.drawable.pressbanca),
        RoutineExerciseUi("PRESS MILITAR", "x10", R.drawable.pushup),
        RoutineExerciseUi("FONDOS", "x12", R.drawable.pushup)
    )

    RoutineTemplateScreen(
        title = "Empujes",
        exercises = exercises,
        onBack = onBack
    )
}

@Composable
fun StretchScreen(onBack: () -> Unit) {
    // Mock temporal de Estiramientos para la ruta routineId="stretch".
    val exercises = listOf(
        RoutineExerciseUi("MOVILIDAD HOMBRO", "x30s", R.drawable.estiramientos),
        RoutineExerciseUi("ISQUIOS", "x30s", R.drawable.estiramientos),
        RoutineExerciseUi("CADERA", "x30s", R.drawable.estiramientos),
        RoutineExerciseUi("LUMBAR", "x30s", R.drawable.estiramientos)
    )

    RoutineTemplateScreen(
        title = "Estiramientos",
        exercises = exercises,
        onBack = onBack
    )
}

