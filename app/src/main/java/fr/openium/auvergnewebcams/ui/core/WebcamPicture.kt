package fr.openium.auvergnewebcams.ui.core

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.getUrlForWebcam
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.utils.DateUtils

@Composable
fun WebcamPicture(
    webcam: Webcam,
    imageLoader: ImageLoader,
    canBeHD: Boolean,
    goToWebcamDetail: () -> Unit,
    shouldDisplayBanner: Boolean = false,
    modifier: Modifier = Modifier,
    startingAlpha: Float = 0.5f,
    aspectRatio: Float = 10f,
    pageOffset: Float? = null,
) {
    val dateUtils: DateUtils = org.koin.androidx.compose.get()
    val context = LocalContext.current

    var showProgress by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    val urlForWebcam by remember(
        webcam.lastUpdate,
        canBeHD
    ) { mutableStateOf(webcam.getUrlForWebcam(canBeHD = canBeHD, canBeVideo = false)) }

    val painter = rememberAsyncImagePainter(
        model = urlForWebcam,
        imageLoader = imageLoader,
        onState = { state ->
            when (state) {
                AsyncImagePainter.State.Empty -> {
                    showProgress = true
                    showError = false
                }

                is AsyncImagePainter.State.Error -> {
                    showProgress = false
                    showError = true
                }

                is AsyncImagePainter.State.Loading -> {
                    showProgress = true
                    showError = false
                }

                is AsyncImagePainter.State.Success -> {
                    showProgress = false
                    showError = false
                    errorText = updateErrorText(context, dateUtils, webcam, shouldDisplayBanner)
                }
            }
        }
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .let { modifier ->
                    pageOffset?.let {
                        modifier.graphicsLayer {
                            lerp(
                                start = 0.85f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            ).also { scale ->
                                scaleX = scale
                                scaleY = scale / 1.2f
                            }
                            alpha = lerp(
                                start = startingAlpha,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                    } ?: modifier
                }
                .aspectRatio(16f / aspectRatio)
                .clip(RoundedCornerShape(4.dp))
                .background(color = AWAppTheme.colors.greyVeryDark)
                .clickable(onClick = goToWebcamDetail),
            painter = painter,
            contentDescription = "",
            contentScale = ContentScale.Crop
        )

        if (showProgress) {
            CircularProgressIndicator(
                color = AWAppTheme.colors.white
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .let { modifier ->
                    pageOffset?.let {
                        modifier.graphicsLayer {
                            lerp(
                                start = 0.85f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            ).also { scale ->
                                scaleX = scale
                                scaleY = scale
                            }
                            alpha = lerp(
                                start = startingAlpha,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                    } ?: modifier
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            if (showError) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AWAppTheme.colors.greyVeryDarkTransparent)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Image(
                        modifier = Modifier
                            .size(104.dp)
                            .aspectRatio(16f / aspectRatio),
                        painter = painterResource(id = R.drawable.ic_broken_camera),
                        contentDescription = "",
                        contentScale = ContentScale.Inside
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.generic_not_up_to_date),
                        color = AWAppTheme.colors.white,
                        style = AWAppTheme.typography.p3,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                if (errorText.isNotEmpty()) {
                    Text(
                        text = errorText,
                        color = AWAppTheme.colors.greyLight,
                        style = AWAppTheme.typography.p3,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AWAppTheme.colors.greyVeryDarkTransparent)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

fun updateErrorText(
    context: Context,
    dateUtils: DateUtils,
    webcam: Webcam,
    shouldDisplay: Boolean = false
): String {
    return when {
        !shouldDisplay -> ""
        dateUtils.isUpToDate(webcam.lastUpdate) -> ""
        else -> context.getString(R.string.generic_not_up_to_date)
    }
}
