package fr.openium.auvergnewebcams.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.colorResource
import fr.openium.auvergnewebcams.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AWTheme(content: @Composable () -> Unit) {
    val colors = AWColors(
        primary = colorResource(id = R.color.colorPrimary),
        primaryDark = colorResource(id = R.color.colorPrimaryDark),
        blue = colorResource(id = R.color.blue),
        black = colorResource(id = R.color.black),
        greyVeryDark = colorResource(id = R.color.grey_very_dark),
        greyDark = colorResource(id = R.color.grey_dark),
        greyVeryDarkTransparent = colorResource(id = R.color.grey_very_dark_transparent),
        greyMedium = colorResource(id = R.color.grey_medium),
        grey = colorResource(id = R.color.grey),
        greyLight = colorResource(id = R.color.grey_light),
        colorAccent = colorResource(id = R.color.colorAccent),
        white = colorResource(id = R.color.white),
        greyMediumTransparent = colorResource(id = R.color.grey_medium_transparent)
    )

    val primaryColor = colors.primary
    val backgroundColor = colors.primaryDark

    val selectionColors = remember(primaryColor, backgroundColor) {
        TextSelectionColors(
            handleColor = colors.primary,
            backgroundColor = primaryColor.copy(alpha = 0.4f)
        )
    }

    val rippleConfiguration =
        RippleConfiguration(
            color = AWAppTheme.colors.black,
            rippleAlpha = if (AWAppTheme.colors.black.luminance() > 0.5) {
                RippleAlpha(
                    pressedAlpha = 0.24f,
                    focusedAlpha = 0.24f,
                    draggedAlpha = 0.16f,
                    hoveredAlpha = 0.08f
                )
            } else {
                RippleAlpha(
                    pressedAlpha = 0.12f,
                    focusedAlpha = 0.12f,
                    draggedAlpha = 0.08f,
                    hoveredAlpha = 0.04f
                )
            }
        )

    CompositionLocalProvider(
        LocalColors provides colors,
        LocalContentAlpha provides ContentAlpha.high,
        LocalIndication provides ripple(),
        LocalRippleConfiguration provides rippleConfiguration,
        LocalTextSelectionColors provides selectionColors,
        LocalTypography provides Typography
    ) {
        ProvideTextStyle(value = Typography.h1) {
            content()
        }
    }
}

object AWAppTheme {

    val colors: AWColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val typography: AWTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}