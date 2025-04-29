package fr.openium.auvergnewebcams.ui.webcamDetail

import android.Manifest
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.getUrlForWebcam
import fr.openium.auvergnewebcams.ext.launchSignalWebcamNotWorking
import fr.openium.auvergnewebcams.ui.core.AWTopBar
import fr.openium.auvergnewebcams.ui.core.WebcamVideo
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.auvergnewebcams.utils.DateUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DetailScreen(
    webcamId: Long, onNavigateBack: () -> Unit, viewModel: ViewModelWebcamDetail = koinViewModel()
) {

    LaunchedEffect(webcamId) { viewModel.loadWebcam(webcamId) }

    var menuExpanded by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current

    LaunchedEffect(viewModel) {
        viewModel.errorMessage.collectLatest {
            scaffoldState.snackbarHostState.showSnackbar(
                message = context.getString(R.string.generic_no_application_for_action),
                duration = SnackbarDuration.Short
            )
        }
    }

    when (state) {
        is ViewModelWebcamDetail.State.Loading -> Unit

        is ViewModelWebcamDetail.State.Loaded -> {
            val webcam = (state as ViewModelWebcamDetail.State.Loaded).webcam

            val rememberPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS, {
                    if (it) {
                        viewModel.saveWebcam(context)
                    }
                })
            } else {
                null
            }

            var asyncImageState: AsyncImagePainter.State by remember {
                mutableStateOf(AsyncImagePainter.State.Empty)
            }

            Scaffold(
                backgroundColor = AWAppTheme.colors.greyVeryDark, scaffoldState = scaffoldState, topBar = {
                    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                        ) {
                            Column {

                                AWTopBar(
                                    title = (state as? ViewModelWebcamDetail.State.Loaded)?.webcam?.title ?: "",
                                    onNavigateBack = onNavigateBack,
                                    onNavigateTo = { menuExpanded = true },
                                    icon = painterResource(id = R.drawable.ic_more),
                                    iconDescription = stringResource(id = R.string.map_title),
                                    isOptionalButton = true,
                                    iconOpt = painterResource(id = R.drawable.ic_refresh),
                                    iconDescriptionOpt = stringResource(id = R.string.detail_refresh_menu),
                                    onNavigateToOpt = {
                                        viewModel.loadWebcam(webcamId)
                                    },
                                    dropdownMenu = {
                                        DropdownMenu(
                                            expanded = menuExpanded,
                                            onDismissRequest = { menuExpanded = false },
                                            modifier = Modifier.background(AWAppTheme.colors.greyMedium)
                                        ) {
                                            DropdownMenuItem(onClick = {
                                                menuExpanded = false
                                                viewModel.shareWebcam(
                                                    context,
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
                                                rememberPermissionState?.launchPermissionRequest() ?: kotlin.run {
                                                    viewModel.saveWebcam(context)
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
                                    LastUpdateText(lastUpdate = webcam.lastUpdate, dateUtils = viewModel.dateUtils)
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
                            if (webcam.isVideo) {
                                WebcamVideo(
                                    webcam = webcam,
                                    canBeHD = viewModel.prefUtils.isWebcamsHighQuality
                                )
                            } else {

                                var boxSize by remember { mutableStateOf(IntSize.Zero) }
                                Column(modifier = Modifier
                                    .fillMaxSize()
                                    .onSizeChanged { boxSize = it }
                                    .background(AWAppTheme.colors.greyDark)
                                    .let {
                                        if (asyncImageState is AsyncImagePainter.State.Error) {
                                            it.clickable {
                                                AnalyticsUtils.signalProblemClicked(context)
                                                context.launchSignalWebcamNotWorking(webcam) {
                                                    coroutineScope.launch {
                                                        scaffoldState.snackbarHostState.showSnackbar(
                                                            message = context.getString(R.string.generic_no_email_app),
                                                            duration = SnackbarDuration.Short
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            it
                                        }
                                    }) {
                                    Box(modifier = Modifier
                                        .weight(1f)
                                        .onSizeChanged { boxSize = it }
                                        .background(AWAppTheme.colors.greyDark)
                                        .let {
                                            if (asyncImageState is AsyncImagePainter.State.Success) {
                                                it

                                            } else {
                                                it

                                            }
                                        }) {
                                        AsyncImage(
                                            ImageRequest.Builder(LocalContext.current)
                                                .data(webcam.getUrlForWebcam(canBeHD = viewModel.prefUtils.isWebcamsHighQuality))
                                                .memoryCachePolicy(CachePolicy.DISABLED).build(),
                                            contentDescription = webcam.title,
                                            contentScale = if (asyncImageState is AsyncImagePainter.State.Success) ContentScale.Fit else ContentScale.Inside,
                                            error = painterResource(R.drawable.ic_broken_camera),
                                            onLoading = {
                                                asyncImageState = it
                                            },
                                            onSuccess = {
                                                asyncImageState = it
                                            },
                                            onError = {
                                                asyncImageState = it
                                            },
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        if (asyncImageState is AsyncImagePainter.State.Loading) {
                                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                        }
                                    }

                                    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                                        when (asyncImageState) {

                                            is AsyncImagePainter.State.Error -> {
                                                WebcamNotWorkingFull()
                                            }

                                            is AsyncImagePainter.State.Success -> {
                                                if (!viewModel.dateUtils.isUpToDate(webcam.lastUpdate)) {
                                                    WebcamNotWorking()
                                                }
                                            }

                                            AsyncImagePainter.State.Empty,
                                            is AsyncImagePainter.State.Loading -> Unit
                                        }
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
fun LastUpdateText(lastUpdate: Long?, dateUtils: DateUtils) {
    lastUpdate?.let { updateTime ->
        val formattedDate = dateUtils.getDateInFullFormat(updateTime)
        Text(
            text = stringResource(R.string.generic_last_update_format, formattedDate),
            modifier = Modifier
                .fillMaxWidth()
                .background(AWAppTheme.colors.greyVeryDark)
                .padding(10.dp),
            style = MaterialTheme.typography.body2.copy(
                color = AWAppTheme.colors.greyLight, fontSize = 14.sp
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
            color = AWAppTheme.colors.greyLight, fontSize = 14.sp
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
fun WebcamNotWorkingFull(
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.detail_not_working_title),
            style = MaterialTheme.typography.body1,
            color = Color.White,
            modifier = Modifier.padding(top = 50.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(id = R.string.detail_not_working_text),
            style = MaterialTheme.typography.h6,
            color = AWAppTheme.colors.blue,
            modifier = Modifier.padding(top = 24.dp),
            textAlign = TextAlign.Center
        )
    }
}
