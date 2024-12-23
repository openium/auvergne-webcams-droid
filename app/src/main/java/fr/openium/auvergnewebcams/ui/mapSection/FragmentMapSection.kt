package fr.openium.auvergnewebcams.ui.mapSection

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import fr.openium.auvergnewebcams.KEY_SECTION_ID
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragment
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.map.components.MapScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.kotlintools.ext.setTitle
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_search.composeView
import timber.log.Timber

class FragmentMapSection : AbstractFragment() {

    override val layoutId: Int = R.layout.fragment_map_section

    private lateinit var viewModelMapSection: MapSectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelMapSection = ViewModelProvider(this)[MapSectionViewModel::class.java]
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
        viewModelMapSection.getSectionWithCameras(sectionId)
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
                            MapScreen(
                                sections = listOf(sectionWithCameras),
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

    private fun goToWebcamDetail(webcam: Webcam) {
        AnalyticsUtils.webcamDetailsClicked(requireContext(), webcam.title ?: "")
        requireContext().startActivity(ActivityWebcamDetail.getIntent(requireContext(), webcam))
    }
}