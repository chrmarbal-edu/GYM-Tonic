package edu.gymtonic_app.data.remote.model

data class UserDto(
    val id: Int,
    val username: String,
    val name: String,
    val password: String?,
    val birthdate: String,
    val email: String,
    val height: Double,
    val weight: Double,
    val objetive: Int,
    val points: Int?,
    val role: Int
)