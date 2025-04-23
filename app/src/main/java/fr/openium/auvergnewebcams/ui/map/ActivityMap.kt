package fr.openium.auvergnewebcams.ui.map

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.base.AbstractMapViewModel
import fr.openium.auvergnewebcams.enums.MapStyle
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.map.components.MapScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils

class ActivityMap : AbstractActivity() {

    override val layoutId: Int = R.layout.fragment_map
    protected lateinit var viewModelMap: AbstractMapViewModel

    override val showHomeAsUp: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelMap = ViewModelProvider(this)[MapViewModel::class.java]
        viewModelMap.switchMapStyle(prefUtils.mapStyle ?: "")

        findViewById<ComposeView>(R.id.composeView).setContent {
            AWTheme {
                val sections by viewModelMap.sections.collectAsState(initial = emptyList())
                val mapStyle by viewModelMap.mapStyle.collectAsState()
                val canBeHD = prefUtils.isWebcamsHighQuality

                MapScreen(
                    sections = sections,
                    canBeHD = canBeHD,
                    mapStyle = mapStyle,
                    goToWebcamDetail = {
                        goToWebcamDetail(it)
                    },
                )
            }
        }

        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_map_style_outside -> {
                prefUtils.mapStyle = MapStyle.OUTSIDE.style
                viewModelMap.switchMapStyle(MapStyle.OUTSIDE)
                true
            }

            R.id.menu_map_style_dark -> {
                prefUtils.mapStyle = MapStyle.DARK.style
                viewModelMap.switchMapStyle(MapStyle.DARK)
                true
            }

            R.id.menu_map_style_light -> {
                prefUtils.mapStyle = MapStyle.LIGHT.style
                viewModelMap.switchMapStyle(MapStyle.LIGHT)
                true
            }

            R.id.menu_map_style_satellite -> {
                prefUtils.mapStyle = MapStyle.SATELLITE.style
                viewModelMap.switchMapStyle(MapStyle.SATELLITE)
                true
            }

            R.id.menu_map_style_roads -> {
                prefUtils.mapStyle = MapStyle.ROADS.style
                viewModelMap.switchMapStyle(MapStyle.ROADS)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_map, menu)
        return true
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