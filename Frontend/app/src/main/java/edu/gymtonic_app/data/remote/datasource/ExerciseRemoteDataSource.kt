package edu.gymtonic_app.data.remote.datasource

import edu.gymtonic_app.data.remote.model.exercise.ExerciseDetailDto

class ExerciseRemoteDataSource {

    private val mockExercises = listOf(
        ExerciseDetailDto(
            id = "fullbody-estocadas-0",
            name = "ESTOCADAS",
            durationSeconds = 15,
            imageKey = "estocadas",
            instructions = listOf(
                "Da un paso largo al frente y baja el cuerpo controlado.",
                "Mantén el tronco recto y el abdomen activo.",
                "Empuja con el talón delantero para volver al inicio."
            )
        ),
        ExerciseDetailDto(
            id = "fullbody-press-banca-1",
            name = "PRESS BANCA",
            durationSeconds = 15,
            imageKey = "pressbanca",
            instructions = listOf(
                "Alinea muñecas y codos para un empuje estable.",
                "Baja la barra de forma controlada hasta el pecho.",
                "Empuja manteniendo escapulas retraidas."
            )
        ),
        ExerciseDetailDto(
            id = "fullbody-pull-over-2",
            name = "PULL OVER",
            durationSeconds = 15,
            imageKey = "pullover",
            instructions = listOf(
                "Sujeta una mancuerna con ambos brazos extendidos.",
                "Desciende por detrás de la cabeza sin perder control.",
                "Vuelve al centro activando dorsal y pecho."
            )
        ),
        ExerciseDetailDto(
            id = "fullbody-remo-3",
            name = "REMO",
            durationSeconds = 15,
            imageKey = "remo",
            instructions = listOf(
                "Inclina el tronco con espalda neutra.",
                "Lleva los codos hacia atrás cerca del cuerpo.",
                "Controla la bajada sin perder la postura."
            )
        ),
        ExerciseDetailDto(
            id = "fullbody-sentadilla-4",
            name = "SENTADILLA",
            durationSeconds = 15,
            imageKey = "sentadilla",
            instructions = listOf(
                "Separa pies al ancho de hombros.",
                "Baja la cadera manteniendo rodillas alineadas.",
                "Sube empujando desde talones."
            )
        ),
        ExerciseDetailDto(
            id = "fullbody-peso-muerto-5",
            name = "PESO MUERTO",
            durationSeconds = 15,
            imageKey = "pesomuerto",
            instructions = listOf(
                "Mantén la barra pegada al cuerpo.",
                "Hinge de cadera con espalda neutra.",
                "Extiende cadera y rodillas para completar la repeticion."
            )
        )
    )

    suspend fun getExerciseById(exerciseId: String): ExerciseDetailDto {
        return mockExercises.firstOrNull { it.id == exerciseId }
            ?: throw NoSuchElementException("No existe ejercicio para id=$exerciseId")
    }
}

