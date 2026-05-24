package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.local.localDatasource.user.UserLocalDataSource
import edu.gymtonic_app.data.remote.remoteDatasource.AuthRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginRequest
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginResponse
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterResponse
import java.io.File

class AuthRepository(
	private val authRemoteDataSource: AuthRemoteDataSource = AuthRemoteDataSource(),
	private val userLocalDataSource: UserLocalDataSource? = null
) {
	suspend fun checkUsername(username: String): Boolean = authRemoteDataSource.checkUsername(username)
	suspend fun checkEmail(email: String): Boolean = authRemoteDataSource.checkEmail(email)

	suspend fun login(request: LoginRequest): LoginResponse {
		val response = authRemoteDataSource.login(request)
		response.data?.let { userData ->
			// Cache user on login
			val userEntity = edu.gymtonic_app.data.local.localModel.user.UserEntity(
				user_id = userData.user_id,
				user_username = userData.user_username,
				user_name = userData.user_name,
				user_birthdate = userData.user_birthdate,
				user_email = userData.user_email,
				user_picture = userData.user_picture,
				user_height = userData.user_height,
				user_weight = userData.user_weight,
				user_objective = userData.user_objective,
				user_points = userData.user_points,
				user_role = userData.user_role
			)
			userLocalDataSource?.upsertUser(userEntity)
		}
		return response
	}

	suspend fun googleLogin(idToken: String): Any {
		return authRemoteDataSource.googleLogin(idToken)
	}

	suspend fun facebookLogin(accessToken: String): Any {
		return authRemoteDataSource.facebookLogin(accessToken)
	}

	suspend fun register(
		username: String,
		name: String,
		password: String?,
		birthdate: String,
		email: String,
		height: Double,
		weight: Double,
		objective: Int,
		oauth: String?,
		pictureFile: File?,
		pictureUrl: String? = null
	): RegisterResponse {
		return authRemoteDataSource.register(
			username, name, password, birthdate, email, height, weight, objective, oauth, pictureFile, pictureUrl
		)
	}

	suspend fun recoverAccount(email: String, newPassword: String): Result<edu.gymtonic_app.data.remote.remoteModel.user.RecoverResponse> {
		return runCatching {
			authRemoteDataSource.recoverAccount(email, newPassword)
		}
	}

	suspend fun changePassword(code: String, recoveryToken: String): Result<Unit> {
		return runCatching {
			authRemoteDataSource.changePassword(code, recoveryToken)
		}
	}

	suspend fun logout(): Result<Unit> {
		return runCatching {
			authRemoteDataSource.logout()
		}
	}
}

