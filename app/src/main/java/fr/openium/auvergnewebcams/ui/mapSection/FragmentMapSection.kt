package fr.openium.auvergnewebcams.ui.mapSection

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import fr.openium.auvergnewebcams.KEY_SECTION_ID
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragmentMap
import fr.openium.auvergnewebcams.ui.map.components.MapScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.kotlintools.ext.setTitle
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_search.composeView
import timber.log.Timber

class FragmentMapSection : AbstractFragmentMap() {

    override val layoutId: Int = R.layout.fragment_map_section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelMap = ViewModelProvider(this)[MapSectionViewModel::class.java]
        viewModelMap.switchMapStyle(prefUtils.mapStyle ?: "")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sectionId = arguments?.getLong(KEY_SECTION_ID, -1L) ?: -1L

        if (sectionId != -1L) {
            composeMapScreen(sectionId)
        } else {
            requireActivity().finish()
        }
    }

    // --- Methods
    // ---------------------------------------------------

    private fun composeMapScreen(sectionId: Long) {
        viewModelMap.getSectionWithCameras(sectionId)
            .fromIOToMain()
            .subscribe({ item ->
                item.value?.let { sectionWithCameras ->

                    setTitle(
                        getString(
                            R.string.map_title_with_name,
                            sectionWithCameras.section.title
                        )
                    )

                    composeView.setContent {
                        AWTheme {
                            val mapStyle by viewModelMap.mapStyle.collectAsState()
                            val canBeHD = prefUtils.isWebcamsHighQuality

                            MapScreen(
                                sections = listOf(sectionWithCameras),
                                canBeHD = canBeHD,
                                mapStyle = mapStyle,
                                goToWebcamDetail = { webcam ->
                                    goToWebcamDetail(webcam)
                                },
                            )
                        }
                    }
                }
            }, {
                Timber.e(it, "Error when getting section")
                activity?.finish()
            }).addTo(disposables)
    }
}