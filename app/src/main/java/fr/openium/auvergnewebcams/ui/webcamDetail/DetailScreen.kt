package fr.openium.auvergnewebcams.ui.webcamDetail

import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.getUrlForWebcam
import fr.openium.auvergnewebcams.ui.core.AWTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailScreen(
    webcamId: Long,
    typeWebcam: String?,
    onNavigateBack: () -> Unit,
    vm: ViewModelWebcamDetail = koinViewModel()
) {
    LaunchedEffect(webcamId) {
        vm.loadWebcam(webcamId)
    }

    val state by vm.state.collectAsState()

    when (state) {
        is ViewModelWebcamDetail.State.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ViewModelWebcamDetail.State.Error -> {
            val message = (state as ViewModelWebcamDetail.State.Error).message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: $message", color = Color.Red)
            }
        }

        is ViewModelWebcamDetail.State.Loaded -> {
            val webcam = (state as ViewModelWebcamDetail.State.Loaded).webcam
            val isVideoMode = typeWebcam == "viewsurf" || typeWebcam == "video"

            if (isVideoMode) {
                // --- VIDEO MODE ---
                val videoUrl = webcam.getUrlForWebcam(vm.prefUtils.isWebcamsHighQuality, canBeVideo = true)
                Media3VideoPlayer(videoUrl = videoUrl)

            } else {
                // --- IMAGE MODE ---
                val imageUrl = if (vm.prefUtils.isWebcamsHighQuality && !webcam.imageHD.isNullOrBlank()) {
                    webcam.getUrlForWebcam(canBeHD = true, canBeVideo = false)
                } else if (!webcam.imageLD.isNullOrBlank()) {
                    webcam.getUrlForWebcam(canBeHD = false, canBeVideo = false)
                } else null

                val painter = rememberAsyncImagePainter(model = imageUrl)
                Box(modifier = Modifier.fillMaxSize()) {
                    if (painter.state is AsyncImagePainter.State.Loading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    Image(
                        painter = painter,
                        contentDescription = webcam.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            AWTopBar(
                title = webcam.title ?: "",
                onNavigateBack = onNavigateBack,
                onNavigateTo = { },
                icon = painterResource(id = R.drawable.ic_more),
                iconDescription = stringResource(id = R.string.map_title),
                modifier = Modifier
            )

        }
    }
}

@Composable
fun Media3VideoPlayer(
    videoUrl: String
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    var isVideoLoading by remember { mutableStateOf(true) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true


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

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    setBackgroundColor(
                        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                            ContextCompat.getColor(ctx, R.color.grey_medium)
                        else
                            ContextCompat.getColor(ctx, R.color.black)
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        if (isVideoLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
