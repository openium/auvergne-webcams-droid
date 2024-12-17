package fr.openium.auvergnewebcams.ui.sectionDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import fr.openium.auvergnewebcams.KEY_SECTION_ID
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivityFragment
import fr.openium.auvergnewebcams.databinding.ContainerToolbarBinding

/**
 * Created by Openium on 19/02/2019.
 */
class ActivitySectionDetail : AbstractActivityFragment<ContainerToolbarBinding>() {

    override val layoutId: Int = R.layout.container_toolbar

    override val showHomeAsUp: Boolean = true

    override fun provideViewBinding(): ContainerToolbarBinding =
        ContainerToolbarBinding.inflate(layoutInflater)

    override fun getDefaultFragment(): Fragment? =
        FragmentSectionDetail()

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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