package hr.itrojnar.eventmanagement.api

import hr.itrojnar.eventmanagement.model.AccessTokenResponse
import hr.itrojnar.eventmanagement.model.LoginRequest
import hr.itrojnar.eventmanagement.model.RegisterRequest
import retrofit2.Response

class AuthRepository(private val authService: AuthService) {
    suspend fun login(username: String, password: String): AccessTokenResponse {
        val credentials = LoginRequest(username, password)
        return authService.login(credentials)
    }

    suspend fun register(username: String, password: String): Response<Unit> {
        val request = RegisterRequest(username, password)
        return authService.register(request)
    }
}