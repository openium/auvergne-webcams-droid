package fr.openium.auvergnewebcams.ui.about.components

import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import fr.openium.auvergnewebcams.R

@Composable
fun AboutScreen() {
    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                CenteredImages()
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Auvergne Webcams",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(
                                    view: WebView?,
                                    newProgress: Int
                                ) {
                                    if (newProgress == 100) isLoading = false
                                }
                            }
                            setBackgroundColor(android.graphics.Color.TRANSPARENT)
                            loadUrl("file:///android_asset/about.html")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            if (!isLoading) {

                Image(
                    painter = painterResource(id = R.drawable.logo_openium),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .width(150.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 25.dp)
                )
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun CenteredImages() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.categ_pdd_landscape),
            contentDescription = "Main Image",
            modifier = Modifier.size(130.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.categ_lioran_landscape),
            contentDescription = "Secondary Image 1",
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-20).dp, y = 20.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.categ_goal_landscape),
            contentDescription = "Secondary Image 2",
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.BottomStart)
                .offset(x = 10.dp, y = (-10).dp)
        )

        Image(
            painter = painterResource(id = R.drawable.categ_cf_landscape),
            contentDescription = "Secondary Image 3",
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.TopStart)
                .offset(x = 50.dp, y = (-5).dp)
        )
    }
}
