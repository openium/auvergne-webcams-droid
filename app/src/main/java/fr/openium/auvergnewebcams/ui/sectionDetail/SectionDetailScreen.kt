package fr.openium.auvergnewebcams.ui.sectionDetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun SectionDetailScreen(
    sectionId: Long,
    vm: ViewModelSectionDetail = koinViewModel(),
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "Section",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(16.dp)
            )
            Divider()
        }
        // List de toutes les webcam
//        items(sectionDetailState!!.webcams) { webcam ->
//            WebcamItem(webcam = webcam, onClick = {
//                AnalyticsUtils.webcamDetailsClicked(LocalContext.current, webcam.title ?: "")
//                LocalContext.current.startActivity(
//                    ActivityWebcamDetail.getIntent(LocalContext.current, webcam)
//                )
//            })
//        }
    }
}
