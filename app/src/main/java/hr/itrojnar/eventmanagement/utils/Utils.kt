package hr.itrojnar.eventmanagement.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.bumptech.glide.Glide
import hr.itrojnar.eventmanagement.model.UserAuthDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

fun cleanBase64String(base64String: String): String {
    return base64String.replace("\n", "")
}

// Function to convert image URI to Base64 string
suspend fun convertImageUriToBase64(context: Context, imageUri: Uri): String {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            inputStream?.use { input ->
                val bitmap: Bitmap = BitmapFactory.decodeStream(input)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                return@withContext android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext ""
    }
}

fun formatDateAndTime(dateString: String): Pair<String, String>? {
    val patterns = listOf("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", "yyyy-MM-dd'T'HH:mm:ss")

    for (pattern in patterns) {
        try {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            val localDateTime = LocalDateTime.parse(dateString, formatter)

            // Format date and time
            val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

            val formattedDate = localDateTime.format(dateFormatter)
            val formattedTime = localDateTime.format(timeFormatter)

            return Pair(formattedDate, formattedTime)
        } catch (e: DateTimeParseException) {
            // Parsing failed with this pattern, try the next one
        }
    }

    // Parsing failed with all patterns
    return null
}