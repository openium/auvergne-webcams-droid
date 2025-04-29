package fr.openium.auvergnewebcams.ui.map

import android.os.Bundle
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.base.AbstractMapViewModel
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.map.components.MapScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils

class ActivityMap : AbstractActivity() {

    override val layoutId: Int = R.layout.fragment_map
    protected lateinit var viewModelMap: AbstractMapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelMap = ViewModelProvider(this)[MapViewModel::class.java]

        findViewById<ComposeView>(R.id.composeView).setContent {
            val sections by viewModelMap.sections.collectAsState(initial = emptyList())
            AWTheme {
                MapScreen(
                    sections = sections,
                    onNavigateBack = { finish() },
                    goToWebcamDetail = {
                        goToWebcamDetail(it)
                    },
                )
            }
        }

        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
    }

    protected fun goToWebcamDetail(webcam: Webcam) {
        AnalyticsUtils.webcamDetailsClicked(this, webcam.title ?: "")
        startActivity(ActivityWebcamDetail.getIntent(this, webcam))
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }

}