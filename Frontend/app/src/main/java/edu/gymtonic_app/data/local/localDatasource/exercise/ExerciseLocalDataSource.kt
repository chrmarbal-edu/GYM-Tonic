package edu.gymtonic_app.data.local.localDatasource.exercise

import edu.gymtonic_app.data.local.dao.ExerciseDao
import edu.gymtonic_app.data.local.dao.UserFavoriteExerciseDao
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.local.localModel.user.UserFavoriteExerciseEntity
import kotlinx.coroutines.flow.Flow

class ExerciseLocalDataSource(
	private val exerciseDao: ExerciseDao,
	private val userFavoriteDao: UserFavoriteExerciseDao? = null
) {
	//obtener favoritas del usuario
	fun getFavoriteExercises(userId: Int): Flow<List<ExerciseEntity>> {
		return userFavoriteDao?.getFavoriteExercises(userId) ?: kotlinx.coroutines.flow.flowOf(emptyList())
	}

	//obtener por id
	suspend fun getExerciseById(exercise_id: Int): ExerciseEntity? = exerciseDao.getExerciseById(exercise_id)

	suspend fun getAllExercises(): List<ExerciseEntity> = exerciseDao.getAllExercises()

	suspend fun insertExercises(exercises: List<ExerciseEntity>) = exerciseDao.insertExercises(exercises)

	suspend fun isFavorite(userId: Int, exerciseId: Int): Boolean {
		return userFavoriteDao?.isFavorite(userId, exerciseId) ?: false
	}

	suspend fun toggleFavorite(userId: Int, exerciseId: Int) {
		userFavoriteDao?.let { dao ->
			if (dao.isFavorite(userId, exerciseId)) {
				dao.deleteFavorite(UserFavoriteExerciseEntity(userId, exerciseId))
			} else {
				dao.insertFavorite(UserFavoriteExerciseEntity(userId, exerciseId))
			}
		}
	}

	//insertar ejercicio base
	suspend fun insertExercise(exercise: ExerciseEntity): Long = exerciseDao.insertExercise(exercise)

	suspend fun updateExerciseMedia(exerciseId: Int, image: String?, video: String?) =
		exerciseDao.updateExerciseMedia(exerciseId, image, video)

	//eliminar ejercicio base
	suspend fun deleteExercise(exercise: ExerciseEntity): Int = exerciseDao.deleteExercise(exercise)

	suspend fun deleteExerciseById(id: Int): Int = exerciseDao.deleteExerciseById(id)
}
