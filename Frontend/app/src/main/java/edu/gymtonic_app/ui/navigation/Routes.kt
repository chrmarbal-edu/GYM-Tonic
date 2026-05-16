package edu.gymtonic_app.ui.navigation

object Routes {
    const val WELCOME = "welcome"
    const val LOGIN_FORM = "login_form"

    const val REGISTER = "register_step_1"

    const val REGISTER2 = "register_step_2"

    const val HOME = "home"

    const val TRAINING = "training_screen"

    const val CREATE_ROUTINE = "create_routine"

    // Dynamic route using backend routineId.
    const val ROUTINE_DETAIL = "routine/{routineId}?local={local}"
    const val EXERCISE_DETAIL = "exercise/{exerciseId}?reps={reps}"
    const val PROFILE = "profile"
    const val ACCOUNT = "account"
    const val SETTINGS = "settings"

    const val GROUPS = "groups"
    const val GROUP_DETAIL = "group/{groupId}"
    const val GROUP_ADD_ROUTINE = "group/{groupId}/add-routine"

    fun groupDetail(groupId: Int): String = "group/$groupId"
    fun groupAddRoutine(groupId: Int): String = "group/$groupId/add-routine"

    fun routine(routineId: String, isLocal: Boolean): String = "routine/$routineId?local=$isLocal"
    fun exercise(exerciseId: String, reps: String = "N/A"): String = "exercise/$exerciseId?reps=$reps"

    const val WEEK = "week_challenges"

    const val DISCOUNTS = "discounts"

    const val FRIENDS = "friends"
}
