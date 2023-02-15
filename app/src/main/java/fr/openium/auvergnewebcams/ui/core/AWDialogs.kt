package fr.openium.auvergnewebcams.ui.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.AWTheme

@Composable
fun AWDialogMessage(
    title: String,
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(modifier = Modifier
            .background(color = AWAppTheme.colors.black)
            .wrapContentSize()
            .padding(24.dp)) {
            Text(
                text = title,
                color = AWAppTheme.colors.white,
                style = AWAppTheme.typography.p1,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = AWAppTheme.colors.greyLight,
                style = AWAppTheme.typography.p3,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = onButtonClick,
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = AWAppTheme.colors.white
                ),
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.End)
            ) {
                Text(
                    text = buttonText.uppercase(),
                    style = AWAppTheme.typography.button
                )
            }
        }
    }
}

@Composable
@Preview
fun AWDialogMessagePreview() {
    AWTheme {
        AWDialogMessage(
            title = "title",
            message = "dialog message",
            buttonText = "OK",
            onButtonClick = {},
            onDismiss = {}
        )
    }
}