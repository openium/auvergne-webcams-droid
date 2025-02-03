package fr.openium.auvergnewebcams.ui.splash.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import kotlinx.coroutines.delay


@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun SplashScreen() {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        isVisible = true
    }

    //Ajouter animations nuages
    val imageOffset by animateDpAsState(
        targetValue = if (isVisible) (-50).dp else 0.dp,
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = Modifier
            .background(Color.DarkGray)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_splash),
            contentDescription = "Splash Image",
            modifier = Modifier
                .align(Alignment.Center)
                .size(250.dp)
                .offset(y = imageOffset),
            contentScale = ContentScale.Fit
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 250.dp)
        ) {
            SplashText(isVisible = isVisible)
        }
    }
}
