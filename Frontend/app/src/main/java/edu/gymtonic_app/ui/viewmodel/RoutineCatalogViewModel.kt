package edu.gymtonic_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import edu.gymtonic_app.R

//modelo de UI para un ejercicio (nombre, reps, imagen local).
data class RoutineExerciseUi(
    val name: String,
    val reps: String,
    val imageRes: Int
)
//modelo de UI para el detalle de una rutina (id, titulo, lista de ejercicios).
data class RoutineDetailUi(
    val id: String,
    val title: String,
    val exercises: List<RoutineExerciseUi>
)
//hardcodeado en memoria, usado como mock mientras no llega la integración real.
class RoutineCatalogViewModel(application: Application) : AndroidViewModel(application) {

    // Catalogo temporal local. En la integracion real se reemplaza por repository/backend.
    private val routinesById: Map<String, RoutineDetailUi> = mapOf(
        "fullbody" to RoutineDetailUi(
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
        "back" to RoutineDetailUi(
            id = "back",
            title = "Espalda",
            exercises = listOf(
                RoutineExerciseUi("JALON AL PECHO", "x12", R.drawable.remo),
                RoutineExerciseUi("REMO CON BARRA", "x10", R.drawable.remo),
                RoutineExerciseUi("PESO MUERTO", "x8", R.drawable.pesomuerto),
                RoutineExerciseUi("PULL OVER", "x12", R.drawable.pullover)
            )
        ),
        "arm" to RoutineDetailUi(
            id = "arm",
            title = "Brazo",
            exercises = listOf(
                RoutineExerciseUi("CURL BICEPS", "x12", R.drawable.brazo),
                RoutineExerciseUi("EXTENSION TRICEPS", "x12", R.drawable.brazo),
                RoutineExerciseUi("MARTILLO", "x10", R.drawable.brazo),
                RoutineExerciseUi("FONDOS", "x10", R.drawable.pushup)
            )
        ),
        "calves" to RoutineDetailUi(
            id = "calves",
            title = "Gemelos",
            exercises = listOf(
                RoutineExerciseUi("ELEVACION TALONES", "x20", R.drawable.pierna),
                RoutineExerciseUi("SENTADILLA", "x12", R.drawable.sentadilla),
                RoutineExerciseUi("ESTOCADAS", "x12", R.drawable.estocadas),
                RoutineExerciseUi("PRENSA", "x10", R.drawable.pierna)
            )
        ),
        "push" to RoutineDetailUi(
            id = "push",
            title = "Empujes",
            exercises = listOf(
                RoutineExerciseUi("PUSH UPS", "x15", R.drawable.pushup),
                RoutineExerciseUi("PRESS BANCA", "x10", R.drawable.pressbanca),
                RoutineExerciseUi("PRESS MILITAR", "x10", R.drawable.pushup),
                RoutineExerciseUi("FONDOS", "x12", R.drawable.pushup)
            )
        ),
        "stretch" to RoutineDetailUi(
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
// busca la rutina por id y, si no existe, retorna fullbody.
    fun getRoutine(routineId: String): RoutineDetailUi {
        return routinesById[routineId] ?: routinesById.getValue("fullbody")
    }
}