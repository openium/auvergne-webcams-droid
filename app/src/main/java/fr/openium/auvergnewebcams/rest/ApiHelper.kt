package fr.openium.auvergnewebcams.rest

import android.content.Context
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.SectionList
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import retrofit2.adapter.rxjava2.Result
import timber.log.Timber

/**
 * Created by godart on 29/11/2017.
 */
class ApiHelper(val context: Context, val api: AWApi) {

    // --------------------- FLUX --------------------- //

    fun getSections(): Single<Result<SectionList>> {
        return startQueryLogged(api.getSections()).subscribeOn(Schedulers.io()).doOnSuccess { result ->

            if (!result.isError && result.response()?.body() != null) {
                Realm.getDefaultInstance().use {
                    val sections = result.response()!!.body()!!

                    val listId = arrayListOf<Long>()

                    for (section in sections.sections) {

                        section.latitude = Math.round(section.latitude * 100.0) / 100.0
                        section.longitude = Math.round(section.longitude * 100.0) / 100.0

                        for (webcam in section.webcams) {
                            if (webcam.type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
                                // load media ld
                                webcam.mediaViewSurfLD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfLD)
                                webcam.mediaViewSurfHD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfHD)
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

                        listId.add(section.uid)
                    }

                    it.executeTransaction {
                        //it.where(Section::class.java).findAll().deleteAllFromRealm()
                        //it.where(Webcam::class.java).findAll().deleteAllFromRealm()

                        val item = it.where(Section::class.java).not().`in`(Section::uid.name, listId.toTypedArray()).findAll()
                        item.deleteAllFromRealm()
                        it.insertOrUpdate(sections.sections)
                    }
                }
            }
        }.doOnError {
                    Timber.d("Error getting sections")
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