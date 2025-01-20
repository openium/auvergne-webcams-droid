package fr.openium.auvergnewebcams.ui.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.AWTheme

@Composable
fun SectionWeather(
    weatherIcon: Int,
    weatherTemp: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = weatherIcon),
            contentDescription = null,
            tint = AWAppTheme.colors.blue,
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = stringResource(id = R.string.weather_degree_celsius_format, weatherTemp),
            color = AWAppTheme.colors.blue,
            style = AWAppTheme.typography.p3,
        )
    }
}

@Preview
@Composable
private fun SectionWeatherPreview() {
    AWTheme {
        SectionWeather(
            weatherIcon = R.drawable.ic_weather_cloudy,
            weatherTemp = 12,
        )
    }
}
