package fr.openium.auvergnewebcams.ui.core

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.getUrlForWebcam
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.AWTheme

@Composable
fun WebcamPicture(
    webcam: Webcam,
    imageLoader: ImageLoader,
    canBeHD: Boolean,
    modifier: Modifier = Modifier,
    goToWebcamDetail: () -> Unit
) {
    var showProgress by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.clickable(onClick = goToWebcamDetail),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier.aspectRatio(16f / 10f),
            imageLoader = imageLoader,
            model = webcam.getUrlForWebcam(canBeHD = canBeHD, canBeVideo = false),
            contentDescription = webcam.title,
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
            },
            contentScale = ContentScale.Crop
        )
        if (showProgress) {
            CircularProgressIndicator(
                color = AWAppTheme.colors.white
            )
        }
        if (showError) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp)
                    .background(color = AWAppTheme.colors.greyVeryDarkTransparent)
                    .align(Alignment.BottomCenter),
                text = stringResource(id = R.string.generic_not_up_to_date),
                color = AWAppTheme.colors.greyLight,
                style = AWAppTheme.typography.p3,
                textAlign = TextAlign.Center
            )
        }
    }
}