package fr.openium.auvergnewebcams.ui.splash

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.databinding.ComposeViewBinding
import fr.openium.auvergnewebcams.ui.main.ActivityMain
import fr.openium.auvergnewebcams.ui.splash.components.SplashScreen
import fr.openium.auvergnewebcams.ui.theme.AWTheme
import fr.openium.kotlintools.ext.startActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import timber.log.Timber


class ActivitySplash : AbstractActivity<ComposeViewBinding>() {

    override val layoutId: Int = R.layout.compose_view

    private lateinit var viewModelSplash: ViewModelSplash

    override fun provideViewBinding(): ComposeViewBinding =
        ComposeViewBinding.inflate(layoutInflater)

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelSplash = ViewModelProvider(this)[ViewModelSplash::class.java]

        binding.composeView.setContent {
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
