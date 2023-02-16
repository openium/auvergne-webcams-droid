package fr.openium.auvergnewebcams.ui.webcamDetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.AWTheme

@Composable
fun WebcamDetailFooter(
    isWebcamUpToDate: Boolean,
    isLowQualityOnly: Boolean
) {

    Column(
        modifier = Modifier
            .background(color = AWAppTheme.colors.greyMedium)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        if (!isWebcamUpToDate) {
            Text(
                text = stringResource(id = R.string.generic_not_up_to_date),
                color = AWAppTheme.colors.greyLight,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                style = AWAppTheme.typography.p2,
                textAlign = TextAlign.Center
            )
        }
        if (isLowQualityOnly) {
            Text(
                text = stringResource(id = R.string.webcam_only_available_low_quality),
                color = AWAppTheme.colors.greyLight,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                style = AWAppTheme.typography.p2,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
@Preview
fun WebcamDetailFooterPreview() {
    AWTheme {
        WebcamDetailFooter(
            isLowQualityOnly = true,
            isWebcamUpToDate = false
        )
    }
}