package edu.gymtonic_app.data.repository

import android.content.Context
import edu.gymtonic_app.data.local.GymTonicDatabase
import edu.gymtonic_app.data.local.datasource.local.exercise.ExerciseLocalDataSource
import kotlinx.coroutines.flow.Flow

class FavoritesRepository(context: Context) {

    private val exerciseLocalDataSource = ExerciseLocalDataSource(
        GymTonicDatabase.getInstance(context).exerciseDao()
    )

    fun observeFavoriteIds(): Flow<Set<Int>> {
        return exerciseLocalDataSource.observeFavoriteIds()
    }

    suspend fun toggleFavorite(exerciseId: Int): Boolean {
        return exerciseLocalDataSource.toggleFavorite(exerciseId)
    }
}

