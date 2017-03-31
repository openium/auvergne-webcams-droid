package fr.openium.auvergnewebcams.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.SectionList
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.concurrent.TimeUnit

/**
 * Created by laura on 20/03/2017.
 */
class ActivitySplash : AbstractActivity() {

    override val layoutId: Int
        get() = R.layout.activity_splash

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val getDataObs = Observable.fromCallable {
            Realm.getDefaultInstance().use {
                val sections = SectionList.getSectionsFromAssets(applicationContext)
                if (sections != null) {
                    it.executeTransaction { realm ->
                        realm.insertOrUpdate(sections.sections)
                    }
                }
            }
        }

        subscriptions.add(Observable.combineLatest(getDataObs, Observable.timer(3, TimeUnit.SECONDS), BiFunction {
            _: Unit, _: Long ->
            true
        }).subscribe {
            startActivityMain()
        })
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

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun startActivityMain() {
        val intent = Intent(this, ActivityMain::class.java)
        startActivity(intent)
        finish()
    }
}