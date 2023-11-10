package hr.itrojnar.eventmanagement.nav

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hr.itrojnar.eventmanagement.api.AuthRepository
import hr.itrojnar.eventmanagement.api.RetrofitClient
import hr.itrojnar.eventmanagement.view.LoginRegisterScreen
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun RootNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.AUTH
    ) {
        val authRepository = AuthRepository(RetrofitClient.authService)

        composable(route = Graph.AUTH) {
            val coroutineScope = rememberCoroutineScope()

            LoginRegisterScreen(
                onLoginClick = { username, password ->

                    coroutineScope.launch {
                        try {
                            val result = authRepository.login(username, password)
                            println("User2 logged in: $username")
                            navController.navigate(Graph.MAIN)
                        } catch (e: Exception) {
                            // Handle login failure (show error message, etc.)
                            println("Exception: ${e.message}")
                        }
                    }
                },
                onRegisterClick = { username, password ->

                    coroutineScope.launch {
                        try {
                            val result = authRepository.register(username, password)
                            if (result.isSuccessful) {
                                println("User registered successfully: $username")
                                // You can navigate to another screen or show a success message
                            } else {
                                // Handle registration failure (show error message, etc.)
                                println("Registration failed: ${result.code()}")
                            }
                        } catch (e: Exception) {
                            // Handle registration failure (show error message, etc.)
                            println("Exception during registration: ${e.message}")
                        }
                    }
                }
            )
        }
        composable(route = Graph.MAIN) {
            Text("Main Screen")
        }
    }
}