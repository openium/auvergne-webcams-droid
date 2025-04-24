package fr.openium.auvergnewebcams.ui.search

import android.os.Bundle
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.search.components.SearchScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import org.koin.android.ext.android.inject

class ActivitySearch : AbstractActivity() {


    override val layoutId: Int = R.layout.fragment_search
    private lateinit var viewModelSearch: SearchViewModel

    private val imageLoader by inject<ImageLoader>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelSearch = ViewModelProvider(this)[SearchViewModel::class.java]
        setTitle(getString(R.string.search_title))
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
        findViewById<ComposeView>(R.id.composeView).setContent {
            AWTheme {
                val webcams by viewModelSearch.webcams.collectAsState()
                SearchScreen(
                    onNewSearch = {
                        viewModelSearch.onNewSearch(it)
                    },
                    canBeHD = prefUtils.isWebcamsHighQuality,
                    imageLoader = imageLoader,
                    webcams = webcams,
                    onNavigateBack = { finish() },
                    goToWebcamDetail = {
                        goToWebcamDetail(it)
                    }
                )
            }
        }
    }

    private fun goToWebcamDetail(webcam: Webcam) {
        AnalyticsUtils.webcamDetailsClicked(this, webcam.title ?: "")
        startActivity(ActivityWebcamDetail.getIntent(this, webcam))
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }

}