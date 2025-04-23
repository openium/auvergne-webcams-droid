package fr.openium.auvergnewebcams.ui.mapSection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import fr.openium.auvergnewebcams.KEY_SECTION_ID
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.base.AbstractMapViewModel
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.ui.map.components.MapScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.rxkotlin.addTo
import timber.log.Timber

class ActivityMapSection : AbstractActivity() {


    override val layoutId: Int = R.layout.fragment_map_section
    protected lateinit var viewModelMap: AbstractMapViewModel

    override val showHomeAsUp: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)

        viewModelMap = ViewModelProvider(this)[MapSectionViewModel::class.java]
        viewModelMap.switchMapStyle(prefUtils.mapStyle ?: "")
        val sectionId = intent.getLongExtra(KEY_SECTION_ID, -1L)
        if (sectionId == -1L) {
            finish()
            return
        }

        composeMapScreen(sectionId)

    }

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

                    findViewById<ComposeView>(R.id.composeView).setContent {
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
                this.finish()
            }).addTo(disposables)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_map, menu)
        return true
    }

    //fun getDefaultFragment(): Fragment = FragmentMapSection()

    // --- Other methods
    // ---------------------------------------------------

    companion object {

        fun getIntent(context: Context, sectionId: Long): Intent =
            Intent(context, ActivityMapSection::class.java).apply {
                putExtra(KEY_SECTION_ID, sectionId)
            }
    }

    protected fun goToWebcamDetail(webcam: Webcam) {
        AnalyticsUtils.webcamDetailsClicked(this, webcam.title ?: "")
        startActivity(ActivityWebcamDetail.getIntent(this, webcam))
    }
}