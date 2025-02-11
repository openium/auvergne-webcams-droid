package fr.openium.auvergnewebcams.ui.splash

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.splash.components.CloudWithAnimation
import fr.openium.auvergnewebcams.ui.splash.components.SplashText
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber


@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun SplashScreen(vm: ViewModelSplash = koinViewModel(), startActivityMain: () -> Unit) {

    DisposableEffect(Unit) {
        val job = vm.updateData()
            .subscribe({
                startActivityMain()
            }, { Timber.e(it) })

        onDispose {
            job.dispose()
        }
    }

    Box(
        modifier = Modifier
            .background(Color.DarkGray)
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(modifier = Modifier.size(250.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_splash),
                    contentDescription = "Splash Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                CloudWithAnimation(
                    cloud = painterResource(R.drawable.ic_splash_cloud1),
                    initialOffsetX = 55.dp,
                    initialOffsetY = 67.27.dp,
                    animatedOffsetX = (15).dp,
                    size = 50.50.dp
                )
                CloudWithAnimation(
                    cloud = painterResource(R.drawable.ic_splash_cloud2),
                    initialOffsetX = 150.dp,
                    initialOffsetY = 67.27.dp,
                    animatedOffsetX = (-15).dp,
                    size = 50.50.dp
                )
                CloudWithAnimation(
                    cloud = painterResource(R.drawable.ic_splash_cloud3),
                    initialOffsetX = 40.dp,
                    initialOffsetY = 67.27.dp,
                    animatedOffsetX = (15).dp,
                    size = 32.32.dp
                )
                CloudWithAnimation(
                    cloud = painterResource(R.drawable.ic_splash_cloud4),
                    initialOffsetX = 185.dp,
                    initialOffsetY = 67.27.dp,
                    animatedOffsetX = (-15).dp,
                    size = 32.32.dp
                )
                CloudWithAnimation(
                    cloud = painterResource(R.drawable.ic_splash_cloud5),
                    initialOffsetX = 40.dp,
                    initialOffsetY = 40.dp,
                    animatedOffsetX = 0.dp,
                    size = 40.40.dp,
                )
                CloudWithAnimation(
                    cloud = painterResource(R.drawable.ic_splash_cloud6),
                    initialOffsetX = 100.dp,
                    initialOffsetY = 10.0.dp,
                    animatedOffsetX = 0.dp,
                    size = 50.50.dp,
                )
                CloudWithAnimation(
                    cloud = painterResource(R.drawable.ic_splash_cloud7),
                    initialOffsetX = 191.dp,
                    initialOffsetY = 35.0.dp,
                    animatedOffsetX = 0.dp,
                    size = 40.40.dp,
                )
            }
            SplashText(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp)
            )
        }
    }
}
