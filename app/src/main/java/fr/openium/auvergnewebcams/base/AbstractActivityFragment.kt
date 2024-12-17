package fr.openium.auvergnewebcams.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import fr.openium.auvergnewebcams.R

/**
 * Created by Openium on 19/02/2019.
 */

abstract class AbstractActivityFragment<T : ViewBinding> : AbstractActivity<T>() {

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
        // Optional hook for subclasses to implement additional behavior before adding the fragment
    }

    protected abstract fun getDefaultFragment(): Fragment?

    // Returns the ID of the view containing the fragment
    protected open val containerId: Int
        get() = R.id.container_framelayout
}