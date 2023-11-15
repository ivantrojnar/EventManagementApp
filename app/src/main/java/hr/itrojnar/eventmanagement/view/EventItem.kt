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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import hr.itrojnar.eventmanagement.model.EventDTO

@Composable
fun EventItem(event: EventDTO) {
    val gradient = Brush.horizontalGradient(listOf(Color(0xFFCF753A), Color(0xFFB33161)))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click if needed */ }
            .border(3.dp, gradient, shape = RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Image
            Base64Image(event.picture!!, modifier = Modifier.size(90.dp))

            Spacer(modifier = Modifier.width(16.dp))

            // Event details
            Column {
                Text(text = event.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Date: ${event.date}")
                Text(text = "Time: ${event.date}")
                Text(text = "Price: ${event.price}")
                Text(text = "Max Attendees: ${event.maxAttendees}")
                Text(text = "Attendees: ${event.numAttendees}")
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
            .size(120.dp)
            .background(Color.Gray, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}