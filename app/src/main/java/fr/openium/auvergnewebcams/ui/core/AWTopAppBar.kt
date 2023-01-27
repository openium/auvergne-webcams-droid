package fr.openium.auvergnewebcams.ui.core

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.AWTheme

@Composable
fun AWTopAppBar(
    title: String,
    onClickHomeButton: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        elevation = 0.dp,
        backgroundColor = AWAppTheme.colors.greyVeryDark
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            onClickHomeButton?.let {
                IconButton(onClick = it) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = null,
                        tint = AWAppTheme.colors.white
                    )
                }
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                color = AWAppTheme.colors.white,
                style = AWAppTheme.typography.toolbar,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }

    }
}

@Composable
@Preview
fun AWTopAppBarPreview() {
    AWTheme {
        AWTopAppBar(
            title = "",
            onClickHomeButton = {}
        )
    }
}