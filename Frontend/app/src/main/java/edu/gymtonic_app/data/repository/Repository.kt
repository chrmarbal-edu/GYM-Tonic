package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.datasource.TrainingRemoteDataSource
import edu.gymtonic_app.data.remote.datasource.AuthRemoteDataSource
import edu.gymtonic_app.data.remote.datasource.WeekRemoteDataSource
import edu.gymtonic_app.data.remote.model.auth.LoginRequest
import edu.gymtonic_app.data.remote.model.auth.LoginResponse
import edu.gymtonic_app.data.remote.model.training.TrainingCategoryDto
import edu.gymtonic_app.data.remote.model.user.RegisterRequest
import edu.gymtonic_app.data.remote.model.user.RegisterResponse
import edu.gymtonic_app.data.remote.model.week.WeeklyCalendarDayDto
import edu.gymtonic_app.data.remote.model.week.WeeklyGoalDto

class Repository(
    private val authRemoteDataSource: AuthRemoteDataSource = AuthRemoteDataSource(),
    private val trainingRemoteDataSource: TrainingRemoteDataSource = TrainingRemoteDataSource(),
    private val weekRemoteDataSource: WeekRemoteDataSource = WeekRemoteDataSource()
) {


    // Función para obtener el login.
    suspend fun login(request: LoginRequest): LoginResponse {
        return authRemoteDataSource.login(request)
    }

    suspend fun register(request: RegisterRequest): RegisterResponse {
        return authRemoteDataSource.register(request)
    }

    suspend fun logout(): Result<Unit> {
        return runCatching {
            authRemoteDataSource.logout()
        }
    }

    suspend fun getTrainingCategories(): Result<List<TrainingCategoryDto>> {
        return runCatching {
            trainingRemoteDataSource.getTrainingCategories()
        }
    }

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