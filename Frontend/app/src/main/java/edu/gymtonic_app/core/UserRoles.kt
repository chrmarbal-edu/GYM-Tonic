package edu.gymtonic_app.core

object UserRoles {
    const val USER = 0
    const val ADMIN = 1

    fun isAdmin(role: Int?): Boolean = role == ADMIN
}
