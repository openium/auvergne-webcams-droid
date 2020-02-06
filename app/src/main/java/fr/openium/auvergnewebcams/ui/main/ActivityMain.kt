package fr.openium.auvergnewebcams.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivityFragment

class ActivityMain : AbstractActivityFragment() {

    override val layoutId: Int = R.layout.container_toolbar

    override fun getDefaultFragment(): Fragment? = FragmentMain()

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        supportActionBar?.title = getString(R.string.app_name)
    }
}