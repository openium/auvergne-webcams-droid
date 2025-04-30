package fr.openium.auvergnewebcams.ui.core

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme

@Composable
fun ConfirmOpenSettingsDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        text = {
            Text(text = stringResource(R.string.settings_notifications_dialog), style = AWAppTheme.typography.p1,)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.generic_ok), color = AWAppTheme.colors.white, style = AWAppTheme.typography.p3)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.generic_cancel), color = AWAppTheme.colors.white, style = AWAppTheme.typography.p3)
            }
        },
        onDismissRequest = {
            onDismiss()
        },
        contentColor = AWAppTheme.colors.white,
        backgroundColor = AWAppTheme.colors.greyVeryDark,
        shape = RoundedCornerShape(12.dp)
    )
}
