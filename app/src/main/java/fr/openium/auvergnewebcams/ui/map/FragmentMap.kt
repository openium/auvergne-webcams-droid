package fr.openium.auvergnewebcams.ui.map

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragment
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.map.components.MapScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import kotlinx.android.synthetic.main.fragment_search.composeView

class FragmentMap : AbstractFragment() {

    override val layoutId: Int = R.layout.fragment_map

    private lateinit var viewModelMap: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelMap = ViewModelProvider(this)[MapViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView.setContent {
            AWTheme {
                val sectionsList by viewModelMap.sections.collectAsState(initial = emptyList())

                MapScreen(
                    sections = sectionsList,
                    goToWebcamDetail = {
                        goToWebcamDetail(it)
                    },
                )
            }
        }
    }

    private fun goToWebcamDetail(webcam: Webcam) {
        AnalyticsUtils.webcamDetailsClicked(requireContext(), webcam.title ?: "")
        requireContext().startActivity(ActivityWebcamDetail.getIntent(requireContext(), webcam))
    }
}