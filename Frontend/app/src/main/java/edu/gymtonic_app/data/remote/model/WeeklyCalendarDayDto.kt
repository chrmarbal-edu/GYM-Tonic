package edu.gymtonic_app.data.remote.model

data class WeeklyCalendarDayDto(
    val dayIndex: Int,
    val didWorkout: Boolean,
    val isClosedDay: Boolean
)

