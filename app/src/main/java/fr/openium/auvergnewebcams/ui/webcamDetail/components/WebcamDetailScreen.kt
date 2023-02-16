package fr.openium.auvergnewebcams.ui.webcamDetail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.utils.DateUtils
import java.io.File

@Composable
fun WebcamDetailScreen(
    webcam: Webcam?,
    isVideo: Boolean,
    isUpToDate: Boolean,
    isLowQualityOnly: Boolean,
    isOrientationPortrait: Boolean,
    dateUtils: DateUtils,
    isWebcamsHighQuality: Boolean,
    setLastLoadingSuccess: (Boolean) -> Unit,
    onGetImageFile: (File?) ->  Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (isOrientationPortrait) {
            webcam?.lastUpdate?.let {
                WebcamDetailHeader(text = stringResource(id = R.string.generic_last_update_format, dateUtils.getDateInFullFormat(it)))
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            webcam?.let {
                if (isVideo) {
                    WebcamDetailVideo(
                        webcam = webcam,
                        isWebcamsHighQuality = isWebcamsHighQuality,
                        setLastLoadingSuccess = setLastLoadingSuccess
                    )
                } else {
                    WebcamDetailImage(
                        webcam = webcam,
                        isWebcamsHighQuality = isWebcamsHighQuality,
                        setLastLoadingSuccess = { success, file ->
                            setLastLoadingSuccess(success)
                            onGetImageFile(file)
                        }
                    )
                }
            }

        }

        if (isOrientationPortrait) {
            WebcamDetailFooter(isWebcamUpToDate = isUpToDate, isLowQualityOnly = isLowQualityOnly)
        }

    }
}