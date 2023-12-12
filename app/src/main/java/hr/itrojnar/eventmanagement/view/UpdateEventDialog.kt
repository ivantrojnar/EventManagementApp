package hr.itrojnar.eventmanagement.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanpra.composematerialdialogs.MaterialDialog
import hr.itrojnar.eventmanagement.api.ApiRepository
import hr.itrojnar.eventmanagement.model.EventDTO
import hr.itrojnar.eventmanagement.viewmodel.EventViewModel
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateEventDialog(
    event: EventDTO,
    apiRepository: ApiRepository,
    accessToken: String,
    eventViewModel: EventViewModel,
    onDismiss: () -> Unit
) {
    // Initialize ViewModel with existing event details
    //eventViewModel.initWithEvent(event)

    MaterialDialog(
        buttons = {
            positiveButton("Update") {
                // Handle the update logic here
                //val updatedEvent = eventViewModel.createUpdatedEvent()
                // Update the event using the repository and access token
                runBlocking {
                    // TODO update
                    //eventRepository.updateEvent(accessToken, updatedEvent)
                }
                onDismiss()
            }
            negativeButton("Cancel") {
                onDismiss()
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Update Event: ${event.name}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = eventViewModel.eventName.value,
                onValueChange = { eventViewModel.eventName.value = it },
                label = { Text(text = "Event Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = eventViewModel.description.value,
                onValueChange = { eventViewModel.description.value = it },
                label = { Text(text = "Event Description") },
                modifier = Modifier.fillMaxWidth()
            )
            // Add other fields as needed
        }
    }
}
