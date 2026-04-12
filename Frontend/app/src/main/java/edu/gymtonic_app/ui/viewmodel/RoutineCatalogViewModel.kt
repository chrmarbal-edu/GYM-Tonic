package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import edu.gymtonic_app.R
import edu.gymtonic_app.ui.screens.routines.RoutineExerciseUi

data class RoutineCatalogDetailUi(
    val id: String,
    val title: String,
    val exercises: List<RoutineExerciseUi>
)

class RoutineCatalogStoreViewModel(application: Application) : AndroidViewModel(application) {

    // Catalogo temporal local. En la integracion real se reemplaza por repository/backend.
    private val routinesById: Map<String, RoutineCatalogDetailUi> = mapOf(
        "fullbody" to RoutineCatalogDetailUi(
            id = "fullbody",
            title = "FullBody",
            exercises = listOf(
                RoutineExerciseUi("ESTOCADAS", "x10", R.drawable.estocadas),
                RoutineExerciseUi("PRESS BANCA", "x10", R.drawable.pressbanca),
                RoutineExerciseUi("PULL OVER", "x12", R.drawable.pullover),
                RoutineExerciseUi("REMO", "x15", R.drawable.remo),
                RoutineExerciseUi("SENTADILLA", "x15", R.drawable.sentadilla),
                RoutineExerciseUi("PESO MUERTO", "x20", R.drawable.pesomuerto)
            )
        ),
        "back" to RoutineCatalogDetailUi(
            id = "back",
            title = "Espalda",
            exercises = listOf(
                RoutineExerciseUi("JALON AL PECHO", "x12", R.drawable.remo),
                RoutineExerciseUi("REMO CON BARRA", "x10", R.drawable.remo),
                RoutineExerciseUi("PESO MUERTO", "x8", R.drawable.pesomuerto),
                RoutineExerciseUi("PULL OVER", "x12", R.drawable.pullover)
            )
        ),
        "arm" to RoutineCatalogDetailUi(
            id = "arm",
            title = "Brazo",
            exercises = listOf(
                RoutineExerciseUi("CURL BICEPS", "x12", R.drawable.brazo),
                RoutineExerciseUi("EXTENSION TRICEPS", "x12", R.drawable.brazo),
                RoutineExerciseUi("MARTILLO", "x10", R.drawable.brazo),
                RoutineExerciseUi("FONDOS", "x10", R.drawable.pushup)
            )
        ),
        "calves" to RoutineCatalogDetailUi(
            id = "calves",
            title = "Gemelos",
            exercises = listOf(
                RoutineExerciseUi("ELEVACION TALONES", "x20", R.drawable.pierna),
                RoutineExerciseUi("SENTADILLA", "x12", R.drawable.sentadilla),
                RoutineExerciseUi("ESTOCADAS", "x12", R.drawable.estocadas),
                RoutineExerciseUi("PRENSA", "x10", R.drawable.pierna)
            )
        ),
        "push" to RoutineCatalogDetailUi(
            id = "push",
            title = "Empujes",
            exercises = listOf(
                RoutineExerciseUi("PUSH UPS", "x15", R.drawable.pushup),
                RoutineExerciseUi("PRESS BANCA", "x10", R.drawable.pressbanca),
                RoutineExerciseUi("PRESS MILITAR", "x10", R.drawable.pushup),
                RoutineExerciseUi("FONDOS", "x12", R.drawable.pushup)
            )
        ),
        "stretch" to RoutineCatalogDetailUi(
            id = "stretch",
            title = "Estiramientos",
            exercises = listOf(
                RoutineExerciseUi("MOVILIDAD HOMBRO", "x30s", R.drawable.estiramientos),
                RoutineExerciseUi("ISQUIOS", "x30s", R.drawable.estiramientos),
                RoutineExerciseUi("CADERA", "x30s", R.drawable.estiramientos),
                RoutineExerciseUi("LUMBAR", "x30s", R.drawable.estiramientos)
            )
        )
    )

    fun getRoutine(routineId: String): RoutineCatalogDetailUi {
        return routinesById[routineId] ?: routinesById.getValue("fullbody")
    }
}




