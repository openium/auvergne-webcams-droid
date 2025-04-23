package fr.openium.auvergnewebcams.base

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.enums.MapStyle
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils

abstract class AbstractFragmentMap : AbstractFragment() {

    protected lateinit var viewModelMap: AbstractMapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_map, menu)

        super.onCreateOptionsMenu(menu, inflater)
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

    protected fun goToWebcamDetail(webcam: Webcam) {
        AnalyticsUtils.webcamDetailsClicked(requireContext(), webcam.title ?: "")
        requireContext().startActivity(ActivityWebcamDetail.getIntent(requireContext(), webcam))
    }
}