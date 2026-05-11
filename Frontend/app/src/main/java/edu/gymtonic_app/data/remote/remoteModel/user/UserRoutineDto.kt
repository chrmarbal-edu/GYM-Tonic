package edu.gymtonic_app.data.remote.remoteModel.user

import com.google.gson.annotations.SerializedName

data class UserRoutineDto(
    @SerializedName("user_x_routine_id")
    val userRoutineId: Int,

    @SerializedName("user_x_routine_userid")
    val userId: Int,

    @SerializedName("user_x_routine_routineid")
    val routineId: Int
)