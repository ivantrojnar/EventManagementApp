package hr.itrojnar.eventmanagement.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hr.itrojnar.eventmanagement.model.EventDTO

@Composable
fun EventItem(event: EventDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {  },
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = event.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Date: ${event.date}")
            Text(text = "Description: ${event.description}")
            Text(text = "Ticket Price: ${event.price}")
        }
    }
}