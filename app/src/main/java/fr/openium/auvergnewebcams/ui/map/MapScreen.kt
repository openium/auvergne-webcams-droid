package fr.openium.auvergnewebcams.ui.map

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.enums.MapStyle
import fr.openium.auvergnewebcams.ext.navigateToLocationSettings
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.core.AWTopBar
import fr.openium.auvergnewebcams.ui.map.components.MapWebcamAnnotation
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    onNavigateBack: () -> Unit,
    goToWebcamDetail: (Webcam) -> Unit,
    vm: MapViewModel = koinViewModel(),
) {

    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }

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

    val sectionState by vm.state.collectAsStateWithLifecycle()

    if (!locationPermissionState.allPermissionsGranted) {
        LaunchedEffect(key1 = Unit) {
            locationPermissionState.launchMultiplePermissionRequest()
        }
    } else {
        LaunchedEffect(key1 = Unit) {
            context.navigateToLocationSettings()
        }
    }
    Scaffold(
        backgroundColor = AWAppTheme.colors.greyVeryDark,
        topBar = {
            AWTopBar(
                title = if (vm.sectionTitle != null) {
                    stringResource(R.string.map_title_with_name, vm.sectionTitle)
                } else {
                    stringResource(id = R.string.map_title)
                },
                onNavigateBack = onNavigateBack,
                onNavigateTo = { menuExpanded = true },
                icon = painterResource(id = R.drawable.ic_settings),
                iconDescription = stringResource(id = R.string.map_title),
                isOptionalButton = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                dropdownMenu = {
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier
                            .background(AWAppTheme.colors.greyMedium)
                    ) {
                        DropdownMenuItem(onClick = {
                            menuExpanded = false
                            vm.prefUtils.mapStyle = MapStyle.OUTSIDE.style
                            vm.switchMapStyle(MapStyle.OUTSIDE)
                        }) {
                            Text(
                                text = stringResource(id = R.string.map_style_outside_menu),
                                color = AWAppTheme.colors.white,
                                style = AWAppTheme.typography.p1
                            )
                        }
                        DropdownMenuItem(onClick = {
                            menuExpanded = false
                            vm.prefUtils.mapStyle = MapStyle.DARK.style
                            vm.switchMapStyle(MapStyle.DARK)
                        }) {
                            Text(
                                text = stringResource(id = R.string.map_style_dark_menu),
                                color = AWAppTheme.colors.white,
                                style = AWAppTheme.typography.p1
                            )
                        }
                        DropdownMenuItem(onClick = {
                            menuExpanded = false
                            vm.prefUtils.mapStyle = MapStyle.LIGHT.style
                            vm.switchMapStyle(MapStyle.LIGHT)
                        }) {
                            Text(
                                text = stringResource(id = R.string.map_style_light_menu),
                                color = AWAppTheme.colors.white,
                                style = AWAppTheme.typography.p1
                            )
                        }
                        DropdownMenuItem(onClick = {
                            menuExpanded = false
                            vm.prefUtils.mapStyle = MapStyle.SATELLITE.style
                            vm.switchMapStyle(MapStyle.SATELLITE)
                        }) {
                            Text(
                                text = stringResource(id = R.string.map_style_satellite_menu),
                                color = AWAppTheme.colors.white,
                                style = AWAppTheme.typography.p1
                            )
                        }
                        DropdownMenuItem(onClick = {
                            menuExpanded = false
                            vm.prefUtils.mapStyle = MapStyle.ROADS.style
                            vm.switchMapStyle(MapStyle.ROADS)
                        }) {
                            Text(
                                text = stringResource(id = R.string.map_style_roads_menu),
                                color = AWAppTheme.colors.white,
                                style = AWAppTheme.typography.p1
                            )
                        }
                    }
                },
            )
        },
        content = { paddingValues ->
            when (val state = sectionState) {
                MapViewModel.State.Loading -> {
                    Box(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center), color = AWAppTheme.colors.white
                        )
                    }
                }

                is MapViewModel.State.Loaded -> {

                    val mapStyle = state.mapStyle
                    val canBeHD = state.canBeHD

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(paddingValues)
                            .navigationBarsPadding()
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
                            MapEffect(state.sections) { mapView ->
                                mapView.location.updateSettings {
                                    locationPuck = createDefault2DPuck()
                                    enabled = true
                                }
                                // Only for single section
                                if (state.sections.size == 1) {
                                    // Focus on section position
                                    mapViewportState.setCameraOptions(
                                        getCameraPositionBySection(
                                            mapView,
                                            state.sections.first()
                                        )
                                    )
                                }
                            }

                            state.sections.forEach { sectionWithCamera ->
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
            }
        }
    )
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

