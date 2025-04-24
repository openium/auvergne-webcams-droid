package fr.openium.auvergnewebcams.ui.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
    isPrimaryButton: Boolean = true,
    isOptionalButton: Boolean = false,
    isNavBack: Boolean = true,
    dropdownMenu: (@Composable () -> Unit)? = null
) {
    TopAppBar(
        title = {
            if (!isNavBack) {
                Text(
                    text = title,
                    color = AWAppTheme.colors.white,
                    style = AWAppTheme.typography.p1,
                    modifier = Modifier.offset(
                        x = (-48).dp
                    )
                )
            } else {
                Text(
                    text = title,
                    color = AWAppTheme.colors.white,
                    style = AWAppTheme.typography.p1,
                )
            }

        },
        navigationIcon = {
            if (isNavBack) {
                IconButton(onClick = {
                    onNavigateBack()
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        tint = AWAppTheme.colors.white,
                        contentDescription = "Arrow"
                    )
                }
            }
        },
        actions = {
            if (isOptionalButton) {
                IconButton(onClick = onNavigateToOpt) {
                    Icon(
                        painter = iconOpt,
                        contentDescription = iconDescriptionOpt,
                        tint = Color.White
                    )
                }
            }
            if (isPrimaryButton) {
                Box {
                    IconButton(onClick = onNavigateTo) {
                        Icon(
                            painter = icon,
                            contentDescription = iconDescription,
                            tint = Color.White
                        )
                    }

                    dropdownMenu?.let { menu ->
                        menu()
                    }
                }
            }
        },
        backgroundColor = AWAppTheme.colors.greyVeryDark,
        contentColor = Color.White,
        modifier = modifier
    )


}