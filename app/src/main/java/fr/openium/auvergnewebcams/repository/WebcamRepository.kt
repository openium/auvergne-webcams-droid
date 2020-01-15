package fr.openium.auvergnewebcams.repository

import fr.openium.auvergnewebcams.model.CustomClient
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.utils.Optional
import io.reactivex.Observable

/**
 * Created by Openium on 18/04/2018.
 */
class WebcamRepository(private val client: CustomClient) {

    fun getWebcam(webcamId: Long): Webcam? = client.database.webcamDao().getWebcam(webcamId)

    fun getWebcamWithPartialUrl(url: String): Webcam? = client.database.webcamDao().getWebcamWithPartialUrl(url)

    fun getWebcamObs(webcamId: Long): Observable<Optional<Webcam>> {
        return client.database.webcamDao().getWebcamObs(webcamId).map {
            Optional.of(it.firstOrNull())
        }
    }

    fun getWebcamsObs(): Observable<List<Webcam>> {
        return client.database.webcamDao().getWebcamsObs()
    }

    fun getWebcamsObs(sectionID: String): Observable<List<Webcam>> {
        return client.database.webcamDao().getWebcamsObs(sectionID)
    }

    fun update(webcam: Webcam): Int {
        return client.database.webcamDao().update(webcam)
    }

    fun update(webcams: List<Webcam>): Int {
        return client.database.webcamDao().update(webcams)
    }

    fun insert(webcam: Webcam): Long {
        return client.database.webcamDao().insert(webcam)
    }

    fun insert(webcams: List<Webcam>): List<Long> {
        return client.database.webcamDao().insert(webcams)
    }

    fun delete(webcam: Webcam) {
        client.database.webcamDao().delete(webcam)
    }

    fun delete(webcams: List<Webcam>) {
        client.database.webcamDao().delete(webcams)
    }

    fun deleteAllNoMoreInSection(map: List<Long>, sectionUid: Long) {
        client.database.webcamDao().deleteAllNoMoreInSection(map, sectionUid)
    }
}