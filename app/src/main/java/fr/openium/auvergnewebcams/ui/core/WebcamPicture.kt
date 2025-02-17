package fr.openium.auvergnewebcams.ui.core

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

@Composable
fun WebcamPicture(
    webcam: Webcam,
    imageLoader: ImageLoader,
    canBeHD: Boolean,
    goToWebcamDetail: () -> Unit,
    modifier: Modifier = Modifier,
    startingAlpha: Float = 0.5f,
    aspectRatio: Float = 10f,
    pageOffset: Float? = null,
) {
    var showProgress by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

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
                            // We animate the scaleX + scaleY, between 85% and 100%
                            lerp(
                                start = 0.85f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            ).also { scale ->
                                scaleX = scale
                                scaleY = scale / 1.2f
                            }
                            // We animate the alpha, between 50% and 100%
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

        if (showError) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .let { modifier ->
                        pageOffset?.let {
                            modifier.graphicsLayer {
                                // We animate the scaleX + scaleY, between 85% and 100%
                                lerp(
                                    start = 0.85f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                ).also { scale ->
                                    scaleX = scale
                                    scaleY = scale
                                }
                                // We animate the alpha, between 50% and 100%
                                alpha = lerp(
                                    start = startingAlpha,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                            }
                        } ?: modifier
                    }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(8.dp)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = AWAppTheme.colors.greyVeryDarkTransparent),
                        text = stringResource(id = R.string.generic_not_up_to_date),
                        color = AWAppTheme.colors.white,
                        style = AWAppTheme.typography.p3,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}