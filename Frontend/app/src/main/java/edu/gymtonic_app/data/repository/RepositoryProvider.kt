package edu.gymtonic_app.data.repository

import android.content.Context
import android.util.Log
import edu.gymtonic_app.data.local.GymTonicDatabase
import edu.gymtonic_app.data.local.localDatasource.exercise.ExerciseLocalDataSource
import edu.gymtonic_app.data.local.localDatasource.mission.MissionLocalDataSource
import edu.gymtonic_app.data.local.localDatasource.routine.RecentRoutineLocalDataSource
import edu.gymtonic_app.data.local.localDatasource.routine.RoutineLocalDataSource
import edu.gymtonic_app.data.local.localDatasource.user.UserLocalDataSource
import edu.gymtonic_app.data.local.localDatasource.routineExercise.RoutineExerciseLocalDataSource
import edu.gymtonic_app.data.local.localDatasource.userMission.UserMissionLocalDataSource
import edu.gymtonic_app.data.remote.remoteDatasource.ExerciseRemoteDataSource
import edu.gymtonic_app.data.remote.remoteDatasource.RoutineRemoteDataSource
import edu.gymtonic_app.data.remote.remoteDatasource.user.UserMissionsRemoteDatasource
import edu.gymtonic_app.data.remote.remoteDatasource.user.UsersRemoteDataSource

object RepositoryProvider {

    fun getExerciseRepository(context: Context): ExerciseRepository {
        val database = GymTonicDatabase.getInstance(context)
        val localDataSource = ExerciseLocalDataSource(database.exerciseDao(), database.userFavoriteExerciseDao())
        val remoteDataSource = ExerciseRemoteDataSource()
        return ExerciseRepository(remoteDataSource, localDataSource, context)
    }

    fun getRoutineRepository(context: Context): RoutineRepository {
        val database = GymTonicDatabase.getInstance(context)
        val localDataSource = RoutineLocalDataSource(database.routineDao())
        val exerciseLocal = ExerciseLocalDataSource(database.exerciseDao())
        val routineExLocal = RoutineExerciseLocalDataSource(database.routineExerciseDao())
        val recentRoutineLocal = RecentRoutineLocalDataSource(database.recentRoutineDao())
        val remoteDataSource = RoutineRemoteDataSource()
        val groupRemote = edu.gymtonic_app.data.remote.remoteDatasource.GroupRemoteDataSource()
        return RoutineRepository(remoteDataSource, localDataSource, exerciseLocal, routineExLocal, recentRoutineLocal, groupRemote, context)
    }

    fun getUserMissionsRepository(context: Context): UserMissionsRepository {
        val database = GymTonicDatabase.getInstance(context)
        val localDataSource = MissionLocalDataSource(database.missionDao())
        val userMissionLocal = UserMissionLocalDataSource(database.userMissionDao())
        val remoteDataSource = UserMissionsRemoteDatasource()
        return UserMissionsRepository(remoteDataSource, localDataSource, userMissionLocal, context)
    }

    fun getUserRepository(context: Context): UserRepository {
        Log.d("RepositoryProvider", "Providing UserRepository")
        val database = GymTonicDatabase.getInstance(context)
        val localDataSource = UserLocalDataSource(database.userDao())
        val remoteDataSource = UsersRemoteDataSource()
        return UserRepository(remoteDataSource, localDataSource, context)
    }

    fun getAuthRepository(context: Context): AuthRepository {
        val database = GymTonicDatabase.getInstance(context)
        val localDataSource = UserLocalDataSource(database.userDao())
        val remoteDataSource = edu.gymtonic_app.data.remote.remoteDatasource.AuthRemoteDataSource()
        return AuthRepository(remoteDataSource, localDataSource)
    }

    fun getAdminRepository(context: Context): AdminRepository {
        val database = GymTonicDatabase.getInstance(context)
        val routineLocal = RoutineLocalDataSource(database.routineDao())
        val routineExLocal = RoutineExerciseLocalDataSource(database.routineExerciseDao())
        val recentRoutineLocal = RecentRoutineLocalDataSource(database.recentRoutineDao())
        val exerciseLocal = ExerciseLocalDataSource(database.exerciseDao())
        return AdminRepository(routineLocal, routineExLocal, recentRoutineLocal, exerciseLocal)
    }
}
