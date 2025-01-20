package fr.openium.auvergnewebcams.ui.main.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.AWTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SectionHeader(
    title: String,
    webcamsCount: Int,
    @DrawableRes image: Int,
    goToSectionList: () -> Unit,
    weatherIcon: Int? = null,
    weatherTemp: Int? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = goToSectionList)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentDescription = title,
            painter = painterResource(id = image),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier.weight(2f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                color = AWAppTheme.colors.white,
                style = AWAppTheme.typography.h1
            )

            Row(
                modifier = Modifier.wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pluralStringResource(
                        id = R.plurals.nb_cameras_format,
                        count = webcamsCount,
                        webcamsCount
                    ),
                    color = AWAppTheme.colors.greyLight,
                    style = AWAppTheme.typography.p2Italic
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right_extra_small),
                    contentDescription = title,
                    tint = AWAppTheme.colors.greyLight
                )
            }
        }

        if (weatherIcon != null && weatherTemp != null) {
            SectionWeather(
                weatherIcon = weatherIcon,
                weatherTemp = weatherTemp,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
@Preview
fun SectionHeaderPreview() {
    AWTheme {
        SectionHeader(
            title = "test",
            webcamsCount = 3,
            image = R.drawable.categ_allier_landscape,
            goToSectionList = {},
            weatherIcon = R.drawable.ic_weather_cloudy,
            weatherTemp = 12
        )
    }
}