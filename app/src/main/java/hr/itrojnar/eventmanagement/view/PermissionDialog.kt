package hr.itrojnar.eventmanagement.view

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hr.itrojnar.eventmanagement.R

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider()
                Text(
                    text = if(isPermanentlyDeclined) {
                        stringResource(R.string.grant_permission)
                    } else {
                        stringResource(R.string.ok)
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isPermanentlyDeclined) {
                                onGoToAppSettingsClick()
                            } else {
                                onOkClick()
                            }
                        }
                        .padding(16.dp)
                )
            }
        },
        title = {
            Text(text = stringResource(R.string.permission_required))
        },
        text = {
            Text(
                text = permissionTextProvider.getDescription(
                    isPermanentlyDeclined = isPermanentlyDeclined
                ))
        },
        modifier = modifier
    )
}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class MediaImagesPermissionTextProvider(private val context: Context): PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            context.getString(R.string.it_seems_you_permanently_declined_permission_to_access_images_and_videos) +
                    context.getString(R.string.you_can_go_to_the_app_settings_to_grant_it)
        } else {
            context.getString(R.string.this_app_needs_access_to_your_gallery_so_that_you_can_take_choose_pictures_to_upload_while_using_the_app)
        }
    }

}

class CameraPermissionTextProvider(private val context: Context): PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            context.getString(R.string.it_seems_you_permanently_declined_camera_permission) +
                    context.getString(R.string.you_can_go_to_the_app_settings_to_grant_it)
        } else {
            context.getString(R.string.this_app_needs_access_to_your_camera_so_that_you_can_take_pictures_while_using_the_app)
        }
    }

}

class RecordAudioPermissionTextProvider(private val context: Context): PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            context.getString(R.string.it_seems_you_permanently_declined_microphone_permission) +
                    context.getString(R.string.you_can_go_to_the_app_settings_to_grant_it)
        } else {
            context.getString(R.string.this_app_needs_access_to_your_microphone_so_that_your_friends_can_hear_you)
        }
    }

}