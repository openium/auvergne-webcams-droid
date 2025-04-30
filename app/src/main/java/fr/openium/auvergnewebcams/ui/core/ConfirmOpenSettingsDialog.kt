package fr.openium.auvergnewebcams.ui.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme

@Composable
fun ConfirmOpenSettingsDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(modifier = Modifier.background(color = AWAppTheme.colors.greyVeryDark)) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(12.dp),
                backgroundColor = AWAppTheme.colors.greyVeryDark,
                contentColor = AWAppTheme.colors.white,
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.settings_notifications_dialog), style = AWAppTheme.typography.p3,
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(text = stringResource(R.string.generic_cancel), color = AWAppTheme.colors.white, style = AWAppTheme.typography.p3)
                        }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = onConfirm) {
                            Text(text = stringResource(R.string.generic_ok), color = AWAppTheme.colors.white, style = AWAppTheme.typography.p3)
                        }
                    }
                }
            }
        }
    }
}
