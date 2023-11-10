package hr.itrojnar.eventmanagement.api

import hr.itrojnar.eventmanagement.model.EventDTO
import hr.itrojnar.eventmanagement.model.UserDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {

    @GET("/users/details")
    suspend fun getUserDetails(@Header("Authorization") authHeader: String): UserDetailsResponse

    @GET("/events/all")
    suspend fun getAllEvents(@Header("Authorization") authorization: String): List<EventDTO>
}