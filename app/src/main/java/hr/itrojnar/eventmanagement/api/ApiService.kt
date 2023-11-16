package hr.itrojnar.eventmanagement.api

import hr.itrojnar.eventmanagement.model.CreateEventDTO
import hr.itrojnar.eventmanagement.model.EventDTO
import hr.itrojnar.eventmanagement.model.LoginRequest
import hr.itrojnar.eventmanagement.model.UserDetailsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/users/details")
    suspend fun getUserDetails(@Header("Authorization") authHeader: String, @Body credentials: LoginRequest): UserDetailsResponse

    @GET("/events/all")
    suspend fun getAllEvents(@Header("Authorization") authorization: String): List<EventDTO>

    @POST("/events/create")
    suspend fun createEvent(@Header("Authorization") authorization: String, @Body event: CreateEventDTO): EventDTO

    @DELETE("/events/delete/{eventId}")
    suspend fun deleteEvent(@Header("Authorization") authorization: String, @Path("eventId") eventId: Long): Response<Unit>
}