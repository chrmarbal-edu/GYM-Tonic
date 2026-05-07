package edu.gymtonic_app.data.local.datasource.local.exercise

import edu.gymtonic_app.data.local.dao.ExerciseDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExerciseLocalDataSource(
	private val exerciseDao: ExerciseDao
) {
	// Obtener lista de favoritos
	fun observeFavoriteIds(): Flow<Set<Int>> {
		return exerciseDao.observeFavoriteIds().map { it.toSet() }
	}

	// Toggle favorito
	suspend fun toggleFavorite(exerciseId: Int): Boolean {
		val current = exerciseDao.isFavoriteById(exerciseId) ?: false
		val next = !current
		val updatedRows = exerciseDao.setFavorite(exerciseId, next)
		if (updatedRows <= 0) {
			throw IllegalStateException("No se pudo actualizar favorito para exercise_id=$exerciseId")
		}
		return next
	}
}
