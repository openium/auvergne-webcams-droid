package fr.openium.auvergnewebcams.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.view.animation.AnimationUtils
import com.github.salomonbrys.kodein.instance
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.fromIOToMain
import fr.openium.auvergnewebcams.ext.hasNetwork
import fr.openium.auvergnewebcams.ext.toUnixTimestamp
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.SectionList
import fr.openium.auvergnewebcams.model.Weather
import fr.openium.auvergnewebcams.rest.AWWeatherApi
import fr.openium.auvergnewebcams.rest.ApiHelper
import fr.openium.auvergnewebcams.utils.PreferencesAW
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_splash.*
import retrofit2.adapter.rxjava2.Result
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by laura on 20/03/2017.
 */
class ActivitySplash : AbstractActivity() {

    private val apiWeather: AWWeatherApi by kodeinInjector.instance()
    private val apiHelper: ApiHelper by kodeinInjector.instance()

    override val layoutId: Int
        get() = R.layout.activity_splash

    private var nbRemainingRequests: AtomicInteger? = AtomicInteger(0)

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (applicationContext.hasNetwork) {
            disposables.add(Single.zip(Observable.timer(2, TimeUnit.SECONDS).singleOrError(), apiHelper.getSections(), BiFunction { time: Long, list: Result<SectionList> ->
                if (list.isError || list.response()?.body() == null) {
                    loadFromAssets()
                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        initWeather()
                    }, {
                        initWeather()
                    }))
        } else {
            val getDataObs = Observable.fromCallable {
                loadFromAssets()
            }
            disposables.add(Observable.combineLatest(getDataObs, Observable.timer(3, TimeUnit.SECONDS), BiFunction { _: Unit, _: Long ->
                true
            }).subscribe {
                startActivityMain()
            })
        }
    }

    private fun initWeather() {
        PreferencesAW.setLastUpdateWeatherTimestamp(applicationContext, System.currentTimeMillis().toUnixTimestamp())

        Realm.getDefaultInstance().use {
            it.executeTransaction { realm ->
                val sections = realm.where(Section::class.java)
                        .sort(Section::order.name)
                        .isNotEmpty(Section::webcams.name)
                        .findAll()

                for (section in sections) {
                    if (section.latitude != 0.0 || section.longitude != 0.0) {
                        nbRemainingRequests?.incrementAndGet()
                    }
                }

                if (sections.isEmpty()) {
                    startActivityMain()
                }

                for (section in sections) {
                    if (section.latitude != 0.0 || section.longitude != 0.0) {
                        disposables.add(apiWeather.queryByGeographicCoordinates(section.latitude, section.longitude, getString(R.string.app_weather_id)).fromIOToMain().subscribe({ weatherRest ->
                            Realm.getDefaultInstance().use {
                                weatherRest.response()?.body()?.let { body ->
                                    it.executeTransaction {
                                        it.where(Weather::class.java).equalTo(Weather::lat.name, body.coord?.lat).equalTo(Weather::lon.name, body.coord?.lon).findAll().deleteAllFromRealm()

                                        val weather = Weather(body.weather?.get(0)?.id, body.main?.temp, body.coord?.lon
                                                ?: 0.0, body.coord?.lat
                                                ?: 0.0)
                                        it.insertOrUpdate(weather)
                                    }
                                }
                                nbRemainingRequests?.decrementAndGet()
                                if (nbRemainingRequests?.get() == 0) {
                                    startActivityMain()
                                }
                            }
                        }, { e ->
                            Timber.e("Error init weather ${e.message}")
                        }))
                    }
                }
            }
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