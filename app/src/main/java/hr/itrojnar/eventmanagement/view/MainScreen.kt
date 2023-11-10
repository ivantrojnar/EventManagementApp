package hr.itrojnar.eventmanagement.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import hr.itrojnar.eventmanagement.api.ApiRepository
import hr.itrojnar.eventmanagement.api.RetrofitClient
import hr.itrojnar.eventmanagement.model.EventDTO
import hr.itrojnar.eventmanagement.model.UserDetailsResponse
import hr.itrojnar.eventmanagement.utils.getAccessToken

@Composable
fun MainScreen(navHostController: NavHostController) {

    val context = LocalContext.current

    val accessToken = getAccessToken(context)

    val apiRepository = ApiRepository(RetrofitClient.apiService)

    var userDetails by remember { mutableStateOf<UserDetailsResponse?>(null) }
    var userEvents by remember { mutableStateOf(emptyList<EventDTO>()) }

    LaunchedEffect(accessToken) {
        //userDetails = apiRepository.getUserDetails(accessToken)
        userEvents = apiRepository.getAllEvents(accessToken)
    }

    // UI
    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (userEvents.isNotEmpty()) {
                // Display your content based on userDetails
                // Example: Text(userDetails.username)
                for (event in userEvents) {
                    println(event)
                }
            } else {
                // Display loading indicator or placeholder
                CircularProgressIndicator()
            }
        }
    }
}