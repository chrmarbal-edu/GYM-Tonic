package edu.gymtonic_app.data.remote.datasource

import edu.gymtonic_app.data.remote.model.week.WeeklyCalendarDayDto
import edu.gymtonic_app.data.remote.model.week.WeeklyGoalDto

class WeekRemoteDataSource {
    suspend fun getWeeklyGoals(): List<WeeklyGoalDto> {
        return listOf(
            WeeklyGoalDto(
                title = "Entrenar 5 dias a la semana",
                progressLabel = "2/5",
                pointsLabel = "+ 300 pts",
                progress = 0.40f
            ),
            WeeklyGoalDto(
                title = "Quemar 1000 kcal",
                progressLabel = "882/1000",
                pointsLabel = "+ 120 pts",
                progress = 0.88f
            ),
            WeeklyGoalDto(
                title = "Manten la racha 2 dias seguidos",
                progressLabel = "1/2",
                pointsLabel = "+ 50 pts",
                progress = 0.50f
            )
        )
    }

    suspend fun getWeeklyCalendarDays(): List<WeeklyCalendarDayDto> {
        return listOf(
            WeeklyCalendarDayDto(0, true, true),
            WeeklyCalendarDayDto(1, false, true),
            WeeklyCalendarDayDto(2, true, true),
            WeeklyCalendarDayDto(3, true, true),
            WeeklyCalendarDayDto(4, false, true),
            WeeklyCalendarDayDto(5, false, true),
            WeeklyCalendarDayDto(6, false, true),
            WeeklyCalendarDayDto(7, false, true),
            WeeklyCalendarDayDto(8, true, true),
            WeeklyCalendarDayDto(9, true, true),
            WeeklyCalendarDayDto(10, false, true),
            WeeklyCalendarDayDto(11, true, true),
            WeeklyCalendarDayDto(12, false, true),
            WeeklyCalendarDayDto(13, false, true),
            WeeklyCalendarDayDto(14, true, true),
            WeeklyCalendarDayDto(15, false, true),
            WeeklyCalendarDayDto(16, true, true),
            WeeklyCalendarDayDto(17, false, false),
            WeeklyCalendarDayDto(18, false, false),
            WeeklyCalendarDayDto(19, false, false),
            WeeklyCalendarDayDto(20, false, false),
            WeeklyCalendarDayDto(21, false, false),
            WeeklyCalendarDayDto(22, false, false),
            WeeklyCalendarDayDto(23, false, false),
            WeeklyCalendarDayDto(24, false, false),
            WeeklyCalendarDayDto(25, false, false),
            WeeklyCalendarDayDto(26, false, false),
            WeeklyCalendarDayDto(27, false, false)
        )
    }
}

