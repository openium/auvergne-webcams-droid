package fr.openium.auvergnewebcams.ui.main.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
    goToSectionList: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                goToSectionList()
            }
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp),
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
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                color = AWAppTheme.colors.white,
                style = AWAppTheme.typography.title
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.wrapContentWidth()) {
                Text(
                    text = pluralStringResource(id = R.plurals.nb_cameras_format, count = webcamsCount, webcamsCount),
                    color = AWAppTheme.colors.greyLight,
                    style = AWAppTheme.typography.subtitle
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right_extra_small),
                    contentDescription = title,
                    tint = AWAppTheme.colors.greyLight
                )
            }

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
            goToSectionList = {}
        )
    }
}