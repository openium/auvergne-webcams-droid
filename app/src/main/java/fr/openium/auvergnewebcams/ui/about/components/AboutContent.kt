package fr.openium.auvergnewebcams.ui.about.components

import android.graphics.Color
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.AWTheme

@Composable
fun AboutContent(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.categ_pdd_landscape),
                contentDescription = null,
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.Center)
            )

            Image(
                painter = painterResource(id = R.drawable.categ_lioran_landscape),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 24.dp)
                    .size(70.dp)
                    .align(Alignment.CenterEnd)
            )

            Image(
                painter = painterResource(id = R.drawable.categ_goal_landscape),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 24.dp, bottom = 16.dp)
                    .size(60.dp)
                    .align(Alignment.BottomStart)
            )

            Image(
                painter = painterResource(id = R.drawable.categ_cf_landscape),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 70.dp, top = 24.dp)
                    .size(50.dp)
                    .align(Alignment.TopStart)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.settings_about_title),
            color = AWAppTheme.colors.white,
            style = AWAppTheme.typography.h2,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        AndroidView(factory = {
            val webViewAbout = WebView(it)
            webViewAbout.setBackgroundColor(Color.TRANSPARENT)
            webViewAbout.loadUrl("file:///android_asset/about.html")
            webViewAbout
        }, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Image(
            painter = painterResource(id = R.drawable.logo_openium),
            contentDescription = null,
            modifier = Modifier.width(150.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
@Preview
fun AboutScreenPreview() {
    AWTheme {
        AboutContent(paddingValues = PaddingValues())
    }
}