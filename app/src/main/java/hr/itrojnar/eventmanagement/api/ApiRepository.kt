package hr.itrojnar.eventmanagement.api

import hr.itrojnar.eventmanagement.model.EventDTO
import hr.itrojnar.eventmanagement.model.UserDetailsResponse

class ApiRepository(private val apiService: ApiService) {

    suspend fun getUserDetails(accessToken: String): UserDetailsResponse {
        val authHeader = "Bearer $accessToken"
        return apiService.getUserDetails(authHeader)
    }

    suspend fun getAllEvents(accessToken: String): List<EventDTO> {
        val authHeader = "Bearer $accessToken"
        return apiService.getAllEvents(authHeader)
    }
}