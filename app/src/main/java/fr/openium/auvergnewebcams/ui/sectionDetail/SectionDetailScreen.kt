package fr.openium.auvergnewebcams.ui.sectionDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.core.AWTopBar
import fr.openium.auvergnewebcams.ui.core.SectionHeader
import fr.openium.auvergnewebcams.ui.core.WebcamPicture
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.utils.ImageUtils
import fr.openium.auvergnewebcams.utils.WeatherUtils
import org.koin.androidx.compose.koinViewModel


@Composable
fun SectionDetailScreen(
    sectionId: Long,
    goToWebcamDetail: (Webcam) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToMap: () -> Unit,
    vm: ViewModelSectionDetail = koinViewModel(),
) {

    val context = LocalContext.current

    LaunchedEffect(sectionId) {
        vm.loadSectionAndWebcams(sectionId)
    }
    val state by vm.state.collectAsState()

    when (state) {

        is ViewModelSectionDetail.State.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ViewModelSectionDetail.State.Loaded -> {
            val loadedState = state as ViewModelSectionDetail.State.Loaded
            val section = loadedState.section
            val webcams = loadedState.webcams.sortedBy { it.order }

            Box(modifier = Modifier.fillMaxSize()) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 56.dp)
                ) {
                    item {
                        SectionHeader(
                            title = section.title ?: "",
                            webcamsCount = webcams.count(),
                            image = ImageUtils.getImageResourceAssociatedToSection(context, section),
                            goToSectionList = {},
                            weatherIcon = section.weatherUid?.let { WeatherUtils.weatherImage(it) },
                            weatherTemp = section.weatherTemp?.let { WeatherUtils.convertKelvinToCelsius(it) }
                        )
                    }
                    items(items = webcams) { webcam ->
                        WebcamPicture(
                            pageOffset = 0.5f,
                            webcam = webcam,
                            imageLoader = vm.imageLoader,
                            canBeHD = vm.prefUtils.isWebcamsHighQuality,
                            startingAlpha = 1f,
                            aspectRatio = 8f,
                            goToWebcamDetail = {
                                goToWebcamDetail(webcam)
                            }
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .defaultMinSize(minHeight = 40.dp),
                            text = webcam.title ?: "",
                            color = AWAppTheme.colors.greyLight,
                            style = AWAppTheme.typography.p1,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
                AWTopBar(
                    section.title ?: "",
                    onNavigateBack,
                    onNavigateToMap,
                    icon = painterResource(id = R.drawable.map_icon_3),
                    iconDescription = stringResource(id = R.string.map_title),
                    modifier = Modifier.align(Alignment.TopCenter)
                )

            }
        }
    }
}
