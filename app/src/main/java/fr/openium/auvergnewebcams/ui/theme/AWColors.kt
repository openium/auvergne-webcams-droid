package fr.openium.auvergnewebcams.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.google.errorprone.annotations.Immutable

@Immutable
data class AWColors(
    val primary: Color,
    val primaryDark: Color,
    val colorAccent: Color,
    val white: Color,
    val greyLight: Color,
    val grey: Color,
    val greyMedium: Color,
    val greyMediumTransparent: Color,
    val greyDark: Color,
    val greyVeryDark: Color,
    val greyVeryDarkTransparent: Color,
    val black: Color,
    val blue: Color
)

val LocalColors = staticCompositionLocalOf {
    AWColors(
        primary = Color.Unspecified,
        primaryDark = Color.Unspecified,
        colorAccent = Color.Unspecified,
        white = Color.Unspecified,
        greyLight = Color.Unspecified,
        grey = Color.Unspecified,
        greyMedium = Color.Unspecified,
        greyMediumTransparent = Color.Unspecified,
        greyDark = Color.Unspecified,
        greyVeryDark = Color.Unspecified,
        greyVeryDarkTransparent = Color.Unspecified,
        black = Color.Unspecified,
        blue = Color.Unspecified
    )
}