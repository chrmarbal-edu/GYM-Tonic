package edu.gymtonic_app.data.remote.datasource.model

data class ExerciseDto(
    val exerciseId: Int = 0,
    val exerciseName: String,
    val exerciseDescription: String,
    val exerciseType: Int,
    val exerciseVideo: String? = null,
    val exerciseImage: String? = null
)
