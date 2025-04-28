package fr.openium.auvergnewebcams.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.main.components.SectionsListScreen
import fr.openium.auvergnewebcams.ui.map.ActivityMap
import fr.openium.auvergnewebcams.ui.search.ActivitySearch
import fr.openium.auvergnewebcams.ui.sectionDetail.ActivitySectionDetail
import fr.openium.auvergnewebcams.ui.settings.ActivitySettings
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.kotlintools.ext.snackbar
import fr.openium.kotlintools.ext.startActivity
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ActivityMain : AbstractActivity() {


    override val layoutId: Int = R.layout.fragment_main

    private lateinit var viewModelMain: ViewModelMain


    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        supportActionBar?.title = getString(R.string.app_name)
        AnalyticsUtils.appIsOpen(this)
        AnalyticsUtils.sendAllUserProperties(this)

        viewModelMain = ViewModelProvider(this)[ViewModelMain::class.java]

        findViewById<ComposeView>(R.id.composeView).setContent {
            AWTheme {
                val sectionsList by viewModelMain.sections.collectAsState(initial = emptyList())
                val refresh by viewModelMain.isRefreshing.observeAsState(false)
                val canBeHD = prefUtils.isWebcamsHighQuality
                SectionsListScreen(
                    goToWebcamDetail = {
                        goToWebcamDetail(it)
                    },
                    goToSectionList = {
                        goToSectionList(it)
                    },
                    goToSearch = ::goToSearch,
                    goToMap = {
                        startActivity<ActivityMap>()
                    },
                    goToSettings = {
                        AnalyticsUtils.settingsClicked(this)
                        startActivity<ActivitySettings>()
                    },
                )
            }
        }
    }


    // --- Methods
    // ---------------------------------------------------

    private fun goToWebcamDetail(webcam: Webcam) {
        AnalyticsUtils.webcamDetailsClicked(this, webcam.title ?: "")
        startActivity(ActivityWebcamDetail.getIntent(this, webcam))
    }

    private fun goToSectionList(section: Section) {
        AnalyticsUtils.webcamDetailsClicked(this, section.title ?: "")
        startActivity(
            ActivitySectionDetail.getIntent(
                this,
                sectionId = section.uid
            )
        )
    }

    private fun goToSearch() {
        startActivity(Intent(applicationContext, ActivitySearch::class.java))
    }

    private fun refreshMethod() {
        // Get new data
        AnalyticsUtils.homeRefreshed(this)

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