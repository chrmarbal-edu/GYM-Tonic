package edu.gymtonic_app.data.local.localDatasource.exercise

import edu.gymtonic_app.data.local.dao.ExerciseDao
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import kotlinx.coroutines.flow.Flow

class ExerciseLocalDataSource(
	private val exerciseDao: ExerciseDao
) {
	//obtener favoritas según orden
	fun getExercises(): Flow<List<ExerciseEntity>> = exerciseDao.getFavoriteExercise()

	//obtener por id
	suspend fun getExerciseById(exercise_id: Int): ExerciseEntity? = exerciseDao.getExerciseById(exercise_id)

	suspend fun getAllExercises(): List<ExerciseEntity> = exerciseDao.getAllExercises()

	suspend fun insertExercises(exercises: List<ExerciseEntity>) = exerciseDao.insertExercises(exercises)

	//insertar favorita
	suspend fun insertExercise(exercise: ExerciseEntity): Long = exerciseDao.insertExercise(exercise)

	//eliminar favorita
	suspend fun deleteExercise(exercise: ExerciseEntity): Int = exerciseDao.deleteExercise(exercise)
}
