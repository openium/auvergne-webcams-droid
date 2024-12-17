package fr.openium.auvergnewebcams.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivityFragment
import fr.openium.auvergnewebcams.databinding.ContainerToolbarBinding

class ActivitySearch : AbstractActivityFragment<ContainerToolbarBinding>() {

    override val showHomeAsUp: Boolean = true

    override fun provideViewBinding(): ContainerToolbarBinding =
        ContainerToolbarBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }

    override fun getDefaultFragment(): Fragment =
        FragmentSearch()
}