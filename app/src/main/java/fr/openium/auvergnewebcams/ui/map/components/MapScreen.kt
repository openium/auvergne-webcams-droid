package fr.openium.auvergnewebcams.ui.map.components

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import fr.openium.auvergnewebcams.ext.navigateToLocationSettings
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import fr.openium.auvergnewebcams.model.entity.Webcam

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    sections: List<SectionWithCameras>,
    goToWebcamDetail: (Webcam) -> Unit,
) {
    val context = LocalContext.current

    var webcamPreviewUid by remember {
        mutableStateOf<Long>(0)
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(7.0)
            center(Point.fromLngLat(3.250595, 45.785931))
            pitch(0.0)
            bearing(0.0)
        }
    }

    val locationPermissionState: MultiplePermissionsState =
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            onPermissionsResult = { it ->
                if (it.all { it.value }) {
                    context.navigateToLocationSettings()
                }
            }
        )

    if (!locationPermissionState.allPermissionsGranted) {
        LaunchedEffect(key1 = Unit) {
            locationPermissionState.launchMultiplePermissionRequest()
        }
    } else {
        LaunchedEffect(key1 = Unit) {
            context.navigateToLocationSettings()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            onMapClickListener = {
                webcamPreviewUid = 0
                true
            }
        ) {
            MapEffect(Unit) { mapView ->
                mapView.location.updateSettings {
                    locationPuck = createDefault2DPuck()
                    enabled = true
                }
            }

            sections.forEach { sectionWithCamera ->
                val section = sectionWithCamera.section
                sectionWithCamera.webcams
                    .filter { it.hidden == false && it.longitude != null && it.latitude != null }
                    .forEach { webcam ->
                        val showWebcamPreview by remember(webcamPreviewUid, webcam) {
                            derivedStateOf {
                                webcamPreviewUid == webcam.uid
                            }
                        }

                        MapWebcamAnnotation(
                            webcam = webcam,
                            section = section,
                            showWebcamPreview = showWebcamPreview,
                            onWebcamClick = {
                                webcamPreviewUid = webcam.uid
                            },
                            goToWebcamDetail = {
                                goToWebcamDetail(webcam)
                            },
                        )
                    }
            }
        }
    }
}
