package hr.itrojnar.eventmanagement.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.math.BigDecimal

class EventViewModel: ViewModel() {

    var eventName = mutableStateOf("")
    var maxAttendees = mutableStateOf("")
    var address = mutableStateOf("")
    var description = mutableStateOf("")
    var date = mutableStateOf("")
    var price = mutableStateOf("")
    var imageUri = mutableStateOf<String?>(null) // Assuming imageUri will be stored as a string (base64)
    var numAttendees = mutableStateOf(0)

    val isReadyToCreateEvent: Boolean
        get() = eventName.value.isNotBlank() &&
                maxAttendees.value.isNotBlank() &&
                address.value.isNotBlank() &&
                description.value.isNotBlank() &&
                date.value.isNotBlank() &&
                price.value.isNotBlank() &&
                imageUri.value != null

    fun createEvent() {
        if (isReadyToCreateEvent) {
            // Convert maxAttendees and price to appropriate types
            val maxAttendeesInt = maxAttendees.value.toInt()
            val priceBigDecimal = BigDecimal(price.value)

            // TODO: Perform necessary actions with the event details
            // You can add API calls or other logic here

            // Reset values after event creation
            resetEventDetails()
        }
    }

    private fun resetEventDetails() {
        eventName.value = ""
        maxAttendees.value = ""
        address.value = ""
        description.value = ""
        date.value = ""
        price.value = ""
        imageUri.value = null
        numAttendees.value = 0
    }
}