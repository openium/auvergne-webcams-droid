package fr.openium.auvergnewebcams.ui.map.components

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.mapbox.geojson.Polygon
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.coroutine.awaitCameraForCoordinates
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.GenericStyle
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import fr.openium.auvergnewebcams.enums.MapStyle
import fr.openium.auvergnewebcams.ext.navigateToLocationSettings
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import fr.openium.auvergnewebcams.model.entity.Webcam

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    sections: List<SectionWithCameras>,
    canBeHD: Boolean,
    goToWebcamDetail: (Webcam) -> Unit,
    mapStyle: MapStyle = MapStyle.ROADS,
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
            },
            style = {
                GenericStyle(
                    style = mapStyle.style
                )
            }
        ) {
            MapEffect(sections) { mapView ->
                mapView.location.updateSettings {
                    locationPuck = createDefault2DPuck()
                    enabled = true
                }
                // Only for single section
                if (sections.size == 1) {
                    // Focus on section position
                    mapViewportState.setCameraOptions(
                        getCameraPositionBySection(
                            mapView,
                            sections.first()
                        )
                    )
                }
            }

            sections.forEach { sectionWithCamera ->
                val section = sectionWithCamera.section
                sectionWithCamera.webcams
                    .filter { it.hidden == false && it.longitude != null && it.latitude != null }
                    .forEach { webcam ->
                        MapWebcamAnnotation(
                            webcam = webcam,
                            section = section,
                            webcamPreviewUid = webcamPreviewUid,
                            canBeHD = canBeHD,
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

private suspend fun getCameraPositionBySection(
    mapView: MapView,
    sectionWithCameras: SectionWithCameras,
): CameraOptions {

    val triangleCoordinates: ArrayList<Point> = ArrayList()

    sectionWithCameras.webcams
        .filter { it.hidden == false && it.longitude != null && it.latitude != null }
        .forEach { webcam ->
            triangleCoordinates.add(
                Point.fromLngLat(
                    webcam.longitude ?: sectionWithCameras.section.longitude,
                    webcam.latitude ?: sectionWithCameras.section.latitude
                )
            )
        }

    val polygon = Polygon.fromLngLats(listOf(triangleCoordinates))

    return mapView.mapboxMap.awaitCameraForCoordinates(
        polygon.coordinates().flatten(),
        cameraOptions { },
        EdgeInsets(100.0, 100.0, 100.0, 100.0)
    )
}