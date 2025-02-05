package fr.openium.auvergnewebcams.ui.splash.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.openium.auvergnewebcams.R

@Composable
fun SplashText() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.splash_loading_cameras),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .padding(10.dp),
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))

        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            color = Color.White
        )
    }

}