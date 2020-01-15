package fr.openium.auvergnewebcams.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import fr.openium.auvergnewebcams.R

/**
 * Created by Openium on 19/02/2019.
 */

abstract class AbstractActivityFragment : AbstractActivity() {
    protected var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addDefaultFragment()
    }

    protected open fun addDefaultFragment() {
        onPreAddFragment()
        val fragmentManager = supportFragmentManager
        fragment = fragmentManager.findFragmentById(containerId)
        if (fragment == null) {
            fragment = getDefaultFragment()
            fragment?.arguments = intent?.extras
            fragmentManager.beginTransaction().replace(containerId, fragment!!).commit()
        }
    }

    protected open fun onPreAddFragment() {

    }

    protected abstract fun getDefaultFragment(): Fragment?

    // Retourne l'id de la vue qui contient le fragment
    protected open val containerId: Int = R.id.container_framelayout
}
