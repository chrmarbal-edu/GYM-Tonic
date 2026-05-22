package edu.gymtonic_app.ui.navigation

import edu.gymtonic_app.core.UserRoles

object Routes {
    const val WELCOME = "welcome"
    const val LOGIN_FORM = "login_form"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password?email={email}"

    const val REGISTER = "register_step_1"

    const val REGISTER2 = "register_step_2"

    const val HOME = "home"

    const val TRAINING = "training_screen"

    const val CREATE_ROUTINE = "create_routine"
    const val EDIT_ROUTINE = "routine/{routineId}/edit?local={local}"

    // Dynamic route using backend routineId.
    const val ROUTINE_DETAIL = "routine/{routineId}?local={local}"
    const val EXERCISE_DETAIL = "exercise/{exerciseId}?reps={reps}&series={series}"
    const val PROFILE = "profile"
    const val ACCOUNT = "account"
    const val SETTINGS = "settings"

    const val GROUPS = "groups"
    const val GROUP_DETAIL = "group/{groupId}"
    const val GROUP_ADD_ROUTINE = "group/{groupId}/add-routine"

    fun groupDetail(groupId: Int): String = "group/$groupId"
    fun groupAddRoutine(groupId: Int): String = "group/$groupId/add-routine"

    fun routine(routineId: String, isLocal: Boolean): String = "routine/$routineId?local=$isLocal"
    fun editRoutine(routineId: Int, isLocal: Boolean): String = "routine/$routineId/edit?local=$isLocal"
    fun exercise(exerciseId: String, reps: String = "N/A", series: String = "N/A"): String = "exercise/$exerciseId?reps=$reps&series=$series"

    const val WEEK = "week_challenges"

    const val DISCOUNTS = "discounts"

    const val FRIENDS = "friends"

    // Admin dashboard
    const val ADMIN_ROUTINES = "admin/routines"
    const val ADMIN_ROUTINE_DETAIL = "admin/routine/{routineId}"
    const val ADMIN_ROUTINE_EDIT = "admin/routine/{routineId}/edit"
    const val ADMIN_EXERCISES = "admin/exercises"
    const val ADMIN_EXERCISE_DETAIL = "admin/exercise/{exerciseId}/detail"
    const val ADMIN_EXERCISE_EDIT = "admin/exercise/{exerciseId}/edit"
    const val ADMIN_EXERCISE_NEW = "admin/exercise/new"
    const val ADMIN_USERS = "admin/users"
    const val ADMIN_USER_DETAIL = "admin/user/{userId}"
    const val ADMIN_GROUPS = "admin/groups"
    const val ADMIN_GROUP_DETAIL = "admin/group/{groupId}"
    const val ADMIN_MISSIONS = "admin/missions"
    const val ADMIN_MISSION_EDIT = "admin/mission/{missionId}"
    const val ADMIN_MISSION_NEW = "admin/mission/new"

    fun adminRoutineDetail(routineId: Int): String = "admin/routine/$routineId"
    fun adminRoutineEdit(routineId: Int): String = "admin/routine/$routineId/edit"
    fun adminExerciseDetail(exerciseId: Int): String = "admin/exercise/$exerciseId/detail"
    fun adminExerciseEdit(exerciseId: Int): String = "admin/exercise/$exerciseId/edit"
    fun adminUserDetail(userId: Int): String = "admin/user/$userId"
    fun adminGroupDetail(groupId: Int): String = "admin/group/$groupId"
    fun adminMissionEdit(missionId: Int): String = "admin/mission/$missionId"

    fun resetPassword(email: String): String = "reset_password?email=$email"

    fun postLoginDestination(role: Int?): String =
        if (UserRoles.isAdmin(role)) HOME else TRAINING
}
