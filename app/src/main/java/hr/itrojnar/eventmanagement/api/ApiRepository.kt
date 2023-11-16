package hr.itrojnar.eventmanagement.api

import hr.itrojnar.eventmanagement.model.CreateEventDTO
import hr.itrojnar.eventmanagement.model.EventDTO
import hr.itrojnar.eventmanagement.model.LoginRequest
import hr.itrojnar.eventmanagement.model.UserDetailsResponse

class ApiRepository(private val apiService: ApiService) {

    suspend fun getUserDetails(username: String, password: String, accessToken: String): UserDetailsResponse {
        val authHeader = "Bearer $accessToken"
        val loginRequest = LoginRequest(username, password)
        return apiService.getUserDetails(authHeader, loginRequest)
    }

    suspend fun getAllEvents(accessToken: String): List<EventDTO> {
        val authHeader = "Bearer $accessToken"
        return apiService.getAllEvents(authHeader)
    }

    suspend fun createEvent(accessToken: String, createEventDTO: CreateEventDTO): EventDTO {
        val authHeader = "Bearer $accessToken"
        return apiService.createEvent(authHeader, createEventDTO)
    }

    suspend fun deleteEvent(accessToken: String, eventId: Long) {
        val authHeader = "Bearer $accessToken"
        apiService.deleteEvent(authHeader, eventId)
    }
}