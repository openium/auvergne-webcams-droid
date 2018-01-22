package fr.openium.auvergnewebcams.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.view.animation.AnimationUtils
import com.github.salomonbrys.kodein.instance
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.hasNetwork
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.SectionList
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_splash.*
import retrofit2.adapter.rxjava2.Result
import java.util.concurrent.TimeUnit

/**
 * Created by laura on 20/03/2017.
 */
class ActivitySplash : AbstractActivity() {

    protected val api: AWApi by kodeinInjector.instance()

    override val layoutId: Int
        get() = R.layout.activity_splash

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (applicationContext.hasNetwork) {
            disposables.add(Observable.zip(Observable.timer(2, TimeUnit.SECONDS), api.getSections(),
                    BiFunction
                    { _: Long, result: Result<SectionList> ->
                        if (!result.isError && result.response()?.body() != null) {
                            Realm.getDefaultInstance().use {
                                it.executeTransaction { realm ->
                                    val sections = result.response()!!.body()!!
                                    for (section in sections.sections) {
                                        for (webcam in section.webcams) {
                                            if (webcam.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                                                // load media ld
                                                webcam.mediaViewSurfLD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfLD)
                                                webcam.mediaViewSurfHD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfHD)
                                            }

                                            val webcamDB = realm.where(Webcam::class.java)
                                                    .equalTo(Webcam::uid.name, webcam.uid)
                                                    .findFirst()
                                            webcam.lastUpdate = webcamDB?.lastUpdate
                                            webcam.isFavoris = webcamDB?.isFavoris ?: false
                                        }
                                    }
                                    realm.insertOrUpdate(sections.sections)
                                }
                            }
                        } else {
                            loadFromAssets()
                        }
                        true
                    })
                    .subscribe(
                            {
                                startActivityMain()
                            },
                            {
                                startActivityMain()
                            }))

        } else {
            val getDataObs = Observable
                    .fromCallable {
                        loadFromAssets()
                    }
            disposables.add(Observable.combineLatest(getDataObs, Observable.timer(3, TimeUnit.SECONDS), BiFunction
            { _: Unit, _: Long ->
                true
            }).subscribe
            {
                startActivityMain()
            })
        }

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

    private fun loadFromAssets() {
        Realm.getDefaultInstance().use {
            if (it.where(Section::class.java).findAll().count() == 0) {
                val sections = SectionList.getSectionsFromAssets(applicationContext)
                if (sections != null) {
                    it.executeTransaction {
                        it.insertOrUpdate(sections.sections)
                    }
                }
            }
        }
    }

    private fun startActivityMain() {
        val intent = Intent(this, ActivityMain::class.java)
        val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
        startActivity(intent, bundle)
        finish()
    }
}