package fr.openium.auvergnewebcams.ui.splash

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProviders
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.ui.main.ActivityMain
import fr.openium.kotlintools.ext.startActivity
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_splash.*
import timber.log.Timber


class ActivitySplash : AbstractActivity() {

    override val layoutId: Int
        get() = R.layout.activity_splash

    private lateinit var viewModelSplash: ViewModelSplash

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelSplash = ViewModelProviders.of(this).get(ViewModelSplash::class.java)

        // Get new data
        viewModelSplash.updateData().subscribe({
            startActivityMain()
        }, { Timber.e(it) }).addTo(disposables)
    }

    override fun onResume() {
        super.onResume()
        val animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha)
        mCloud1.startAnimation(animation)
        mCloud2.startAnimation(animation)
        mCloud3.startAnimation(animation)
        mCloud4.startAnimation(animation)
        mCloud5.startAnimation(animation)
        mCloud6.startAnimation(animation)
        mCloud7.startAnimation(animation)
    }

    // --- Methods
    // ---------------------------------------------------

    private fun startActivityMain() {
        startActivity<ActivityMain>()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}