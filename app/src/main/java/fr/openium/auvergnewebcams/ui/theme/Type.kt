package fr.openium.auvergnewebcams.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.errorprone.annotations.Immutable
import fr.openium.auvergnewebcams.R

val Proxima = FontFamily(
    Font(R.font.proxima_nova_regular, FontWeight.Normal),
    Font(R.font.proxima_nova_bold, FontWeight.Bold),
    Font(R.font.proxima_nova_sbold, FontWeight.SemiBold)
)

val Circular = FontFamily(
    Font(R.font.circular_std_book, FontWeight.SemiBold)
)

@Immutable
data class AWTypography(
    val title: TextStyle,
    val subtitle: TextStyle,
    val error: TextStyle,
    val subtitle2: TextStyle
)

val Typography = AWTypography(
    title = TextStyle(
        fontSize = 18.sp,
        fontFamily = Proxima,
        fontWeight = FontWeight.Bold
    ),
    subtitle = TextStyle(
        fontSize = 13.sp,
        fontFamily = Proxima,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    ),
    subtitle2 = TextStyle(
        fontSize = 16.sp,
        fontFamily = Proxima,
        fontWeight = FontWeight.Normal
    ),
    error = TextStyle(
        fontSize = 13.sp,
        fontFamily = Proxima,
        fontWeight = FontWeight.Normal
    )
)

val LocalTypography = staticCompositionLocalOf {
    AWTypography(
        title = TextStyle.Default,
        subtitle = TextStyle.Default,
        error = TextStyle.Default,
        subtitle2 = TextStyle.Default
    )
}