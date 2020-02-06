package fr.openium.auvergnewebcams.ui.search

import androidx.fragment.app.Fragment
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivityFragment

/**
 * Created by Openium on 19/02/2019.
 */
class ActivitySearch : AbstractActivityFragment() {

    override val layoutId: Int = R.layout.container_toolbar

    override val showHomeAsUp: Boolean = true

    override fun getDefaultFragment(): Fragment? = FragmentSearch()

    // --- Life cycle
    // ---------------------------------------------------


}