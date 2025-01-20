package fr.openium.auvergnewebcams.ui.mapSection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import fr.openium.auvergnewebcams.KEY_SECTION_ID
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivityFragment

class ActivityMapSection : AbstractActivityFragment() {

    override val showHomeAsUp: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }

    override fun getDefaultFragment(): Fragment = FragmentMapSection()

    // --- Other methods
    // ---------------------------------------------------

    companion object {

        fun getIntent(context: Context, sectionId: Long): Intent =
            Intent(context, ActivityMapSection::class.java).apply {
                putExtra(KEY_SECTION_ID, sectionId)
            }
    }
}