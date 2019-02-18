package fr.openium.auvergnewebcams.rest

import android.content.Context
import fr.openium.auvergnewebcams.ext.toRealmList
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.SectionList
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import io.reactivex.Single
import io.realm.Realm
import retrofit2.HttpException
import timber.log.Timber

/**
 * Created by godart on 29/11/2017.
 */
class ApiHelper(val context: Context, val api: AWApi) {

    companion object {
        private const val TAG = "[AW]"
    }

    /**
     * Method that show a generic log when error is unknown
     */
    private fun showSingleErrorLog(functionName: String, error: Throwable?) {
        Timber.e("$TAG][ERROR] -> $functionName: $error")
    }

    /**
     * Method that shows the correct log for the given error code
     */
    private fun showErrorLogWithCode(functionName: String, code: Int?, error: Throwable?) {
        when (code) {
            400 -> {
                Timber.e("$TAG -> $functionName: 400 Bad request: $error")
            }
            401 -> {
                Timber.e("$TAG -> $functionName: 401 Unauthorized: $error")
            }
            403 -> {
                Timber.e("$TAG -> $functionName: 403 Forbidden: $error")
            }
            404 -> {
                Timber.e("$TAG -> $functionName: 404 Not found: $error")
            }
            409 -> {
                Timber.e("$TAG -> $functionName: 409 Already exists: $error")
            }
            else -> {
                showSingleErrorLog(functionName, error)
            }
        }
    }

    // --------------------- FLUX --------------------- //

    fun getSections(): Single<SectionList> {
        return api.getSections()
            .doOnSuccess { sectionsList ->
                Realm.getDefaultInstance().use {
                    it.executeTransaction {

                        for (section in sectionsList.sections) {

                            //TODO comment this
                            for (webcam in section.webcams) {

                                //TODO What this does ?
                                if (webcam.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                                    // load media ld
                                    webcam.mediaViewSurfLD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfLD)
                                    webcam.mediaViewSurfHD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfHD)
                                }

                                //TODO Why we don't just clean all the database and then add all again ?
                                val webcamDB = it.where(Webcam::class.java)
                                    .equalTo(Webcam::uid.name, webcam.uid)
                                    .findFirst()

                                //TODO comment this
                                if (webcamDB != null) {
                                    if (webcamDB.lastUpdate != null) {
                                        webcam.lastUpdate = webcamDB.lastUpdate
                                    }
                                    webcam.isFavoris = webcamDB.isFavoris
                                }

                                //TODO Comment this
                                if (webcam.hidden == null) {
                                    webcam.hidden = false
                                }
                            }

                            //Here we have the latest valid section list
                            section.webcams = section.webcams.filter { it.hidden == false }.toRealmList()
                        }

                        //Just delete all the database
                        it.where(Section::class.java).findAll().deleteAllFromRealm()
                        it.where(Webcam::class.java).findAll().deleteAllFromRealm()

                        //And add the new data
                        it.insertOrUpdate(sectionsList.sections)

                        Timber.d("$TAG: Sections inserted")
                    }
                }
            }.doOnError {
                if (it is HttpException) {
                    showErrorLogWithCode("getSections", it.code(), it)
                } else {
                    showSingleErrorLog("getSections", it)
                }
            }
    }

//
//    fun getWeatherForSection(context: Context, section: Section): Single<Result<WeatherRest>>? {
//        return if (section.latitude != 0.0 || section.longitude != 0.0) {
//            startQueryLogged(apiWeather.queryByGeographicCoordinates(
//                section.latitude,
//                section.longitude,
//                context.getString(R.string.app_weather_id)
//            )
//                .fromIOToMain()
//                .doOnSuccess { weatherRest ->
//                    Realm.getDefaultInstance().use {
//                        weatherRest.response()?.body()?.let { body ->
//                            it.executeTransaction {
//                                it.where(Weather::class.java).equalTo(Weather::lat.name, body.coord?.lat)
//                                    .equalTo(Weather::lon.name, body.coord?.lon).findAll().deleteAllFromRealm()
//
//                                val weather = Weather(
//                                    body.weather?.get(0)?.id, body.main?.temp, body.coord?.lon
//                                        ?: 0.0, body.coord?.lat ?: 0.0
//                                )
//                                it.insertOrUpdate(weather)
//                            }
//                        }
//                    }
//                }.doOnError { e ->
//                    Timber.e("Error init weather ${e.message}")
//                })
//        } else {
//            null
//        }
//    }
//
//    fun getWeatherForAllSections(context: Context) {
//        val sections = Realm.getDefaultInstance().use {
//            it.where(Section::class.java)
//                .sort(Section::order.name)
//                .isNotEmpty(Section::webcams.name)
//                .findAll()
//        }
//
//        if (sections != null) {
//            for (section in sections) {
//                getWeatherForSection(context, section)
//            }
//        }
//    }

}