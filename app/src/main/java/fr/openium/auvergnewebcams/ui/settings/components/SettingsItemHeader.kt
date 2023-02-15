package fr.openium.auvergnewebcams.ui.settings.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.AWTheme

@Composable
fun SettingsItemHeader(
    text: String
) {

    Text(
        text = text.uppercase(),
        color = AWAppTheme.colors.grey,
        style = AWAppTheme.typography.p3,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
@Preview
fun SettingsItemHeaderPreview() {
    AWTheme {
        SettingsItemHeader(
            text = "header"
        )
    }
}
