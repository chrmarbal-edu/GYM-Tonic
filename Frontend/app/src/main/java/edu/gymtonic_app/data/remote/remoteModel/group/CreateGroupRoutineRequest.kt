package edu.gymtonic_app.data.remote.remoteModel.group

data class CreateGroupRoutineRequest(
    val name: String,
    val exercise_ids: List<Int>
)
