package fr.openium.auvergnewebcams.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.core.WebcamPicture
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme

@Composable
fun WebcamItem(
    webcam: Webcam,
    canBeHD: Boolean,
    imageLoader: ImageLoader,
    goToWebcamDetail: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            WebcamPicture(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AWAppTheme.colors.blue),
                webcam = webcam,
                canBeHD = canBeHD,
                imageLoader = imageLoader,
                goToWebcamDetail = goToWebcamDetail
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            text = webcam.title ?: "",
            color = AWAppTheme.colors.greyLight,
            style = AWAppTheme.typography.p1,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
