package fr.openium.auvergnewebcams.activity

import android.support.v4.app.Fragment
import fr.openium.auvergnewebcams.fragment.FragmentSearch

/**
 * Created by laura on 05/12/2017.
 */
class ActivitySearch: AbstractActivityFragment() {

    override fun getDefaultFragment(): Fragment? {
        return FragmentSearch()
    }

    override val showHomeAsUp: Boolean
        get() = true
}