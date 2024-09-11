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
    val toolbar: TextStyle,
    val h1: TextStyle,
    val h2: TextStyle,
    val p1: TextStyle,
    val p1Italic: TextStyle,
    val p2Italic: TextStyle,
    val p3: TextStyle,
    val button: TextStyle
)

val Typography = AWTypography(
    toolbar = TextStyle(
        fontFamily = Circular,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    h1 = TextStyle(
        fontSize = 18.sp,
        fontFamily = Proxima,
        fontWeight = FontWeight.Bold
    ),
    h2 = TextStyle(
        fontSize = 20.sp,
        fontFamily = Proxima,
        fontWeight = FontWeight.SemiBold
    ),
    p1 = TextStyle(
        fontSize = 16.sp,
        fontFamily = Proxima,
        fontWeight = FontWeight.Normal
    ),
    p1Italic = TextStyle(
        fontSize = 16.sp,
        fontFamily = Proxima,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    ),
    p2Italic = TextStyle(
        fontSize = 13.sp,
        fontFamily = Proxima,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    ),
    p3 = TextStyle(
        fontSize = 13.sp,
        fontFamily = Proxima,
        fontWeight = FontWeight.Normal
    ),
    button = TextStyle(
        fontSize = 16.sp,
        fontFamily = Proxima,
        fontWeight = FontWeight.SemiBold
    )
)

val LocalTypography = staticCompositionLocalOf {
    AWTypography(
        toolbar = TextStyle.Default,
        h1 = TextStyle.Default,
        h2 = TextStyle.Default,
        p1 = TextStyle.Default,
        p1Italic = TextStyle.Default,
        p2Italic = TextStyle.Default,
        p3 = TextStyle.Default,
        button = TextStyle.Default
    )
}