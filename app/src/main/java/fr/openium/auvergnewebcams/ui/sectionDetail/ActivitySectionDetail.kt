package fr.openium.auvergnewebcams.ui.sectionDetail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.ui.res.colorResource
import fr.openium.auvergnewebcams.KEY_SECTION_ID
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.auvergnewebcams.ui.webcamDetail.ActivityWebcamDetail
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import kotlinx.android.synthetic.main.activity_section_detail.composeView

/**
 * Created by Openium on 19/02/2019.
 */
class ActivitySectionDetail : AbstractActivity() {

    override val layoutId: Int = R.layout.activity_section_detail

    override val showHomeAsUp: Boolean = true

    // override fun getDefaultFragment(): Fragment? = FragmentSectionDetail()

    // --- Life cycle
    // ---------------------------------------------------

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sectionId = intent.getLongExtra(KEY_SECTION_ID, -1L)
        if (sectionId == -1L) {
            finish()
            return
        }

        composeView.setContent {
            AWTheme {
                Scaffold(
                    backgroundColor = colorResource(id = R.color.grey_dark),
                    topBar = {
                        TopAppBar(
                            title = { Text("Toolbar") },
                            backgroundColor = colorResource(id = R.color.grey_very_dark)
                        )
                    }
                ) {
                    SectionDetailScreen(
                        sectionId = sectionId,
                        goToWebcamDetail = {
                            AnalyticsUtils.webcamDetailsClicked(this, it.title ?: "")
                            startActivity(ActivityWebcamDetail.getIntent(this, it))
                        },
                    )
                }
            }
        }

        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }

    // --- Other methods
    // ---------------------------------------------------

    companion object {

        fun getIntent(context: Context, sectionId: Long): Intent =
            Intent(context, ActivitySectionDetail::class.java).apply {
                putExtra(KEY_SECTION_ID, sectionId)
            }
    }
}