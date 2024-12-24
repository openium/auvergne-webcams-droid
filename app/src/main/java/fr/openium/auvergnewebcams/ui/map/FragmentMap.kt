package fr.openium.auvergnewebcams.ui.map

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragmentMap
import fr.openium.auvergnewebcams.ui.map.components.MapScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import kotlinx.android.synthetic.main.fragment_search.composeView

class FragmentMap : AbstractFragmentMap() {

    override val layoutId: Int = R.layout.fragment_map

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelMap = ViewModelProvider(this)[MapViewModel::class.java]
        viewModelMap.switchMapStyle(prefUtils.mapStyle ?: "")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView.setContent {
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
    }
}