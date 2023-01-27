package fr.openium.auvergnewebcams.ui.main.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SectionsList(
    sections: List<Section>,
    isRefreshing: Boolean,
    refresh: () -> Unit,
    goToWebcamDetail: (Webcam) -> Unit,
    goToSectionList: (Section) -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(isRefreshing, { refresh() })

    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 16.dp)) {
            items(sections) { section ->
                SectionItem(
                    section = section,
                    goToWebcamDetail = goToWebcamDetail,
                    goToSectionList = {
                        goToSectionList(section)
                    }
                )
            }
        }
        PullRefreshIndicator(isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }

}


