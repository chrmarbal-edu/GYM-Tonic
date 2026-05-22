package edu.gymtonic_app.data.local.localDatasource.user

import edu.gymtonic_app.data.local.dao.UserDao
import edu.gymtonic_app.data.local.localModel.user.UserEntity
import kotlinx.coroutines.flow.first

class UserLocalDataSource(
    private val userDao: UserDao
) {
    suspend fun getUserById(id: Int): UserEntity? {
        return userDao.getUserById(id).first()
    }

    suspend fun upsertUser(user: UserEntity) {
        userDao.upsertUser(user)
    }

    suspend fun deleteUserById(id: Int) {
        userDao.deleteUserById(id)
    }
}
