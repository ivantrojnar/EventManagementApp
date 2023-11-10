package hr.itrojnar.eventmanagement.api

import hr.itrojnar.eventmanagement.model.EventDTO
import hr.itrojnar.eventmanagement.model.LoginRequest
import hr.itrojnar.eventmanagement.model.UserDetailsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("/users/details")
    suspend fun getUserDetails(@Header("Authorization") authHeader: String, @Body credentials: LoginRequest): UserDetailsResponse

    @GET("/events/all")
    suspend fun getAllEvents(@Header("Authorization") authorization: String): List<EventDTO>
}