package fr.openium.auvergnewebcams.ui.splash.components

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
import fr.openium.auvergnewebcams.ui.splash.ViewModelSplash
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
            Image(
                painter = painterResource(id = R.drawable.ic_splash),
                contentDescription = "Splash Image",
                modifier = Modifier
                    .size(250.dp),
                contentScale = ContentScale.Fit
            )
            SplashText(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp)
            )
        }

    }
}
