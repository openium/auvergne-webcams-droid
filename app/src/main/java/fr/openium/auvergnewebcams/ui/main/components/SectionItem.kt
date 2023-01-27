package fr.openium.auvergnewebcams.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.utils.ImageUtils
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SectionItem(
    section: Section,
    goToWebcamDetail: (Webcam) -> Unit,
    goToSectionList: () -> Unit
) {
    val context = LocalContext.current

    val startIndex = Int.MAX_VALUE / 2
    val state = rememberPagerState(initialPage = startIndex)

    val image = ImageUtils.getImageResourceAssociatedToSection(context, section)

    var currentTitle by remember { mutableStateOf("") }
    LaunchedEffect(key1 = state.currentPage, block = {
        val page = (state.currentPage - startIndex).floorMod(section.webcams.size)
        currentTitle = section.webcams[page].title ?: ""
    })

    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(
            title = section.title ?: "",
            webcamsCount = section.webcams.size,
            image = image,
            goToSectionList = goToSectionList
        )
        HorizontalPager(
            count = Int.MAX_VALUE,
            state = state,
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 50.dp)
        ) { index ->
            val realIndex = (index - startIndex).floorMod(section.webcams.size)
            val webcam = section.webcams[realIndex]

            Box(modifier = Modifier
                .graphicsLayer {
                    val pageOffset = calculateCurrentOffsetForPage(index).absoluteValue
                    // We animate the scaleX + scaleY, between 85% and 100%
                    lerp(
                        start = 0.85f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale / 1.2f
                    }
                    // We animate the alpha, between 50% and 100%
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                }
                .fillMaxWidth()
                .aspectRatio(1f)
            ) {
                ItemWebcam(
                    webcam = webcam,
                    modifier = Modifier.align(Alignment.Center),
                    goToWebcamDetail = {
                        goToWebcamDetail(webcam)
                    }
                )
            }

        }
    }

    Text(
        text = currentTitle,
        color = AWAppTheme.colors.greyLight,
        style = AWAppTheme.typography.subtitle2,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .defaultMinSize(minHeight = 40.dp),
        textAlign = TextAlign.Center,
        maxLines = 2
    )
}


private fun Int.floorMod(other: Int): Int = when (other) {
    0 -> this
    else -> this - floorDiv(other) * other
}