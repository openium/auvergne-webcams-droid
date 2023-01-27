package fr.openium.auvergnewebcams.ui.splash.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {

    var visible by remember { mutableStateOf(false) }

    val animateAlpha: Float by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = LinearEasing
        )
    )

    val offsetXRight1 = remember { Animatable(0f) }
    val offsetXRight2 = remember { Animatable(0f) }
    val offsetXLeft1 = remember { Animatable(30f) }
    val offsetXLeft2 = remember { Animatable(50f) }

    LaunchedEffect(key1 = Unit, block = {
        delay(500)
        visible = true
        offsetXRight1.animateTo(
            targetValue = 30f,
            animationSpec = tween(
                durationMillis = 2000,
                easing = LinearEasing
            )
        )
    })
    LaunchedEffect(key1 = Unit, block = {
        offsetXRight2.animateTo(
            targetValue = 50f,
            animationSpec = tween(
                durationMillis = 2000,
                easing = LinearEasing
            )
        )
    })
    LaunchedEffect(key1 = Unit, block = {
        offsetXLeft1.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 2000,
                easing = LinearEasing
            )
        )
    })
    LaunchedEffect(key1 = Unit, block = {
        offsetXLeft2.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 2000,
                easing = LinearEasing
            )
        )
    })


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AWAppTheme.colors.greyDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.wrapContentSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_splash),
                    contentDescription = null,
                    modifier = Modifier.size(250.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_splash_cloud3),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 35.dp, top = 67.dp)
                        .offset {
                            IntOffset(
                                x = offsetXRight2.value.toInt(),
                                y = 0
                            )
                        }
                        .width(32.dp),
                    alpha = animateAlpha
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_cloud4),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 173.dp, top = 65.dp)
                        .offset {
                            IntOffset(
                                x = offsetXLeft2.value.toInt(),
                                y = 0
                            )
                        }
                        .width(41.dp),
                    alpha = animateAlpha
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_cloud1),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 68.dp, top = 58.dp)
                        .offset {
                            IntOffset(
                                x = offsetXRight1.value.toInt(),
                                y = 0
                            )
                        }
                        .width(44.dp),
                    alpha = animateAlpha
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_cloud2),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 144.dp, top = 72.dp)
                        .offset {
                            IntOffset(
                                x = offsetXLeft1.value.toInt(),
                                y = 0
                            )
                        }
                        .width(42.dp),
                    alpha = animateAlpha
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_splash_cloud5),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 101.dp, top = 24.dp)
                        .width(56.dp),
                    alpha = animateAlpha
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_cloud6),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 50.dp, top = 40.dp)
                        .width(42.dp),
                    alpha = animateAlpha
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_cloud7),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 188.dp, top = 32.dp)
                        .width(31.dp),
                    alpha = animateAlpha
                )
            }

            AnimatedVisibility(visible = visible) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.splash_loading_cameras),
                        color = AWAppTheme.colors.white,
                        style = AWAppTheme.typography.p1,
                        modifier = Modifier
                            .padding(top = 32.dp, bottom = 24.dp)
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = AWAppTheme.colors.white
                    )
                }
            }

        }
    }

}

@Composable
@Preview
fun SplashScreenPreview() {
    AWTheme {
        SplashScreen()
    }
}