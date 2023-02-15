package fr.openium.auvergnewebcams.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.AWTheme

@Composable
fun SettingsItem(
    text: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = AWAppTheme.typography.p1,
            color = AWAppTheme.colors.white,
            modifier = Modifier.weight(1f)
        )
        value?.let {
            Text(
                text = value,
                style = AWAppTheme.typography.p1,
                color = AWAppTheme.colors.white
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right_small),
            contentDescription = text,
            tint = AWAppTheme.colors.white
        )
    }
}

@Composable
@Preview
fun SettingsItemNoValuePreview() {
    AWTheme {
        SettingsItem(
            text = "item",
            onClick = {}
        )
    }
}

@Composable
@Preview
fun SettingsItemWithValuePreview() {
    AWTheme {
        SettingsItem(
            text = "item",
            value = "10",
            onClick = {}
        )
    }
}