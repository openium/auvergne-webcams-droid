package fr.openium.auvergnewebcams.ui.sectionDetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.core.SectionHeader
import fr.openium.auvergnewebcams.ui.core.WebcamItem
import fr.openium.auvergnewebcams.utils.ImageUtils

@Composable
fun SectionDetailScreen(
    section: SectionWithCameras?,
    canBeHD: Boolean,
    imageLoader: ImageLoader,
    goToWebcamDetail: (Webcam) -> Unit
) {
    val context = LocalContext.current

    section?.let {
        val image = ImageUtils.getImageResourceAssociatedToSection(context, section.section)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            item {
                SectionHeader(
                    title = section.section.title ?: "",
                    webcamsCount = section.webcams.size,
                    image = image
                )
            }
            items(section.webcams) {
                WebcamItem(
                    webcam = it,
                    canBeHD = canBeHD,
                    imageLoader = imageLoader,
                    goToWebcamDetail = {
                        goToWebcamDetail(it)
                    }
                )
            }
        }
    }

}