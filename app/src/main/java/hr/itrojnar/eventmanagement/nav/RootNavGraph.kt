package hr.itrojnar.eventmanagement.nav

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hr.itrojnar.eventmanagement.view.LoginRegisterScreen

@Composable
fun RootNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.AUTH
    ) {
        composable(route = Graph.AUTH) {
            LoginRegisterScreen(
                onLoginClick = { username, password ->
                    navController.navigate(Graph.MAIN)
                },
                onRegisterClick = {
                    // Placeholder for registration logic
                }
            )
        }
        composable(route = Graph.MAIN) {
            Text("Main Screen")
        }
    }
}