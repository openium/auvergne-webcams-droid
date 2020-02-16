package fr.openium.auvergnewebcams.repository

import fr.openium.auvergnewebcams.model.AWClient
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.Optional
import io.reactivex.Single

/**
 * Created by Openium on 19/02/2019.
 */
class WebcamRepository(private val client: AWClient, private val dateUtils: DateUtils) {

    fun getWebcam(webcamId: Long): Webcam? =
        client.database.webcamDao().getWebcam(webcamId)

    fun getWebcamSingle(webcamId: Long): Single<Optional<Webcam>> =
        client.database.webcamDao().getWebcamSingle(webcamId).map {
            Optional.of(it)
        }

    fun getWebcamsSingle(): Single<List<Webcam>> =
        client.database.webcamDao().getWebcamsSingle()

    fun getWebcamsSingle(sectionId: Long): Single<List<Webcam>> =
        client.database.webcamDao().getWebcamsSingle(sectionId)

    fun update(webcam: Webcam): Int =
        client.database.webcamDao().update(webcam)

    fun update(webcams: List<Webcam>): Int =
        client.database.webcamDao().update(webcams)

    fun insert(webcam: Webcam): Long =
        client.database.webcamDao().insert(webcam)

    fun insert(webcams: List<Webcam>): List<Long> =
        client.database.webcamDao().insert(webcams)

    fun deleteAllNoMoreInSection(map: List<Long>, sectionUid: Long) =
        client.database.webcamDao().deleteAllNoMoreInSection(map, sectionUid)

    private fun getWebcamWithPartialUrl(url: String): Webcam? =
        client.database.webcamDao().getWebcamWithPartialUrl(url)

    fun updateLastUpdateDate(lastModified: String, urlMedia: String) {
        getWebcamWithPartialUrl(urlMedia)?.let {
            if (!lastModified.isBlank()) {
                val newTime = dateUtils.parseDateGMT(lastModified)

                if (it.lastUpdate == null || newTime != it.lastUpdate!!) {
                    it.lastUpdate = newTime
                    update(it)
                }
            }
        }
    }
}