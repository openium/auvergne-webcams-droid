package fr.openium.auvergnewebcams.ui.splash

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragment
import fr.openium.auvergnewebcams.ui.main.ActivityMain
import fr.openium.auvergnewebcams.ui.splash.components.SplashScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.kotlintools.ext.startActivity
import io.reactivex.Completable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_search.composeView
import timber.log.Timber

class FragmentSplash : AbstractFragment() {

    override val layoutId: Int = R.layout.fragment_splash

    private lateinit var viewModelSplash: ViewModelSplash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelSplash = ViewModelProvider(this).get(ViewModelSplash::class.java)

        Completable.merge(
            listOf(viewModelSplash.updateData())
        ).subscribe({
            startActivityMain()
        }, { Timber.e(it) }).addTo(disposables)
    }

    private fun startActivityMain() {
        startActivity<ActivityMain>()
        requireActivity().finish()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView.setContent {
            AWTheme {
                SplashScreen()
            }
        }

    }
}