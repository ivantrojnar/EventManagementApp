package hr.itrojnar.eventmanagement.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import hr.itrojnar.eventmanagement.R
import hr.itrojnar.eventmanagement.api.ApiRepository
import hr.itrojnar.eventmanagement.api.RetrofitClient
import hr.itrojnar.eventmanagement.model.CreateEventDTO
import hr.itrojnar.eventmanagement.model.EventDTO
import hr.itrojnar.eventmanagement.model.UserDetailsResponse
import hr.itrojnar.eventmanagement.nav.Graph
import hr.itrojnar.eventmanagement.utils.cleanBase64String
import hr.itrojnar.eventmanagement.utils.convertImageUriToBase64
import hr.itrojnar.eventmanagement.utils.findActivity
import hr.itrojnar.eventmanagement.utils.getAccessToken
import hr.itrojnar.eventmanagement.utils.getUserInfo
import hr.itrojnar.eventmanagement.viewmodel.EventViewModel
import hr.itrojnar.eventmanagement.viewmodel.MainViewModel
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun MainScreen(navHostController: NavHostController) {

    val context = LocalContext.current
    val accessToken = getAccessToken(context)
    val user = getUserInfo(context)
    val apiRepository = ApiRepository(RetrofitClient.apiService)
    var userDetails by remember { mutableStateOf<UserDetailsResponse?>(null) }
    val allEvents by remember { mutableStateOf(mutableListOf<EventDTO>()) }

    val logoutClick: () -> Unit = {
        navHostController.popBackStack()
        navHostController.navigate(Graph.ROOT)
    }

    LaunchedEffect(accessToken) {
        userDetails = apiRepository.getUserDetails(user.username, user.password, user.accessToken)
        //allEvents = apiRepository.getAllEvents(accessToken).toMutableList()
        allEvents.addAll(apiRepository.getAllEvents(accessToken))
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
                    allEvents,
                    apiRepository,
                    accessToken
                )

                userDetails?.userType == "USER" -> UserView(logoutClick)
                else -> CircularProgressIndicator()
            }
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminView(
    logoutClick: () -> Unit,
    userDetails: UserDetailsResponse,
    events: MutableList<EventDTO>,
    apiRepository: ApiRepository,
    accessToken: String
) {

    var isAddEventVisible by remember { mutableStateOf(false) }
    val gradient = Brush.horizontalGradient(listOf(Color(0xFFCF753A), Color(0xFFB33161)))
    val fadedGradient = Brush.horizontalGradient(
        listOf(
            Color(0xFFCF753A).copy(alpha = 0.5f),
            Color(0xFFB33161).copy(alpha = 0.5f)
        )
    )

    val context = LocalContext.current
    val activity = context.findActivity()

    val focusManager = LocalFocusManager.current

    var showImagePickerDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val viewModel = viewModel<MainViewModel>()
    val eventViewModel: EventViewModel = viewModel<EventViewModel>()
    val dialogQueue = viewModel.visiblePermissionDialogQueue
    var showDialog by remember { mutableStateOf(false) }

    val commonPermissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CALL_PHONE
    )

    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        (listOf(Manifest.permission.READ_MEDIA_IMAGES) + commonPermissions).toTypedArray()
    } else {
        (listOf(Manifest.permission.READ_EXTERNAL_STORAGE) + commonPermissions).toTypedArray()
    }

    val multiplePermissionResultLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                permissionsToRequest.forEach { permission ->
                    viewModel.onPermissionResult(
                        permission = permission, isGranted = permissions[permission] == true
                    )
                }
            })

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
                    runBlocking {
                        val imageBase64 = convertImageUriToBase64(context, uri)
                        eventViewModel.imageUri.value = cleanBase64String(imageBase64)
                    }
                }
            }
        }

    val pickImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            runBlocking {
                val imageBase64 = convertImageUriToBase64(context, imageUri!!)
                eventViewModel.imageUri.value = cleanBase64String(imageBase64)
            }
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
        Button(
            onClick = { isAddEventVisible = !isAddEventVisible }, modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 20.dp, end = 16.dp)
                .background(gradient, shape = RoundedCornerShape(10.dp))
                .height(ButtonDefaults.MinHeight + 7.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(stringResource(R.string.toggle_add_new_event), fontSize = 15.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))
        AnimatedVisibility(visible = isAddEventVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .animateContentSize() // Animated size for dropdown effect
                    .verticalScroll(rememberScrollState())
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
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(Color.LightGray)
                    )
                    {
                        imageUri?.let { uri ->
                            val painter = rememberImagePainter(
                                data = uri,
                            )
                            Image(
                                painter = painter,
                                contentDescription = stringResource(R.string.selected_preview),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        IconButton(
                            onClick = {
                                multiplePermissionResultLauncher.launch(permissionsToRequest)
                                showImagePickerDialog = true
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            if (imageUri != null) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.edit),
                                    modifier = Modifier.size(36.dp)
                                )
                            } else {
                                Icon(
                                    Icons.Default.AddCircle,
                                    contentDescription = stringResource(R.string.camera),
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                    val datePickerDialogState = rememberMaterialDialogState()
                    val timePickerDialogState = rememberMaterialDialogState()
                    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
                    var selectedTime by remember { mutableStateOf(LocalTime.now()) }

                    MaterialDialog(
                        dialogState = datePickerDialogState,
                        buttons = {
                            positiveButton(stringResource(id = R.string.ok))
                            negativeButton(stringResource(id = R.string.cancel))
                        }
                    ) {
                        datepicker(
                            colors = DatePickerDefaults.colors(
                                headerBackgroundColor = Color(0xFFB33161),
                                dateActiveBackgroundColor = Color(0xFFCF753A),
                            )
                        ) { date ->
                            selectedDate = date
                            eventViewModel.date.value = date.toString()
                        }
                    }

                    MaterialDialog(
                        dialogState = timePickerDialogState,
                        buttons = {
                            positiveButton(stringResource(id = R.string.ok))
                            negativeButton(stringResource(id = R.string.cancel))
                        }
                    ) {
                        timepicker(
                            colors = TimePickerDefaults.colors(
                                activeBackgroundColor = Color(0xFFB33161),
                                selectorColor = Color(0xFFB33161)

                            )
                        ) { time ->
                            selectedTime = time
                        }
                    }

                    OutlinedTextField(
                        value = eventViewModel.eventName.value,
                        onValueChange = { eventViewModel.eventName.value = it },
                        label = { Text(text = stringResource(R.string.event_title)) },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        maxLines = 1,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    )

                    OutlinedTextField(
                        value = eventViewModel.description.value,
                        onValueChange = { eventViewModel.description.value = it },
                        label = { Text(text = stringResource(R.string.event_description)) },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        maxLines = 1,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            onValueChange = { /* Handle text input if needed */ },
                            readOnly = true,
                            shape = RoundedCornerShape(10.dp),
                            label = { Text(stringResource(R.string.date)) },
                            modifier = Modifier
                                .clickable {
                                    datePickerDialogState.show()
                                }
                                .weight(1f)
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Button(
                            onClick = {
                                datePickerDialogState.show()
                            },
                            modifier = Modifier
                                .background(gradient, shape = RoundedCornerShape(10.dp))
                                .height(ButtonDefaults.MinHeight + 10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(stringResource(R.string.select_date), fontSize = 15.sp)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                            onValueChange = { /* Handle text input if needed */ },
                            readOnly = true,
                            shape = RoundedCornerShape(10.dp),
                            label = { Text(stringResource(R.string.time)) },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Button(
                            onClick = {
                                timePickerDialogState.show()
                            },
                            modifier = Modifier
                                .background(gradient, shape = RoundedCornerShape(10.dp))
                                .height(ButtonDefaults.MinHeight + 10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(stringResource(R.string.select_time), fontSize = 15.sp)
                        }
                    }

                    OutlinedTextField(
                        value = eventViewModel.address.value,
                        onValueChange = { eventViewModel.address.value = it },
                        label = { Text(text = stringResource(R.string.event_address)) },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        maxLines = 1,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    )

                    OutlinedTextField(
                        value = eventViewModel.maxAttendees.value,
                        onValueChange = { eventViewModel.maxAttendees.value = it },
                        label = { Text(text = stringResource(R.string.maximum_number_of_attendees)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        maxLines = 1,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    )

                    OutlinedTextField(
                        value = eventViewModel.price.value,
                        onValueChange = { eventViewModel.price.value = it },
                        label = { Text(text = stringResource(R.string.ticket_price)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        maxLines = 1,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    )

                    Button(
                        onClick = {
                            runBlocking {
                                println(selectedDate)
                                println(selectedTime)
                                val dateTimeString = "$selectedDate" + "T" + "$selectedTime:00.000000"
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
                                val localDateTime = LocalDateTime.parse(dateTimeString, formatter)
                                eventViewModel.date.value = localDateTime.toString()

                                val newEvent =
                                    CreateEventDTO(
                                        eventViewModel.imageUri.value,
                                        eventViewModel.eventName.value,
                                        eventViewModel.maxAttendees.value.toInt(),
                                        0,
                                        eventViewModel.address.value,
                                        eventViewModel.description.value,
                                        eventViewModel.date.value,
                                        eventViewModel.price.value.toBigDecimal()
                                    )
                                val result = apiRepository.createEvent(
                                    accessToken,
                                    newEvent
                                )

                                events.add(result)
                            }
                        },
                        enabled = eventViewModel.isReadyToCreateEvent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(
                                if (eventViewModel.isReadyToCreateEvent) gradient else fadedGradient,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .height(ButtonDefaults.MinHeight + 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Create Event", fontSize = 15.sp, color = Color.White)
                    }
                }
            }
        }
        dialogQueue.reversed().forEach { permission ->
            PermissionDialog(permissionTextProvider = when (permission) {
                Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    MediaImagesPermissionTextProvider(context)
                }

                Manifest.permission.CAMERA -> {
                    CameraPermissionTextProvider(context)
                }

                Manifest.permission.RECORD_AUDIO -> {
                    RecordAudioPermissionTextProvider(context)
                }

                else -> return@forEach
            }, isPermanentlyDeclined = !activity!!.shouldShowRequestPermissionRationale(
                permission
            ), onDismiss = viewModel::dismissDialog, onOkClick = {
                viewModel.dismissDialog()
                multiplePermissionResultLauncher.launch(
                    arrayOf(permission)
                )
            }, onGoToAppSettingsClick = { openAppSettings(activity = activity) })
        }
        Text(
            "Welcome, Admin!",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            itemsIndexed(items = events) { index, event ->
                EventItem(event = event)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
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

fun openAppSettings(activity: Activity) {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", activity.packageName, null)
    ).also { activity.startActivity(it) }
}