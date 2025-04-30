package fr.openium.auvergnewebcams.ui.core

import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.getUrlForWebcam
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme


@Composable
fun WebcamVideo(
    webcam: Webcam,
    canBeHD: Boolean
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    var isVideoLoading by remember { mutableStateOf(true) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(webcam.getUrlForWebcam(canBeHD))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            repeatMode = ExoPlayer.REPEAT_MODE_ONE

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    Handler(Looper.getMainLooper()).post {
                        isVideoLoading = playbackState != ExoPlayer.STATE_READY
                    }
                }
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AWAppTheme.colors.greyMedium)
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    controllerShowTimeoutMs = 1500
                    controllerHideOnTouch = true
                    controllerAutoShow = true
                    setBackgroundColor(
                        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) ContextCompat.getColor(ctx, R.color.grey_medium)
                        else ContextCompat.getColor(ctx, R.color.black)
                    )
                }
            }, modifier = Modifier.fillMaxSize()
        )
        if (isVideoLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center), color = AWAppTheme.colors.white
            )
        }
    }
}