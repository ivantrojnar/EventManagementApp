package hr.itrojnar.eventmanagement.model

data class EventDTO(
    val id: Long,
    val picture: String?,
    val name: String,
    val maxAttendees: Int,
    val numAttendees: Int,
    val address: String,
    val description: String,
    val date: String
)
