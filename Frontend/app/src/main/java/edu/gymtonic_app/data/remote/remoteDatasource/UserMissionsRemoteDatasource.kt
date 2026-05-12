package edu.gymtonic_app.data.remote.remoteDatasource

import edu.gymtonic_app.data.remote.remoteModel.week.WeeklyCalendarDayDto
import edu.gymtonic_app.data.remote.services.RetrofitClient

class UserMissionsRemoteDatasource {
    private val api = RetrofitClient.apiService

    suspend fun getUserMissions() = api.getUserMissions()

    suspend fun getUserMissionByUserId( userId: String) = api.getUserMissionByUserId(userId)

    suspend fun getUserMissionByMissionId(missionId: String) = api.getUserMissionByMissionId(missionId)

    suspend fun getUserMissionById(id: String) = api.getUserMissionById(id)

    suspend fun createUserMission( request: Map<String, Any>) = api.createUserMission(request)

    suspend fun updateUserMission(id: String,request: Map<String, Any?>) = api.updateUserMission(id,request)

    suspend fun deleteUserMission( id: String) = api.deleteUserMission(id)

    //hardcodeado de los dias
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