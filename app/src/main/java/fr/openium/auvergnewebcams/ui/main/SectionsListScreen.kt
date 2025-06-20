package fr.openium.auvergnewebcams.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.core.AWTopBar
import fr.openium.auvergnewebcams.ui.main.components.SectionItem
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SectionsListScreen(
    vm: ViewModelMain = koinViewModel(),
    goToWebcamDetail: (Webcam) -> Unit,
    goToSectionList: (Section) -> Unit,
    goToSearch: () -> Unit,
    goToMap: () -> Unit,
    goToSettings: () -> Unit
) {
    val sectionState by vm.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Scaffold(
        backgroundColor = AWAppTheme.colors.greyVeryDark,
        topBar =
        {
            AWTopBar(
                title = stringResource(R.string.app_name),
                onNavigateTo = {
                    AnalyticsUtils.settingsClicked(context)
                    goToSettings()
                },
                onNavigateToOpt = goToMap,
                icon = painterResource(id = R.drawable.ic_settings),
                iconDescription = stringResource(id = R.string.settings_title),
                iconOpt = painterResource(id = R.drawable.map_icon_3),
                iconDescriptionOpt = stringResource(id = R.string.map_title),
                isPrimaryButton = true,
                isOptionalButton = true,
                isNavBack = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        },
        content = { paddingValues ->

            when (val state = sectionState) {

                ViewModelMain.State.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(64.dp),
                            color       = AWAppTheme.colors.white,
                            strokeWidth = 6.dp
                        )
                    }                }

                is ViewModelMain.State.Loaded -> {
                    val sections = state.sections
                    val isRefreshing = state.isRefreshing
                    val canBeHD = state.canBeHD


                    val pullRefreshState = rememberPullRefreshState(
                        refreshing = isRefreshing, onRefresh = {
                            AnalyticsUtils.homeRefreshed(context = context)
                            vm.updateData()
                        })
                    Box(
                        modifier = Modifier
                            .pullRefresh(pullRefreshState)
                            .padding(paddingValues)
                            .navigationBarsPadding()
                            .background(color = AWAppTheme.colors.greyMedium)
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 16.dp),
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = AWAppTheme.colors.grey)
                                        .clickable(onClick = goToSearch)
                                        .padding(16.dp)
                                        .padding(paddingValues)
                                        .navigationBarsPadding(),
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
                                    imageLoader = vm.imageLoader,
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

            }

        }

    )
}


