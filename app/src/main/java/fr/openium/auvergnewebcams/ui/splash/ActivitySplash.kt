package fr.openium.auvergnewebcams.ui.splash

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.ui.main.ActivityMain
import fr.openium.auvergnewebcams.ui.splash.components.SplashScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.kotlintools.ext.startActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.compose_view.*
import timber.log.Timber


class ActivitySplash : AbstractActivity() {

    override val layoutId: Int = R.layout.compose_view

    private lateinit var viewModelSplash: ViewModelSplash

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelSplash = ViewModelProvider(this).get(ViewModelSplash::class.java)

        composeView.setContent {
            AWTheme {
                SplashScreen()
            }
        }
        viewModelSplash.updateData()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                startActivityMain()
            }, {
                Timber.e(it)
            })
            .addTo(disposables)
    }

    // --- Methods
    // ---------------------------------------------------

    private fun startActivityMain() {
        startActivity<ActivityMain>()
        finish()
    }
}