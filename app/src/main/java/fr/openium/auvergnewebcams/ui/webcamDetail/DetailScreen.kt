package fr.openium.auvergnewebcams.ui.webcamDetail

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Environment
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.getUrlForWebcam
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.core.AWTopBar
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
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
    val statusBarColor = AWAppTheme.colors.greyVeryDark

    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }

    LaunchedEffect(webcamId) { vm.loadWebcam(webcamId) }

    var menuExpanded by remember { mutableStateOf(false) }
    val state by vm.state.collectAsState()

    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current

    when (state) {
        is ViewModelWebcamDetail.State.Loading -> {

        }


        is ViewModelWebcamDetail.State.Loaded -> {
            val webcam = (state as ViewModelWebcamDetail.State.Loaded).webcam

            val isHighQuality = vm.prefUtils.isWebcamsHighQuality && !webcam.imageHD.isNullOrBlank()

            val isVideo = typeWebcam == "viewsurf" || typeWebcam == "video"

            val request = ImageRequest.Builder(LocalContext.current)
                .data(
                    when {
                        isHighQuality ->
                            webcam.getUrlForWebcam(canBeHD = true, canBeVideo = isVideo)

                        !webcam.imageLD.isNullOrBlank() ->
                            webcam.getUrlForWebcam(canBeHD = false, canBeVideo = isVideo)

                        else -> null
                    }
                )
                .memoryCachePolicy(CachePolicy.DISABLED)
                .build()

            val painter = rememberAsyncImagePainter(model = request)


            Scaffold(
                backgroundColor = AWAppTheme.colors.greyVeryDark,
                scaffoldState = scaffoldState,
                topBar = {
                    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                        ) {

                            Column {

                                AWTopBar(
                                    title = if (state is ViewModelWebcamDetail.State.Loaded)
                                        (state as ViewModelWebcamDetail.State.Loaded).webcam.title ?: ""
                                    else "",
                                    onNavigateBack = onNavigateBack,
                                    onNavigateTo = { menuExpanded = true },
                                    icon = painterResource(id = R.drawable.ic_more),
                                    iconDescription = stringResource(id = R.string.map_title),
                                    isOptionalButton = true,
                                    iconOpt = painterResource(id = R.drawable.ic_refresh),
                                    iconDescriptionOpt = stringResource(id = R.string.detail_refresh_menu),
                                    onNavigateToOpt = {
                                        vm.loadWebcam(webcamId)
                                    },
                                    dropdownMenu = {
                                        DropdownMenu(
                                            expanded = menuExpanded,
                                            onDismissRequest = { menuExpanded = false },
                                            modifier = Modifier
                                                .background(AWAppTheme.colors.greyMedium)
                                        ) {
                                            DropdownMenuItem(onClick = {
                                                menuExpanded = false
                                                shareWebcam(
                                                    webcam = webcam,
                                                    typeWebcam = typeWebcam ?: "",
                                                    context,
                                                    coroutineScope,
                                                    scaffoldState
                                                )
                                            }) {
                                                Text(
                                                    text = stringResource(id = R.string.detail_share_menu),
                                                    color = AWAppTheme.colors.white,
                                                    style = AWAppTheme.typography.p1
                                                )
                                            }
                                            DropdownMenuItem(onClick = {
                                                menuExpanded = false
                                                if (typeWebcam == "viewsurf" || typeWebcam == "video") {
                                                    saveWebcam(context, webcam, true, coroutineScope, scaffoldState)
                                                } else {
                                                    saveWebcam(context, webcam, false, coroutineScope, scaffoldState)

                                                }
                                            }) {
                                                Text(
                                                    text = stringResource(id = R.string.detail_save_menu),
                                                    color = AWAppTheme.colors.white,
                                                    style = AWAppTheme.typography.p1
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                )
                                key(webcam.lastUpdate) {
                                    LastUpdateText(lastUpdate = webcam.lastUpdate, dateUtils = vm.dateUtils)
                                }
                            }
                        }
                    }
                },


                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .navigationBarsPadding()
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (typeWebcam == "viewsurf" || typeWebcam == "video") {
                                Media3VideoPlayer(videoUrl = webcam.getUrlForWebcam(vm.prefUtils.isWebcamsHighQuality, canBeVideo = true))
                            } else {

                                var scale by remember { mutableFloatStateOf(1f) }
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
                        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                            if (!isVideo) {
                                when (painter.state) {

                                    is AsyncImagePainter.State.Error -> {
                                        WebcamNotWorking()
                                    }

                                    is AsyncImagePainter.State.Success -> {
                                        if (!vm.dateUtils.isUpToDate(webcam.lastUpdate)) {
                                            WebcamNotWorking()
                                        }
                                    }

                                    else -> {
                                    }
                                }
                            }
                        }
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

@Composable
fun WebcamNotWorking() {
    Text(
        text = stringResource(id = R.string.generic_not_up_to_date),
        modifier = Modifier
            .fillMaxWidth()
            .background(AWAppTheme.colors.greyMedium)
            .padding(10.dp),
        style = MaterialTheme.typography.body2.copy(
            color = AWAppTheme.colors.greyLight,
            fontSize = 14.sp
        ),
        textAlign = TextAlign.Center
    )
}

fun shareWebcam(
    webcam: Webcam,
    typeWebcam: String,
    context: Context,
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    val url = if (typeWebcam == "viewsurf" || typeWebcam == "video") {
        webcam.getUrlForWebcam(canBeHD = true, canBeVideo = true)
    } else {
        webcam.getUrlForWebcam(canBeHD = true, canBeVideo = false)

    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "${webcam.title}\n$url")
        putExtra(Intent.EXTRA_SUBJECT, webcam.title)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    val chooser = Intent.createChooser(intent, context.getString(R.string.generic_chooser))
    if (chooser.resolveActivity(context.packageManager) != null) {
        context.startActivity(chooser)
    } else {
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = context.getString(R.string.generic_no_application_for_action),
                duration = SnackbarDuration.Short
            )
        }
    }
}


fun saveWebcam(
    context: Context,
    webcam: Webcam,
    isVideo: Boolean = false,
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    val urlSrc = if (isVideo) {
        webcam.getUrlForWebcam(canBeHD = true, canBeVideo = true)
    } else {
        webcam.getUrlForWebcam(canBeHD = true, canBeVideo = false)
    }

    val fileExtension = if (isVideo) "mp4" else "jpg"
    val sanitizedTitle = webcam.title.orEmpty().replace("\\s+".toRegex(), "_")
    val fileName = "${sanitizedTitle}_${System.currentTimeMillis()}.$fileExtension"

    try {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(urlSrc)
        val request = DownloadManager.Request(uri).apply {
            setTitle(webcam.title)
            setDescription("Downloading ${if (isVideo) "video" else "image"}...")
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        }
        downloadManager.enqueue(request)
    } catch (ex: Exception) {
        ex.printStackTrace()
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = context.getString(R.string.generic_error),
                duration = SnackbarDuration.Short
            )
        }
    }
}
