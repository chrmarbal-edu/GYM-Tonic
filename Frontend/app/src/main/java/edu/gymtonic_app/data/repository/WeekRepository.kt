package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.remoteDatasource.WeekRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.week.WeeklyCalendarDayDto
import edu.gymtonic_app.data.remote.remoteModel.week.WeeklyGoalDto

class WeekRepository(
	private val weekRemoteDataSource: WeekRemoteDataSource = WeekRemoteDataSource()
) {
	suspend fun getWeeklyGoals(): Result<List<WeeklyGoalDto>> {
		return runCatching {
			weekRemoteDataSource.getWeeklyGoals()
		}
	}

	suspend fun getWeeklyCalendarDays(): Result<List<WeeklyCalendarDayDto>> {
		return runCatching {
			weekRemoteDataSource.getWeeklyCalendarDays()
		}
	}
}

