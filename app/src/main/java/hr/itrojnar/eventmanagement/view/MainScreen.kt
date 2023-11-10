package hr.itrojnar.eventmanagement.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import hr.itrojnar.eventmanagement.api.ApiRepository
import hr.itrojnar.eventmanagement.api.RetrofitClient
import hr.itrojnar.eventmanagement.model.EventDTO
import hr.itrojnar.eventmanagement.model.UserDetailsResponse
import hr.itrojnar.eventmanagement.nav.Graph
import hr.itrojnar.eventmanagement.utils.getAccessToken
import hr.itrojnar.eventmanagement.utils.getUserInfo

@Composable
fun MainScreen(navHostController: NavHostController) {

    val context = LocalContext.current
    val accessToken = getAccessToken(context)
    val user = getUserInfo(context)
    val apiRepository = ApiRepository(RetrofitClient.apiService)
    var userDetails by remember { mutableStateOf<UserDetailsResponse?>(null) }
    var userEvents by remember { mutableStateOf(emptyList<EventDTO>()) }

    val logoutClick: () -> Unit = {
        println("LOGOUT click")
        navHostController.popBackStack()
        navHostController.navigate(Graph.ROOT)
    }

    LaunchedEffect(accessToken) {
        userDetails = apiRepository.getUserDetails(user.username, user.password, user.accessToken)
        userEvents = apiRepository.getAllEvents(accessToken)
    }

    // UI
    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                userDetails?.userType == "ADMIN" -> AdminView(logoutClick)
                userDetails?.userType == "USER" -> UserView(logoutClick)
                else -> CircularProgressIndicator()
                }
/*            if (userDetails != null) {
                println(userDetails)
                // Display your content based on userDetails
                // Example: Text(userDetails.username)
            } else {
                // Display loading indicator or placeholder
                CircularProgressIndicator()
            }*/
        }
    }
}


@Composable
fun AdminView(logoutClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopBar(logoutClick)
        Text("Welcome, Admin!", style = MaterialTheme.typography.headlineMedium)
        // Add ADMIN specific content here
    }
}

@Composable
fun UserView(logoutClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopBar(logoutClick)
        Text("Welcome, User!", style = MaterialTheme.typography.headlineMedium)
        // Add USER specific content here
    }
}

@Composable
fun TopBar(logoutClick: () -> Unit) {

    val gradient = Brush.horizontalGradient(listOf(Color(0xFFCF753A), Color(0xFFB33161)))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.ExitToApp, contentDescription = null, tint = Color.Transparent)
            }

            Text(
                "Event Management",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = logoutClick) {
                Icon(Icons.Filled.ExitToApp, contentDescription = null, tint = Color.White)
            }
        }
    }
}