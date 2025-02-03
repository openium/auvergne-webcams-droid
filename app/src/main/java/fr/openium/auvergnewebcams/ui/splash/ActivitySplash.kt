package fr.openium.auvergnewebcams.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import fr.openium.auvergnewebcams.base.AbstractActivityFragment

class ActivitySplash : AbstractActivityFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getDefaultFragment(): Fragment = FragmentSplash()
}