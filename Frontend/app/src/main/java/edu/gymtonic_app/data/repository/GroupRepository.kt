package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.datasource.GroupRemoteDataSource
import edu.gymtonic_app.data.remote.model.group.GroupDto

data class GroupSummaryData(
	val id: Int,
	val name: String,
	val membersLabel: String
)

class GroupRepository(
	private val groupRemoteDataSource: GroupRemoteDataSource = GroupRemoteDataSource()
) {
	suspend fun getUserGroups(userId: Int?): Result<List<GroupSummaryData>> {
		return runCatching {
			groupRemoteDataSource.getUserGroups(userId).map { dto ->
				mapDtoToData(dto)
			}
		}
	}

	private fun mapDtoToData(dto: GroupDto): GroupSummaryData {
		val membersLabel = when (dto.groupId) {
			1 -> "7 personas"
			2 -> "3 personas"
			else -> "5 personas"
		}
		return GroupSummaryData(
			id = dto.groupId,
			name = dto.groupName,
			membersLabel = membersLabel
		)
	}
}

