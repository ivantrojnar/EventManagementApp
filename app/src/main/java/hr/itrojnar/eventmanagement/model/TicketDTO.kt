package hr.itrojnar.eventmanagement.model

import java.math.BigDecimal

data class TicketDTO(
    val id: Long,
    val price: BigDecimal
)