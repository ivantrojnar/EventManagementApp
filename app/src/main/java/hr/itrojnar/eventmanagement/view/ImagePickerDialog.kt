package hr.itrojnar.eventmanagement.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import hr.itrojnar.eventmanagement.R

@Composable
fun ImagePickerDialog(
    onTakePhoto: () -> Unit,
    onSelectFromGallery: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.choose_picture)) },
        text = {
            Column {
                TextButton(onClick = onTakePhoto) {
                    Text(text = stringResource(R.string.take_photo))
                }
                TextButton(onClick = onSelectFromGallery) {
                    Text(text = stringResource(R.string.select_from_gallery))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}