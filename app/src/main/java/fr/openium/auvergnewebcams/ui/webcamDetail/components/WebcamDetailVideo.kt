package fr.openium.auvergnewebcams.ui.webcamDetail.components

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import fr.openium.auvergnewebcams.ext.getUrlForWebcam
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.kotlintools.ext.showWithAnimationCompat
import kotlinx.android.synthetic.main.fragment_webcam_video.*

@Composable
fun WebcamDetailVideo(
    webcam: Webcam,
    isWebcamsHighQuality: Boolean,
    setLastLoadingSuccess: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val uri = webcam.getUrlForWebcam(isWebcamsHighQuality, true)

    var showProgress by remember {
        mutableStateOf(true)
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val video = MediaItem.fromUri(uri)
            this.setMediaItem(video)
            this.prepare()
            this.playWhenReady = true
            this.repeatMode = Player.REPEAT_MODE_ALL
        }
    }

    val player = remember {
        StyledPlayerView(context).apply {
            player = exoPlayer
            player?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if(playbackState == Player.STATE_READY) {
                        showProgress = false
                        setLastLoadingSuccess(true)
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    showProgress = false
                    setLastLoadingSuccess(false)

                }
            })
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        DisposableEffect(
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AndroidView(
                    modifier = Modifier.align(Alignment.Center).fillMaxSize(),
                    factory = {
                        player
                    }
                )
            }
        ) {
            onDispose {
                exoPlayer.release()
            }
        }
        if (showProgress) {
            CircularProgressIndicator(
                color = AWAppTheme.colors.white,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

}