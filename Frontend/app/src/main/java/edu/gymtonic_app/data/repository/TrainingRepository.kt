package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.mapper.toDomain
import edu.gymtonic_app.data.remote.datasource.TrainingRemoteDataSource
import edu.gymtonic_app.domain.model.training.TrainingCategory

class TrainingRepository(
	private val trainingRemoteDataSource: TrainingRemoteDataSource = TrainingRemoteDataSource()
) {
	suspend fun getTrainingCategories(): Result<List<TrainingCategory>> {
		return runCatching {
			trainingRemoteDataSource.getTrainingCategories().map { it.toDomain() }
		}
	}
}

