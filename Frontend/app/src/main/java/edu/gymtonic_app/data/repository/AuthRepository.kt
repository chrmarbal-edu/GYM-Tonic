package edu.gymtonic_app.data.repository

import edu.gymtonic_app.data.remote.datasource.AuthRemoteDataSource
import edu.gymtonic_app.data.remote.model.auth.LoginRequest
import edu.gymtonic_app.data.remote.model.auth.LoginResponse
import edu.gymtonic_app.data.remote.model.user.RegisterRequest
import edu.gymtonic_app.data.remote.model.user.RegisterResponse

class AuthRepository(
	private val authRemoteDataSource: AuthRemoteDataSource = AuthRemoteDataSource()
) {
	suspend fun login(request: LoginRequest): LoginResponse {
		return authRemoteDataSource.login(request)
	}

	suspend fun register(request: RegisterRequest): RegisterResponse {
		return authRemoteDataSource.register(request)
	}

	suspend fun logout(): Result<Unit> {
		return runCatching {
			authRemoteDataSource.logout()
		}
	}
}

