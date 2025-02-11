package fr.openium.auvergnewebcams.ui.splash.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme

@Composable
fun SplashText(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.splash_loading_cameras),
            style = AWAppTheme.typography.splashText,
            modifier = Modifier
                .padding(10.dp),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))

        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            color = Color.White
        )
    }

}