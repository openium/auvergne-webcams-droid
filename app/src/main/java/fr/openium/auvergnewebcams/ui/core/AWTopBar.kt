package fr.openium.auvergnewebcams.ui.core

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import fr.openium.auvergnewebcams.R


@Composable
fun AWTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    onNavigateTo: () -> Unit,
    icon: Painter = painterResource(id = R.drawable.ic_close),
    iconDescription: String = "",
    modifier: Modifier,
) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = {
                onNavigateBack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Arrow"
                )
            }
        },
        actions = {
            IconButton(onClick = onNavigateTo) {
                Icon(
                    painter = icon,
                    contentDescription = iconDescription,
                    tint = Color.White
                )
            }
        },
        backgroundColor = colorResource(id = R.color.grey_very_dark),
        contentColor = Color.White,
        modifier = modifier
    )
}