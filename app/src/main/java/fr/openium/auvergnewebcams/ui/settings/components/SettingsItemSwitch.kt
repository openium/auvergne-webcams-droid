package fr.openium.auvergnewebcams.ui.settings.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.AWTheme

@Composable
fun SettingsItemSwitch(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = AWAppTheme.typography.p1,
            color = AWAppTheme.colors.white,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AWAppTheme.colors.blue,
                checkedTrackColor = AWAppTheme.colors.blue,
                uncheckedThumbColor = AWAppTheme.colors.greyLight
            )
        )
    }
}


@Composable
@Preview
fun SettingsItemSwitchPreview() {
    AWTheme {
        SettingsItemSwitch(
            text = "switch",
            isChecked = false,
            onCheckedChange = {}
        )
    }
}

@Composable
@Preview
fun SettingsItemSwitchCheckedPreview() {
    AWTheme {
        SettingsItemSwitch(
            text = "switch",
            isChecked = true,
            onCheckedChange = {}
        )
    }
}
