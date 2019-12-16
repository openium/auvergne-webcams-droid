package fr.openium.auvergnewebcams.rest

import android.content.Context
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.SectionList
import fr.openium.auvergnewebcams.model.Weather
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.model.rest.WeatherRest
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import retrofit2.adapter.rxjava2.Result
import timber.log.Timber

/**
 * Created by godart on 29/11/2017.
 */
class ApiHelper(val context: Context, val api: AWApi, val apiWeather: AWWeatherApi) {

    // --------------------- FLUX --------------------- //

    fun getSections(): Single<Result<SectionList>> {
        return startQueryLogged(api.getSections()).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).doOnSuccess { result ->
            if (!result.isError && result.response()?.body() != null) {
                Realm.getDefaultInstance().use {
                    it.executeTransaction {
                        result.response()?.body()?.let { sections ->

                            for (section in sections.sections) {

                                section.latitude = Math.round(section.latitude * 100.0) / 100.0
                                section.longitude = Math.round(section.longitude * 100.0) / 100.0

                                for (webcam in section.webcams) {
                                    if (webcam.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                                        // load media ld
                                        try {
                                            webcam.mediaViewSurfLD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfLD)
                                            webcam.mediaViewSurfHD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfHD)
                                        } catch (e: Exception) {
                                            Timber.e(e)
                                        }
                                    }

                                    val webcamDB = it.where(Webcam::class.java)
                                        .equalTo(Webcam::uid.name, webcam.uid)
                                        .findFirst()
                                    if (webcamDB != null) {
                                        if (webcamDB.lastUpdate != null) {
                                            webcam.lastUpdate = webcamDB.lastUpdate
                                        }
                                        webcam.isFavoris = webcamDB.isFavoris
                                    }
                                    if (webcam.hidden == null) {
                                        webcam.hidden = false
                                    }
                                }

                                section.webcams = RealmList<Webcam>().apply {
                                    addAll(section.webcams.filter { it.hidden == false })
                                }
                            }

                            it.where(Section::class.java).findAll().deleteAllFromRealm()
                            it.where(Webcam::class.java).findAll().deleteAllFromRealm()

                            it.insertOrUpdate(sections.sections)
                        }
                    }
                }
            }
        }.doOnError {
            Timber.e(it, "Error getting sections")
        }
    }

    fun getWeatherForSection(context: Context, section: Section): Single<Result<WeatherRest>>? {
        return if (section.latitude != 0.0 || section.longitude != 0.0) {
            startQueryLogged(apiWeather.queryByGeographicCoordinates(
                section.latitude,
                section.longitude,
                context.getString(R.string.app_weather_id)
            )
                .fromIOToMain()
                .doOnSuccess { weatherRest ->
                    Realm.getDefaultInstance().use {
                        weatherRest.response()?.body()?.let { body ->
                            it.executeTransaction {
                                it.where(Weather::class.java).equalTo(Weather::lat.name, body.coord?.lat)
                                    .equalTo(Weather::lon.name, body.coord?.lon).findAll().deleteAllFromRealm()

                                val weather = Weather(
                                    body.weather?.get(0)?.id, body.main?.temp, body.coord?.lon
                                            ?: 0.0, body.coord?.lat ?: 0.0
                                )
                                it.insertOrUpdate(weather)
                            }
                        }
                    }
                }.doOnError { e ->
                    Timber.e("Error init weather ${e.message}")
                })
        } else {
            null
        }
    }

    fun getWeatherForAllSections(context: Context) {
        val sections = Realm.getDefaultInstance().use {
            it.where(Section::class.java)
                .sort(Section::order.name)
                .isNotEmpty(Section::webcams.name)
                .findAll()
        }

        if (sections != null) {
            for (section in sections) {
                getWeatherForSection(context, section)
            }
        }
    }

// ----------------- OTHER METHODS ---------------- //

    fun <T> startQueryLogged(query: Single<T>): Single<T> {
        return Single.defer {
            query
        }
    }
}

class NotLoggedException : Throwable("Error not logged")