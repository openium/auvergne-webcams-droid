package fr.openium.auvergnewebcams.repository

import fr.openium.auvergnewebcams.model.AWClient
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.Optional
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by Openium on 19/02/2019.
 */
class WebcamRepository(private val client: AWClient, private val dateUtils: DateUtils) {

    fun getWebcam(webcamId: Long): Webcam? =
        client.database.webcamDao().getWebcam(webcamId)

    fun getWebcamFlow(webcamId: Long): Flow<Optional<Webcam>> =
        client.database.webcamDao().getWebcamFlow(webcamId).map {
            Optional.of(it)
        }

    fun watchAllWebcams() = client.database.webcamDao().watchAllWebcams()

    fun update(webcam: Webcam): Int =
        client.database.webcamDao().update(webcam)

    fun insert(webcams: List<Webcam>): List<Long> =
        client.database.webcamDao().insert(webcams)

    fun deleteAllNoMoreInSection(map: List<Long>, sectionUid: Long) =
        client.database.webcamDao().deleteAllNoMoreInSection(map, sectionUid)

    private fun getWebcamWithPartialUrl(url: String): Webcam? =
        client.database.webcamDao().getWebcamWithPartialUrl(url)

    fun updateLastUpdateDate(lastModified: String, urlMedia: String) {
        getWebcamWithPartialUrl(urlMedia)?.let {
            if (lastModified.isNotBlank()) {
                val newTime = dateUtils.parseDateGMT(lastModified) ?: 0L

                if (it.lastUpdate == null || newTime != it.lastUpdate!!) {
                    it.lastUpdate = newTime
                    update(it)
                }
            }
        }
    }
}