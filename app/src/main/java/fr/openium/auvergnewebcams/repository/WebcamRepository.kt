package fr.openium.auvergnewebcams.repository

import fr.openium.auvergnewebcams.model.CustomClient
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.Optional
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by Openium on 19/02/2019.
 */
class WebcamRepository(private val client: CustomClient) {

    fun getWebcam(webcamId: Long): Webcam? =
        client.database.webcamDao().getWebcam(webcamId)

    fun getWebcamObs(webcamId: Long): Observable<Optional<Webcam>> =
        client.database.webcamDao().getWebcamObs(webcamId).map {
            Optional.of(it.firstOrNull())
        }

    fun getWebcams(): List<Webcam> =
        client.database.webcamDao().getWebcams()

    fun getWebcamsSingle(): Single<List<Webcam>> =
        client.database.webcamDao().getWebcamsSingle()

    fun getWebcamsObs(): Observable<List<Webcam>> =
        client.database.webcamDao().getWebcamsObs()

    fun getWebcamsObs(sectionID: String): Observable<List<Webcam>> =
        client.database.webcamDao().getWebcamsObs(sectionID)

    fun update(webcam: Webcam): Int =
        client.database.webcamDao().update(webcam)

    fun update(webcams: List<Webcam>): Int =
        client.database.webcamDao().update(webcams)

    fun insert(webcam: Webcam): Long =
        client.database.webcamDao().insert(webcam)

    fun insert(webcams: List<Webcam>): List<Long> =
        client.database.webcamDao().insert(webcams)

    fun delete(webcam: Webcam) =
        client.database.webcamDao().delete(webcam)

    fun delete(webcams: List<Webcam>) =
        client.database.webcamDao().delete(webcams)

    fun deleteAllNoMoreInSection(map: List<Long>, sectionUid: Long) =
        client.database.webcamDao().deleteAllNoMoreInSection(map, sectionUid)

    private fun getWebcamWithPartialUrl(url: String): Webcam? =
        client.database.webcamDao().getWebcamWithPartialUrl(url)

    fun updateLastUpdateDate(lastModified: String, urlMedia: String) {
        getWebcamWithPartialUrl(urlMedia)?.let {
            if (!lastModified.isBlank()) {
                val newTime = DateUtils.parseDateGMT(lastModified)

                if (it.lastUpdate == null || newTime != it.lastUpdate!!) {
                    it.lastUpdate = newTime
                    update(it)
                }
            }
        }
    }
}