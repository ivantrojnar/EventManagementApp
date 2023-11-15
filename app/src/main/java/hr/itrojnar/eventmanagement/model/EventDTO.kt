package hr.itrojnar.eventmanagement.model

import java.math.BigDecimal
import java.time.LocalDate

data class EventDTO(
    val id: Long,
    val picture: String?,
    val name: String,
    val maxAttendees: Int,
    val numAttendees: Int,
    val address: String,
    val description: String,
    val date: String,
    val price: BigDecimal
)
