package hr.itrojnar.eventmanagement.view

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import hr.itrojnar.eventmanagement.R
import hr.itrojnar.eventmanagement.api.ApiRepository
import hr.itrojnar.eventmanagement.api.RetrofitClient
import hr.itrojnar.eventmanagement.model.EventDTO
import hr.itrojnar.eventmanagement.model.UserDetailsResponse
import hr.itrojnar.eventmanagement.nav.Graph
import hr.itrojnar.eventmanagement.utils.getAccessToken
import hr.itrojnar.eventmanagement.utils.getUserInfo
import java.io.ByteArrayOutputStream

@Composable
fun MainScreen(navHostController: NavHostController) {

    val context = LocalContext.current
    val accessToken = getAccessToken(context)
    val user = getUserInfo(context)
    val apiRepository = ApiRepository(RetrofitClient.apiService)
    var userDetails by remember { mutableStateOf<UserDetailsResponse?>(null) }
    var userEvents by remember { mutableStateOf(emptyList<EventDTO>()) }

    val logoutClick: () -> Unit = {
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
                userDetails?.userType == "ADMIN" -> AdminView(
                    logoutClick,
                    userDetails!!,
                    userEvents!!
                )

                userDetails?.userType == "USER" -> UserView(logoutClick)
                else -> CircularProgressIndicator()
            }
        }
    }
}


@Composable
fun AdminView(logoutClick: () -> Unit, userDetails: UserDetailsResponse, events: List<EventDTO>) {

    var isAddEventVisible by remember { mutableStateOf(false) }
    val gradient = Brush.horizontalGradient(listOf(Color(0xFFCF753A), Color(0xFFB33161)))
    val context = LocalContext.current

    var showImagePickerDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }


    val takePictureLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            bitmap?.let {
                val bytes = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path = MediaStore.Images.Media.insertImage(
                    context.contentResolver, it, "Title", null
                )
                imageUri = Uri.parse(path)
                imageUri?.let { uri ->
                    //viewModel.setImageUri(uri)
                }
            }
        }

    val pickImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            //viewModel.setImageUri(uri)
        }

    if (showImagePickerDialog) {
        ImagePickerDialog(
            onTakePhoto = {
                takePictureLauncher.launch(null)
                showImagePickerDialog = false
            },
            onSelectFromGallery = {
                pickImageLauncher.launch("image/*")
                showImagePickerDialog = false
            },
            onDismissRequest = { showImagePickerDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopBar(logoutClick)
        // Add Event Button
        Button(
            onClick = { isAddEventVisible = !isAddEventVisible }, modifier = Modifier
                .padding(start = 16.dp, top = 20.dp)
                .background(gradient, shape = RoundedCornerShape(10.dp))
                .height(ButtonDefaults.MinHeight + 7.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(stringResource(R.string.toggle_add_new_event), fontSize = 15.sp)
        }
        AnimatedVisibility(visible = isAddEventVisible) {
            AddEventForm()
        }
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

@Composable
fun AddEventForm() {
    // Your form layout goes here, with fields for picture, event name, address, etc.
    // You can use standard Compose UI components like TextField, DatePicker, TimePicker, etc.
    // Wrap the entire form in an animated modifier for the dropdown effect.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .animateContentSize() // Animated size for dropdown effect
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(text = stringResource(R.string.create_new_event), fontSize = 16.sp)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.LightGray))
        }
    }
}