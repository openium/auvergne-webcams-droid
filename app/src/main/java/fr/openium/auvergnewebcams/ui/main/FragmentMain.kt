package fr.openium.auvergnewebcams.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragment
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.main.components.SectionsListScreen
import fr.openium.auvergnewebcams.ui.search.ActivitySearch
import fr.openium.auvergnewebcams.ui.sectionDetail.ActivitySectionDetail
import fr.openium.auvergnewebcams.ui.settings.ActivitySettings
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.kotlintools.ext.applicationContext
import fr.openium.kotlintools.ext.snackbar
import fr.openium.kotlintools.ext.startActivity
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_main.composeView
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class FragmentMain : AbstractFragment() {

    override val layoutId: Int = R.layout.fragment_main

    private lateinit var viewModelMain: ViewModelMain


    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        AnalyticsUtils.appIsOpen(requireContext())
        AnalyticsUtils.sendAllUserProperties(requireContext())

        viewModelMain = ViewModelProvider(this)[ViewModelMain::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView.setContent {
            AWTheme {
                val sectionsList by viewModelMain.sections.collectAsState(initial = emptyList())
                val refresh by viewModelMain.isRefreshing.observeAsState(false)
                val canBeHD = prefUtils.isWebcamsHighQuality
                SectionsListScreen(
                    sections = sectionsList,
                    isRefreshing = refresh,
                    refresh = {
                        AnalyticsUtils.homeRefreshed(requireContext())
                        refreshMethod()
                    },
                    canBeHD = canBeHD,
                    goToWebcamDetail = {
                        goToWebcamDetail(it)
                    },
                    goToSectionList = {
                        goToSectionList(it)
                    },
                    goToSearch = ::goToSearch
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_settings) {
            AnalyticsUtils.settingsClicked(requireContext())
            startActivity<ActivitySettings>()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    // --- Methods
    // ---------------------------------------------------

    private fun goToWebcamDetail(webcam: Webcam) {
        AnalyticsUtils.webcamDetailsClicked(requireContext(), webcam.title ?: "")
        requireContext().startActivity(ActivityWebcamDetail.getIntent(requireContext(), webcam))
    }

    private fun goToSectionList(section: Section) {
        AnalyticsUtils.webcamDetailsClicked(requireContext(), section.title ?: "")
        requireContext().startActivity(ActivitySectionDetail.getIntent(requireContext(), sectionId = section.uid))
    }

    private fun goToSearch() {
        requireContext().startActivity(Intent(applicationContext, ActivitySearch::class.java))
    }

    private fun refreshMethod() {
        // Get new data
        viewModelMain.setRefreshing(true)
        viewModelMain.updateData()
            .doFinally {
                viewModelMain.setRefreshing(false)
            }.subscribe({
                Timber.d("Sections refreshed correctly")
            }, {
                if (it is UnknownHostException || it is SocketTimeoutException) {
                    snackbar(R.string.generic_network_error, Snackbar.LENGTH_SHORT)
                } else snackbar(R.string.generic_error, Snackbar.LENGTH_SHORT)
                Timber.e(it, "Error when getting sections from PTR")
            }).addTo(disposables)
    }
}