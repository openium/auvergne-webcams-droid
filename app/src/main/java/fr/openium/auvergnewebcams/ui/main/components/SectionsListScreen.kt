package fr.openium.auvergnewebcams.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SectionsListScreen(
    sections: List<SectionWithCameras>,
    isRefreshing: Boolean,
    refresh: () -> Unit,
    canBeHD: Boolean,
    imageLoader: ImageLoader,
    goToWebcamDetail: (Webcam) -> Unit,
    goToSectionList: (Section) -> Unit,
    goToSearch: () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = refresh)

    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp, top = 16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = AWAppTheme.colors.grey)
                        .clickable(onClick = goToSearch)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = stringResource(id = R.string.search_hint),
                        tint = AWAppTheme.colors.white
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(id = R.string.search_hint),
                        color = AWAppTheme.colors.greyLight,
                        style = AWAppTheme.typography.p1Italic
                    )
                }
            }
            items(sections) { section ->
                SectionItem(
                    section = section,
                    canBeHD = canBeHD,
                    imageLoader = imageLoader,
                    goToWebcamDetail = goToWebcamDetail,
                    goToSectionList = {
                        goToSectionList(section.section)
                    }
                )
            }
        }
        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = isRefreshing,
            state = pullRefreshState
        )
    }
}


