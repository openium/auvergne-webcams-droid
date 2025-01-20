package fr.openium.auvergnewebcams.ui.splash

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.auvergnewebcams.ui.main.ActivityMain
import fr.openium.kotlintools.ext.show
import fr.openium.kotlintools.ext.startActivity
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_splash.linearLayoutSplashText
import kotlinx.android.synthetic.main.activity_splash.mCloud1
import kotlinx.android.synthetic.main.activity_splash.mCloud2
import kotlinx.android.synthetic.main.activity_splash.mCloud3
import kotlinx.android.synthetic.main.activity_splash.mCloud4
import kotlinx.android.synthetic.main.activity_splash.mCloud5
import kotlinx.android.synthetic.main.activity_splash.mCloud6
import kotlinx.android.synthetic.main.activity_splash.mCloud7
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ActivitySplash : AbstractActivity() {

    override val layoutId: Int = R.layout.activity_splash

    private lateinit var viewModelSplash: ViewModelSplash

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelSplash = ViewModelProvider(this).get(ViewModelSplash::class.java)

        // Get new data
        Completable.merge(
            listOf(
                viewModelSplash.updateData(),
                Completable.timer(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                        linearLayoutSplashText.show()
                        mCloud1.show()
                        mCloud2.show()
                        mCloud3.show()
                        mCloud4.show()
                        mCloud5.show()
                        mCloud6.show()
                        mCloud7.show()

                        val alphaAnimation =
                            AnimationUtils.loadAnimation(this, R.anim.anim_alpha_only)
                        mCloud5.startAnimation(alphaAnimation)
                        mCloud6.startAnimation(alphaAnimation)
                        mCloud7.startAnimation(alphaAnimation)

                        val translateRightSlowAnimation =
                            AnimationUtils.loadAnimation(this, R.anim.anim_translate_right_slow)
                        mCloud1.startAnimation(translateRightSlowAnimation)

                        val translateLeftSlowAnimation =
                            AnimationUtils.loadAnimation(this, R.anim.anim_translate_left_slow)
                        mCloud2.startAnimation(translateLeftSlowAnimation)

                        val translateRightFastAnimation =
                            AnimationUtils.loadAnimation(this, R.anim.anim_translate_right_fast)
                        mCloud3.startAnimation(translateRightFastAnimation)

                        val translateLeftFastAnimation =
                            AnimationUtils.loadAnimation(this, R.anim.anim_translate_left_fast)
                        mCloud4.startAnimation(translateLeftFastAnimation)
                    })
        ).subscribe({
            startActivityMain()
        }, { Timber.e(it) }).addTo(disposables)
    }

    // --- Methods
    // ---------------------------------------------------

    private fun startActivityMain() {
        startActivity<ActivityMain>()
        finish()
    }
}