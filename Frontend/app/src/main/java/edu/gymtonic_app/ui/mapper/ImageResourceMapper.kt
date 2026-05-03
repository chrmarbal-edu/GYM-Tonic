package edu.gymtonic_app.ui.mapper

import edu.gymtonic_app.R

object ImageResourceMapper {
    fun fromKey(imageKey: String?): Int {
        return when (imageKey) {
            "espalda" -> R.drawable.espalda
            "fullbody" -> R.drawable.fullbody
            "pushup" -> R.drawable.pushup
            "estiramientos" -> R.drawable.estiramientos
            "brazo" -> R.drawable.brazo
            "pierna" -> R.drawable.pierna
            "estocadas" -> R.drawable.estocadas
            "pressbanca", "bench" -> R.drawable.pressbanca
            "pullover" -> R.drawable.pullover
            "remo", "row" -> R.drawable.remo
            "sentadilla", "squat" -> R.drawable.sentadilla
            "pesomuerto", "deadlift" -> R.drawable.pesomuerto
            else -> R.drawable.fullbody
        }
    }
}

