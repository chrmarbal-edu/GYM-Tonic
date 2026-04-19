package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.datasource.TrainingRemoteDataSource
import edu.gymtonic_app.data.remote.datasource.UserRemoteDataSource
import edu.gymtonic_app.data.remote.datasource.WeekRemoteDataSource
import edu.gymtonic_app.data.remote.model.Login.LoginRequest
import edu.gymtonic_app.data.remote.model.Login.LoginResponse
import edu.gymtonic_app.data.remote.model.RegisterRequest
import edu.gymtonic_app.data.remote.model.RegisterResponse
import edu.gymtonic_app.data.remote.model.TrainingCategoryDto
import edu.gymtonic_app.data.remote.model.WeeklyCalendarDayDto
import edu.gymtonic_app.data.remote.model.WeeklyGoalDto

class Repository(
    private val userRemoteDataSource: UserRemoteDataSource = UserRemoteDataSource(),
    private val trainingRemoteDataSource: TrainingRemoteDataSource = TrainingRemoteDataSource(),
    private val weekRemoteDataSource: WeekRemoteDataSource = WeekRemoteDataSource()
) {


    // Función para obtener el login.
    suspend fun login(request: LoginRequest): LoginResponse {
        return userRemoteDataSource.login(request)
    }

    suspend fun register(request: RegisterRequest): RegisterResponse {
        return userRemoteDataSource.register(request)
    }

    suspend fun logout(): Result<Unit> {
        return runCatching {
            userRemoteDataSource.logout()
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