package fr.openium.auvergnewebcams.ui.webcamDetail.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.piasy.biv.view.BigImageView
import fr.openium.auvergnewebcams.custom.SimpleImageLoaderCallback
import fr.openium.auvergnewebcams.ext.getUrlForWebcam
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import kotlinx.coroutines.delay
import timber.log.Timber
import java.io.File
import kotlin.math.abs
import kotlin.math.min

private fun getZoomValue(bigImageView: BigImageView): Float {
    var zoomValue = if (bigImageView.ssiv.sWidth <= bigImageView.ssiv.sHeight) {
        bigImageView.ssiv.width.toFloat() / bigImageView.ssiv.sWidth
    } else {
        bigImageView.ssiv.height.toFloat() / bigImageView.ssiv.sHeight
    }

    if (abs(zoomValue - 0.1) < 0.2f) {
        zoomValue += 0.2f
    }

    return zoomValue * 0.75f
}


@Composable
fun WebcamDetailImage(
    webcam: Webcam,
    isWebcamsHighQuality: Boolean,
    setLastLoadingSuccess: (Boolean, File?) -> Unit
) {
    var showProgress by remember {
        mutableStateOf(true)
    }
    var imageURL: Uri? = null

    if (isWebcamsHighQuality && !webcam.imageHD.isNullOrBlank()) {
        imageURL = Uri.parse(webcam.getUrlForWebcam(canBeHD = true, canBeVideo = false))
    } else if (!webcam.imageLD.isNullOrBlank()) {
        imageURL = Uri.parse(webcam.getUrlForWebcam(canBeHD = false, canBeVideo = false))
    }

    var bigImageView by remember {
        mutableStateOf<BigImageView?>(null)
    }
    var nbRetry by remember {
        mutableStateOf(0)
    }
    LaunchedEffect(key1 = nbRetry, bigImageView, block = {
        delay(25)
        bigImageView?.let {
            if (it.ssiv.isReady) {
                it.ssiv.animateScaleAndCenter(
                    min(it.ssiv.maxScale, getZoomValue(it)),
                    it.ssiv.center
                )?.withInterruptible(false)?.withDuration(200L)?.start()
            } else {
                nbRetry += 1
            }
        }
    })

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(modifier = Modifier.fillMaxSize(),
            factory = { context ->
                BigImageView(context).apply {

                    setImageLoaderCallback(object : SimpleImageLoaderCallback() {
                        override fun onSuccess(image: File) {
                            setLastLoadingSuccess(true, image)

                            bigImageView = this@apply
                            showProgress = false

                        }

                        override fun onFail(error: Exception) {
                            setLastLoadingSuccess(false, null)
                            showProgress = false
                            Timber.e(error)
                        }

                        override fun onStart() {
                            showProgress = true
                        }
                    })
                    setInitScaleType(BigImageView.INIT_SCALE_TYPE_CENTER_INSIDE)
                    imageURL?.also { showImage(imageURL) }
                }
            })
        if (showProgress) {
            CircularProgressIndicator(
                color = AWAppTheme.colors.white,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}