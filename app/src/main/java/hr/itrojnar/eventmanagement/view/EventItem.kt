package hr.itrojnar.eventmanagement.view

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import hr.itrojnar.eventmanagement.R
import hr.itrojnar.eventmanagement.model.EventDTO
import hr.itrojnar.eventmanagement.utils.formatDateAndTime

@Composable
fun EventItem(event: EventDTO) {

    val gradient = Brush.horizontalGradient(listOf(Color(0xFFCF753A), Color(0xFFB33161)))
    var showDialog by remember { mutableStateOf(false) }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click if needed */ }
            .border(5.dp, gradient, shape = RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Image
            Base64Image(event.picture!!, modifier = Modifier.size(145.dp))

            Spacer(modifier = Modifier.width(16.dp))

            // Event details
            Column {
                Text(text = event.name, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                val (formattedDate, formattedTime) = formatDateAndTime(event.date)!!

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.date_event))
                        }
                        append(" $formattedDate")
                    }
                )
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.time_event))
                        }
                        append(" $formattedTime")
                    }
                )
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.price))
                        }
                        append(" ${event.price}$")
                    }
                )
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.max_attendees))
                        }
                        append(" ${event.maxAttendees}")
                    }
                )
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.attendees))
                        }
                        append(" ${event.numAttendees}")
                    }
                )
            }

            // Move the delete icon more to the right
            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showDialog = true }
            )

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    text = { Text(text = stringResource(R.string.are_you_sure_you_want_to_delete_this_event)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Handle delete action
                                showDialog = false
                            }
                        ) {
                            Text(text = stringResource(R.string.delete))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text(text = stringResource(R.string.cancel))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Base64Image(base64String: String, modifier: Modifier = Modifier) {
    val byteArray = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    val painter = rememberImagePainter(
        data = bitmap
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier
            .size(120.dp) // Adjust the size as needed
            .background(Color.Gray, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop // Center crop the image
    )
}