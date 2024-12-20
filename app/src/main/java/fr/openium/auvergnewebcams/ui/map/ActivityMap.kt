package fr.openium.auvergnewebcams.ui.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivityFragment

class ActivityMap : AbstractActivityFragment() {

    override val showHomeAsUp: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }

    override fun getDefaultFragment(): Fragment =
        FragmentMap()
}