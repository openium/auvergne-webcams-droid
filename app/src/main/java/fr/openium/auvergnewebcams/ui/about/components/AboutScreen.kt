package fr.openium.auvergnewebcams.ui.about.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.core.AWTopAppBar
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme

@Composable
fun AboutScreen(
    navigateBack: () -> Unit
) {

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        modifier = Modifier
            .fillMaxWidth(),
        scaffoldState = scaffoldState,
        topBar = {
            AWTopAppBar(
                title = stringResource(id = R.string.settings_credits_about),
                onClickHomeButton = navigateBack
            )
        },
        backgroundColor = AWAppTheme.colors.greyDark,
    ) {
        AboutContent(paddingValues = it)
    }
}

