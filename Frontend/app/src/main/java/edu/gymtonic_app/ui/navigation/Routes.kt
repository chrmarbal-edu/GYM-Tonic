package edu.gymtonic_app.ui.navigation

object Routes {
    const val WELCOME = "welcome"
    const val LOGIN_FORM = "login_form"

    const val REGISTER = "register_step_1"

    const val REGISTER2 = "register_step_2"

    const val HOME = "home"

    const val TRAINING = "training_screen"

    const val CREATE_ROUTINE = "create_routine"

    const val EXERCISES = "exercises_screen"

    // Ruta dinámica para enlazar cualquier rutina que llegue del backend por su routineId.
    const val ROUTINE_DETAIL = "routine/{routineId}"
    const val EXERCISE_DETAIL = "exercise/{exerciseId}"
    const val PROFILE = "profile"
    const val ACCOUNT = "account"
    const val SETTINGS = "settings"

    fun routine(routineId: String): String = "routine/$routineId"
    fun exercise(exerciseId: String): String = "exercise/$exerciseId"

    const val WEEK = "week_challenges"
}
