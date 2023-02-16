package fr.openium.auvergnewebcams.ui.webcamDetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.AWTheme

@Composable
fun WebcamDetailHeader(
    text: String
) {
    Text(
        text = text, modifier = Modifier
            .fillMaxWidth()
            .background(color = AWAppTheme.colors.greyVeryDark)
            .padding(10.dp),
        style = AWAppTheme.typography.p2Italic,
        color = AWAppTheme.colors.greyLight,
        textAlign = TextAlign.Center
    )
}

@Composable
@Preview
fun WebcamDetailHeaderPreview() {
    AWTheme {
        WebcamDetailHeader(
            text = "title"
        )
    }
}