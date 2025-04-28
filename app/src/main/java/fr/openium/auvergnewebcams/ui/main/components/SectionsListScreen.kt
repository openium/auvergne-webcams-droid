package fr.openium.auvergnewebcams.ui.main.components

import android.content.Context
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.core.AWTopBar
import fr.openium.auvergnewebcams.ui.main.ViewModelMain
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

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

    val disposables: CompositeDisposable = CompositeDisposable()

    val sections by vm.sections.collectAsState(initial = emptyList())
    val isRefreshing by vm.isRefreshing.observeAsState(false)
    val canBeHD = vm.prefUtils.isWebcamsHighQuality

    val context = LocalContext.current

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing, onRefresh = {
            AnalyticsUtils.homeRefreshed(context = context)
            refreshMethod(vm, context, disposables)
        }
    )

    Scaffold(
        backgroundColor = AWAppTheme.colors.greyVeryDark,
        topBar =
        {
            AWTopBar(
                title = stringResource(R.string.app_name),
                onNavigateBack = {},
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
            Box(
                modifier = Modifier
                    .pullRefresh(pullRefreshState)
                    .padding(paddingValues)
                    .navigationBarsPadding()
                    .background(color = AWAppTheme.colors.greyMedium)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
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
                                AnalyticsUtils.webcamDetailsClicked(context, section.section.title ?: "")
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
    )
}

private fun refreshMethod(vm: ViewModelMain, context: Context, disposables: CompositeDisposable) {
    AnalyticsUtils.homeRefreshed(context)

    vm.setRefreshing(true)
    vm.updateData()
        .doFinally {
            vm.setRefreshing(false)
        }.subscribe({
            Timber.d("Sections refreshed correctly")
        }, {

        }).addTo(disposables)
}

