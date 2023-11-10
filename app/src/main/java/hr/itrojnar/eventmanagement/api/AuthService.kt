package hr.itrojnar.eventmanagement.api

import hr.itrojnar.eventmanagement.model.AccessTokenResponse
import hr.itrojnar.eventmanagement.model.LoginRequest
import hr.itrojnar.eventmanagement.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/auth/login")
    suspend fun login(@Body credentials: LoginRequest): AccessTokenResponse

    @POST("/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>
}