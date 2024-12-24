package fr.openium.auvergnewebcams.ui.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.mapbox.geojson.Point
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.viewannotation.annotationAnchor
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.core.WebcamPicture
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.TriangleEdgeShape

@Composable
fun MapWebcamAnnotation(
    webcam: Webcam,
    section: Section,
    webcamPreviewUid: Long,
    canBeHD: Boolean,
    onWebcamClick: () -> Unit,
    goToWebcamDetail: () -> Unit,
) {
    val context = LocalContext.current

    val imageLoader by remember { mutableStateOf(ImageLoader(context)) }

    val webcamBackgroundColor by remember(section.mapColor) {
        mutableStateOf(section.mapColor)
    }

    val webcamLongitude by remember(webcam.longitude, section.longitude) {
        derivedStateOf {
            webcam.longitude ?: section.longitude
        }
    }

    val webcamLatitude by remember(webcam.latitude, section.latitude) {
        derivedStateOf {
            webcam.latitude ?: section.latitude
        }
    }

    val webcamIconName by remember(webcam.mapImageName, section.mapImageName) {
        derivedStateOf {
            webcam.mapImageName ?: section.mapImageName
        }
    }

    val webcamIconDescription by remember(webcam.title, section.title) {
        derivedStateOf {
            webcam.title ?: section.title
        }
    }

    val webcamPoint by remember(webcamLongitude, webcamLatitude) {
        derivedStateOf {
            Point.fromLngLat(
                webcamLongitude,
                webcamLatitude
            )
        }
    }

    val showWebcamPreview by remember(webcamPreviewUid, webcam) {
        derivedStateOf {
            webcamPreviewUid == webcam.uid
        }
    }

    ViewAnnotation(
        options = viewAnnotationOptions {
            geometry(
                webcamPoint
            )
            allowOverlap(true)
            allowOverlapWithPuck(true)
        }
    ) {
        MapWebcamButton(
            backgroundColor = webcamBackgroundColor,
            iconName = webcamIconName,
            iconDescription = webcamIconDescription,
            onClick = onWebcamClick
        )
    }

    if (showWebcamPreview) {
        ViewAnnotation(
            options = viewAnnotationOptions {
                geometry(
                    webcamPoint
                )
                annotationAnchor {
                    anchor(ViewAnnotationAnchor.BOTTOM)
                }
                allowOverlap(true)
                allowOverlapWithPuck(true)
            }
        ) {
            WebcamPicture(
                webcam = webcam,
                imageLoader = imageLoader,
                canBeHD = canBeHD,
                goToWebcamDetail = goToWebcamDetail,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .height(150.dp)
                    .background(
                        color = AWAppTheme.colors.greyMediumTransparent,
                        shape = TriangleEdgeShape(44, 30)
                    )
            )
        }
    }
}