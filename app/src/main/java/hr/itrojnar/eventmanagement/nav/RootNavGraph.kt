package hr.itrojnar.eventmanagement.nav

import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hr.itrojnar.eventmanagement.R
import hr.itrojnar.eventmanagement.api.AuthRepository
import hr.itrojnar.eventmanagement.api.RetrofitClient
import hr.itrojnar.eventmanagement.utils.getUserInfo
import hr.itrojnar.eventmanagement.utils.saveUserInfo
import hr.itrojnar.eventmanagement.view.LoginRegisterScreen
import hr.itrojnar.eventmanagement.view.MainScreen
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun RootNavGraph(navController: NavHostController) {

    val context = LocalContext.current

    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.AUTH
    ) {
        val authRepository = AuthRepository(RetrofitClient.authService)

        composable(route = Graph.AUTH) {
            val coroutineScope = rememberCoroutineScope()

            val userRegisterSuccesText = stringResource(id = R.string.user_registered_successfully)

            LoginRegisterScreen(
                onLoginClick = { username, password ->

                    coroutineScope.launch {
                        try {
                            val result = authRepository.login(username, password)

                            val accessToken = result.accessToken
                            val refreshToken = result.refreshToken

                            saveUserInfo(context, username, password, accessToken, refreshToken)

                            navController.popBackStack()
                            navController.navigate(Graph.MAIN)
                        } catch (e: Exception) {
                            println("Exception: ${e.message}")
                        }
                    }
                },
                onRegisterClick = { username, password ->

                    coroutineScope.launch {
                        try {
                            val result = authRepository.register(username, password)
                            if (result.isSuccessful) {
                                Toast.makeText(context, userRegisterSuccesText, Toast.LENGTH_SHORT).show()
                            } else {
                                println("Registration failed: ${result.code()}")
                            }
                        } catch (e: Exception) {
                            println("Exception during registration: ${e.message}")
                        }
                    }
                }
            )
        }
        composable(route = Graph.MAIN) {
            MainScreen(navHostController = navController)
        }
    }
}