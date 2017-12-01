package fr.openium.auvergnewebcams.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.hasNetwork
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.SectionList
import fr.openium.auvergnewebcams.model.Webcam
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_splash.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
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
        val getDataObs = Observable
                .fromCallable {
                    Realm.getDefaultInstance().use {

                        if (applicationContext.hasNetwork) { // TODO load from url
                            val sections = SectionList.getSectionsFromAssets(applicationContext)
                            if (sections != null) {
                                for (section in sections.sections) {
                                    for (webcam in section.webcams) {
                                        if (webcam.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                                            // load media ld
                                            webcam.mediaViewSurfLD = getMediaViewSurf(webcam.viewsurfLD)
                                            webcam.mediaViewSurfHD = getMediaViewSurf(webcam.viewsurfHD)
                                        }
                                    }
                                }


                                it.executeTransaction { realm ->
                                    realm.insertOrUpdate(sections.sections)
                                }
                            }

                        } else if (it.where(Section::class.java).findAll().count() == 0) {
                            val sections = SectionList.getSectionsFromAssets(applicationContext)
                            if (sections != null) {
                                it.executeTransaction { realm ->
                                    realm.insertOrUpdate(sections.sections)
                                }
                            }
                        }
                    }

                }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())

        subscriptions.add(Observable.combineLatest(getDataObs, Observable.timer(3, TimeUnit.SECONDS), BiFunction
        { _: Unit, _: Long ->
            true
        }).subscribe
        {
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

    private fun getMediaViewSurf(urlBase: String?): String {
        var media = ""
        if (!urlBase.isNullOrEmpty()) {
            val urlLD = String.format("%s/last", urlBase)
            val url = URL(urlLD)
            val bufferedReader = BufferedReader(InputStreamReader(url.openStream()))


            var line = bufferedReader.readLine()
            while (line != null) {
                media += line
                line = bufferedReader.readLine()
            }

            bufferedReader.close()
        }
        return media
    }
}