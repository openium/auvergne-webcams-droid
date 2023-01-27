package fr.openium.auvergnewebcams.ui.search

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragment
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.search.components.SearchScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.kotlintools.ext.setTitle
import kotlinx.android.synthetic.main.fragment_search.composeView
import org.koin.android.ext.android.inject


/**
 * Created by Openium on 19/02/2019.
 */
class FragmentSearch : AbstractFragment() {

    override val layoutId: Int = R.layout.fragment_search

    private lateinit var viewModelSearch: SearchViewModel

    private val imageLoader by inject<ImageLoader>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelSearch = ViewModelProvider(this)[SearchViewModel::class.java]
        setTitle(getString(R.string.search_title))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            AWTheme {
                val webcams by viewModelSearch.webcams.collectAsState()
                SearchScreen(
                    onNewSearch = {
                        viewModelSearch.onNewSearch(it)
                    },
                    canBeHD = prefUtils.isWebcamsHighQuality,
                    imageLoader = imageLoader,
                    webcams = webcams,
                    goToWebcamDetail = {
                        goToWebcamDetail(it)
                    }
                )
            }
        }
    }

    private fun goToWebcamDetail(webcam: Webcam) {
        AnalyticsUtils.webcamDetailsClicked(requireContext(), webcam.title ?: "")
        requireContext().startActivity(ActivityWebcamDetail.getIntent(requireContext(), webcam))
    }
}