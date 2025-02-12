package fr.openium.auvergnewebcams.ui.splash.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R

@Composable
fun CloudWithAnimation(
    cloud: Painter = painterResource(R.drawable.ic_splash_cloud1),
    initialOffsetX: Dp = 0.dp,
    initialOffsetY: Dp = 0.dp,
    animatedOffsetX: Dp = 0.dp,
    size: Dp = 32.dp,
    animationAlphaDuration: Int = 1500,
    animationOffsetDuration: Int = 2000
) {
    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        startAnimation = true
    }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = animationAlphaDuration)
    )
    val offsetXAnim by animateDpAsState(
        targetValue = if (startAnimation) animatedOffsetX else 0.dp,
        animationSpec = tween(durationMillis = animationOffsetDuration)
    )

    Image(
        painter = cloud,
        contentDescription = "Cloud",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(size)
            .absoluteOffset(x = initialOffsetX + offsetXAnim, y = initialOffsetY)
            .alpha(alphaAnim)
    )
}