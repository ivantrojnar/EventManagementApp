package hr.itrojnar.eventmanagement.model

data class UserDetailsResponse(
    val id: Long,
    val username: String,
    val password: String,
    val userType: String,
    val allEvents: List<UserEventDTO>
)