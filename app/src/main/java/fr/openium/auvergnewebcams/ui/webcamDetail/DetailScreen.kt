package fr.openium.auvergnewebcams.ui.webcamDetail

import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.utils.DateUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailScreen(
    webcamId: Long,
    typeWebcam: String?,
    onNavigateBack: () -> Unit,
    vm: ViewModelWebcamDetail = koinViewModel()
) {
    LaunchedEffect(webcamId) { vm.loadWebcam(webcamId) }

    var menuExpanded by remember { mutableStateOf(false) }
    val state by vm.state.collectAsState()


    when (state) {
        is ViewModelWebcamDetail.State.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }


        is ViewModelWebcamDetail.State.Loaded -> {
            val webcam = (state as ViewModelWebcamDetail.State.Loaded).webcam


            Scaffold(
                topBar = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            AWTopBar(
                                title = if (state is ViewModelWebcamDetail.State.Loaded)
                                    (state as ViewModelWebcamDetail.State.Loaded).webcam.title ?: ""
                                else "",
                                onNavigateBack = onNavigateBack,
                                onNavigateTo = { menuExpanded = true },
                                icon = painterResource(id = R.drawable.ic_more),
                                iconDescription = stringResource(id = R.string.map_title),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            )
                            LastUpdateText(lastUpdate = webcam.lastUpdate, dateUtils = vm.dateUtils)
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(AWAppTheme.colors.greyMedium)
                        ) {
                            DropdownMenuItem(onClick = {
                                menuExpanded = false
                            }) {
                                Text(
                                    text = stringResource(id = R.string.detail_share_menu),
                                    color = AWAppTheme.colors.white,
                                    style = AWAppTheme.typography.p1
                                )
                            }
                            DropdownMenuItem(onClick = {
                                menuExpanded = false
                            }) {
                                Text(
                                    text = stringResource(id = R.string.detail_save_menu),
                                    color = AWAppTheme.colors.white,
                                    style = AWAppTheme.typography.p1
                                )
                            }
                        }

                    }
                },
                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (typeWebcam == "viewsurf" || typeWebcam == "video") {
                                Media3VideoPlayer(videoUrl = webcam.getUrlForWebcam(vm.prefUtils.isWebcamsHighQuality, canBeVideo = true))
                            } else {

                                var scale by remember { mutableStateOf(1f) }
                                var offset by remember { mutableStateOf(Offset.Zero) }
                                var boxSize by remember { mutableStateOf(IntSize.Zero) }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onSizeChanged { boxSize = it }
                                        .background(AWAppTheme.colors.greyDark)
                                        .pointerInput(Unit) {
                                            coroutineScope {
                                                launch {
                                                    detectTapGestures(
                                                        onDoubleTap = { tapOffset ->
                                                            val center = Offset(boxSize.width / 2f, boxSize.height / 2f)
                                                            if (scale == 1f) {
                                                                scale = 3f
                                                                offset = center - tapOffset
                                                            } else {
                                                                scale = 1f
                                                                offset = Offset.Zero
                                                            }
                                                        }
                                                    )
                                                }
                                                launch {
                                                    detectTransformGestures { _, pan, zoom, _ ->
                                                        scale = (scale * zoom).coerceIn(1f, 3f)
                                                        offset += pan
                                                        val maxX = (boxSize.width * (scale - 1)) / 2f
                                                        val maxY = (boxSize.height * (scale - 1)) / 2f
                                                        offset = Offset(
                                                            x = offset.x.coerceIn(-maxX, maxX),
                                                            y = offset.y.coerceIn(-maxY, maxY)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        .graphicsLayer {
                                            scaleX = scale
                                            scaleY = scale
                                            translationX = offset.x
                                            translationY = offset.y
                                        }
                                ) {
                                    val painter = rememberAsyncImagePainter(
                                        model = when {
                                            vm.prefUtils.isWebcamsHighQuality && !webcam.imageHD.isNullOrBlank() ->
                                                webcam.getUrlForWebcam(canBeHD = true, canBeVideo = false)

                                            !webcam.imageLD.isNullOrBlank() ->
                                                webcam.getUrlForWebcam(canBeHD = false, canBeVideo = false)

                                            else -> null
                                        }
                                    )
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
                        }
                        // Webcam non fonctionnelle ici
                    }
                }
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

@Composable
fun LastUpdateText(lastUpdate: Long?, dateUtils: DateUtils) {
    lastUpdate?.let { updateTime ->
        val formattedDate = dateUtils.getDateInFullFormat(updateTime)
        Text(
            text = "Mise à jour le $formattedDate",
            modifier = Modifier
                .fillMaxWidth()
                .background(AWAppTheme.colors.greyVeryDark)
                .padding(10.dp),
            style = MaterialTheme.typography.body2.copy(
                color = AWAppTheme.colors.greyLight,
                fontSize = 14.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}
