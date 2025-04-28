package fr.openium.auvergnewebcams.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.core.AWTopBar
import fr.openium.auvergnewebcams.ui.search.SearchViewModel
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    vm: SearchViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    goToWebcamDetail: (Webcam) -> Unit
) {
    val context = LocalContext.current

    var currentSearch by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    val webcams by vm.webcams.collectAsState()

    val canBeHD = vm.prefUtils.isWebcamsHighQuality

    LaunchedEffect(Unit) {
        delay(400L) // delay to show keyboard
        focusRequester.requestFocus()
    }


    Scaffold(
        backgroundColor = AWAppTheme.colors.greyVeryDark,
        topBar =
        {
            AWTopBar(
                title = stringResource(R.string.search_title),
                onNavigateBack = onNavigateBack,
                onNavigateTo = { },
                onNavigateToOpt = { },
                isPrimaryButton = false,
                isOptionalButton = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .navigationBarsPadding()
                    .background(color = AWAppTheme.colors.greyMedium)
            ) {
                SearchItem(
                    currentSearch = currentSearch,
                    onSearchChange = {
                        currentSearch = it
                        vm.onNewSearch(it)
                    },
                    clearSearch = {
                        currentSearch = ""
                        vm.onNewSearch("")
                    },
                    focusRequester = focusRequester
                )
                if (currentSearch.isNotBlank()) {
                    val searchNumberText = if (webcams.isNotEmpty()) {
                        pluralStringResource(id = R.plurals.search_result_format, webcams.size, webcams.size)
                    } else stringResource(id = R.string.search_result_none_format)

                    val index = searchNumberText.length + 1

                    val infoForSearchText = buildAnnotatedString {
                        append(searchNumberText)
                        append(" ")
                        append(currentSearch)
                        addStyle(
                            SpanStyle(color = AWAppTheme.colors.blue, fontStyle = FontStyle.Italic),
                            index,
                            index + currentSearch.length
                        )
                    }

                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 24.dp)
                            .fillMaxWidth(),
                        text = infoForSearchText,
                        textAlign = TextAlign.Center,
                        color = AWAppTheme.colors.white,
                        style = AWAppTheme.typography.p1
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(webcams) { webcam ->
                        WebcamItem(
                            webcam = webcam,
                            canBeHD = canBeHD,
                            imageLoader = vm.imageLoader,
                            goToWebcamDetail = {
                                AnalyticsUtils.webcamDetailsClicked(context, webcam.title ?: "")
                                goToWebcamDetail(webcam)
                            }
                        )
                    }
                }
            }
        }
    )
}
