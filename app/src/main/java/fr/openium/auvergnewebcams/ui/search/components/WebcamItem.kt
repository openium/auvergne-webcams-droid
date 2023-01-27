package fr.openium.auvergnewebcams.ui.search.components

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
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.core.WebcamPicture
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme

@Composable
fun WebcamItem(
    webcam: Webcam,
    goToWebcamDetail: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .height(150.dp)
        ) {
            WebcamPicture(
                webcam = webcam,
                goToWebcamDetail = goToWebcamDetail
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = webcam.title ?: "",
            color = AWAppTheme.colors.greyLight,
            style = AWAppTheme.typography.p1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            textAlign = TextAlign.Center,
            maxLines = 2
        )

    }
}
