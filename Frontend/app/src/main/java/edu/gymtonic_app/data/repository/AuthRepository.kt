package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.remoteDatasource.AuthRemoteDataSource
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginRequest
import edu.gymtonic_app.data.remote.remoteModel.auth.LoginResponse
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterResponse
import java.io.File

class AuthRepository(
	private val authRemoteDataSource: AuthRemoteDataSource = AuthRemoteDataSource()
) {
	suspend fun login(request: LoginRequest): LoginResponse {
		return authRemoteDataSource.login(request)
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

	suspend fun logout(): Result<Unit> {
		return runCatching {
			authRemoteDataSource.logout()
		}
	}
}

