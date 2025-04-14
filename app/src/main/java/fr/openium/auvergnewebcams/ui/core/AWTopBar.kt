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
import androidx.compose.ui.res.painterResource
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme


@Composable
fun AWTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    onNavigateTo: () -> Unit,
    onNavigateToOpt: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter = painterResource(id = R.drawable.ic_close),
    iconDescription: String = "",
    iconOpt: Painter = painterResource(id = R.drawable.ic_close),
    iconDescriptionOpt: String = "",
    isOptionalButtton: Boolean = false,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = AWAppTheme.colors.white,
                style = AWAppTheme.typography.p1
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                onNavigateBack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    tint = AWAppTheme.colors.white,
                    contentDescription = "Arrow"
                )
            }
        },
        actions = {
            if (isOptionalButtton) {
                IconButton(onClick = onNavigateToOpt) {
                    Icon(
                        painter = iconOpt,
                        contentDescription = iconDescriptionOpt,
                        tint = Color.White
                    )
                }
            }
            IconButton(onClick = onNavigateTo) {
                Icon(
                    painter = icon,
                    contentDescription = iconDescription,
                    tint = Color.White
                )
            }
        },
        backgroundColor = AWAppTheme.colors.greyVeryDark,
        contentColor = Color.White,
        modifier = modifier
    )
}