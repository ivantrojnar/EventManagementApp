package hr.itrojnar.eventmanagement.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.isTraceInProgress
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import hr.itrojnar.eventmanagement.model.UserAuthDetails

@Composable
fun GradientButton(
    text: String,
    gradient : Brush,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Transparent),
        onClick = { onClick() },
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .then(modifier),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = text, color = Color.White)
        }
    }
}

// Function to save user information in SharedPreferences
fun saveUserInfo(context: Context, username: String, password: String, accessToken: String, refreshToken: String) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPreferences.edit()
    editor.putString("username", username)
    editor.putString("password", password)
    editor.putString("accessToken", accessToken)
    editor.putString("refreshToken", refreshToken)
    editor.apply()
}

// Function to get user information from SharedPreferences
fun getUserInfo(context: Context): UserAuthDetails {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", "") ?: ""
    val password = sharedPreferences.getString("password", "") ?: ""
    val accessToken = sharedPreferences.getString("accessToken", "") ?: ""
    val refreshToken = sharedPreferences.getString("refreshToken", "") ?: ""
    return UserAuthDetails(username, password, accessToken, refreshToken)
}

fun getAccessToken(context: Context): String {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("accessToken", "") ?: ""
}