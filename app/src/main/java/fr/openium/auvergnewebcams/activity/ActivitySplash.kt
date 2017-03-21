package fr.openium.auvergnewebcams.activity

import android.content.Intent
import android.os.Bundle
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.SectionList
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.realm.Realm
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

        subscriptions.add(Observable.combineLatest(getDataObs, Observable.timer(1, TimeUnit.SECONDS), BiFunction {
            _: Unit, _: Long ->
            true
        }).subscribe {
            startActivityMain()
        })
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