package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.datasource.TrainingRemoteDataSource
import edu.gymtonic_app.data.remote.model.training.TrainingCategoryDto

class TrainingRepository(
	private val trainingRemoteDataSource: TrainingRemoteDataSource = TrainingRemoteDataSource()
) {
	suspend fun getTrainingCategories(): Result<List<TrainingCategoryDto>> {
		return runCatching {
			trainingRemoteDataSource.getTrainingCategories()
		}
	}
}

