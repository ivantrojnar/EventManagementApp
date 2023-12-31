package hr.itrojnar.eventmanagement.model

import java.math.BigDecimal
import java.time.LocalDate

data class UserEventDTO(
    val id: Long,
    val name: String,
    val address: String,
    val attending: Boolean,
    val picture: String,
    val maxAttendees: Int,
    val numAttendees: Int,
    val description: String,
    val date: LocalDate,
    val price: BigDecimal,
    val tickets: List<TicketDTO>
)
