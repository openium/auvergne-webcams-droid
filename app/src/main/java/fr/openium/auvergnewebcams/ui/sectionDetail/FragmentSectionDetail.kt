package fr.openium.auvergnewebcams.ui.sectionDetail

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import fr.openium.auvergnewebcams.KEY_SECTION_ID
import fr.openium.auvergnewebcams.base.AbstractFragment
import fr.openium.auvergnewebcams.databinding.ComposeViewBinding
import fr.openium.auvergnewebcams.ui.sectionDetail.components.SectionDetailScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.kotlintools.ext.setTitle
import org.koin.android.ext.android.inject


/**
 * Created by Openium on 19/02/2019.
 */
class FragmentSectionDetail : AbstractFragment<ComposeViewBinding>() {

    private lateinit var viewModelSectionDetail: ViewModelSectionDetail

    private val imageLoader by inject<ImageLoader>()

    override fun provideViewBinding(): ComposeViewBinding =
        ComposeViewBinding.inflate(layoutInflater)

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelSectionDetail = ViewModelProvider(this)[ViewModelSectionDetail::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sectionId = arguments?.getLong(KEY_SECTION_ID, -1L) ?: -1L

        if (sectionId != -1L) {
            viewModelSectionDetail.setSectionId(sectionId)

            binding.composeView.setContent {
                AWTheme {
                    val section by viewModelSectionDetail.sectionAndWebcams.collectAsState()
                    setTitle(section?.section?.title ?: "")

                    SectionDetailScreen(
                        section = section,
                        imageLoader = imageLoader,
                        canBeHD = prefUtils.isWebcamsHighQuality,
                        goToWebcamDetail = {
                            AnalyticsUtils.webcamDetailsClicked(requireContext(), it.title ?: "")
                            startActivity(ActivityWebcamDetail.getIntent(requireContext(), it))
                        }
                    )
                }
            }
        } else requireActivity().finish()
    }
}