package hr.itrojnar.eventmanagement.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import hr.itrojnar.eventmanagement.model.EventDTO

@Composable
fun UpdateEventScreen(navController: NavHostController, eventDTO: EventDTO) {

    Text(eventDTO.name)
}