package fr.openium.auvergnewebcams.repository

import fr.openium.auvergnewebcams.enums.WebcamType
import fr.openium.auvergnewebcams.ext.jsonKey
import fr.openium.auvergnewebcams.ext.populateId
import fr.openium.auvergnewebcams.model.AWClient
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.rest.model.SectionList
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import fr.openium.auvergnewebcams.utils.LogUtils
import fr.openium.auvergnewebcams.utils.Optional
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

/**
 * Created by Openium on 19/02/2019.
 */
class SectionRepository(
    private val client: AWClient,
    private val api: AWApi,
    private val webcamRepository: WebcamRepository,
) {

    // WS

    fun fetch(): Single<SectionList> =
        api.getSections()
            .doOnSuccess { sectionsList ->
                insertSectionsAndWebcams(sectionsList)
            }.doOnError {
                LogUtils.showSingleErrorLog("Fetch sections", it)
            }

    // Local

    fun insertSectionsAndWebcams(sectionsList: SectionList) {
        Timber.d("Sections count ${sectionsList.sections.count()}")

        for (section in sectionsList.sections) {

            for (webcam in section.webcams) {

                webcam.populateId(section.uid)

                if (webcam.type == WebcamType.VIEWSURF.jsonKey) {
                    val media = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurf)

                    webcam.mediaViewSurfLD = media
                    webcam.mediaViewSurfHD = media
                } else if (webcam.type == WebcamType.VIDEO.jsonKey) {
                    val media = LoadWebCamUtils.getMediaViewVideo(webcam.video)

                    webcam.mediaViewSurfLD = media
                    webcam.mediaViewSurfHD = media
                }

                // Try to get the webcam to know if it's already in DB
                val webcamDB = webcamRepository.getWebcam(webcam.uid)

                webcamDB?.also { wdb ->
                    wdb.lastUpdate?.also { webcam.lastUpdate = it }
                    webcam.isFavorite = wdb.isFavorite
                }

                if (webcam.hidden == null) {
                    webcam.hidden = false
                }
            }

            val webcamsSectionFiltered = section.webcams.filter { it.hidden == false }
            val rowsWebcam = webcamRepository.insert(webcamsSectionFiltered)
            Timber.d("${rowsWebcam.count()} webcams inserted for section ${section.title} | ${section.webcams.count() - webcamsSectionFiltered.count()} are hidden")
            webcamRepository.deleteAllNoMoreInSection(
                webcamsSectionFiltered.map { it.uid },
                section.uid
            )
        }

        val rowsSection = insert(sectionsList.sections)
        Timber.d("${rowsSection.count()} section inserted")
        deleteAllNotInUIDs(sectionsList.sections.map { it.uid })
    }

    fun getSectionSingle(sectionId: Long): Single<Optional<Section>> =
        client.database.sectionDao().getSectionSingle(sectionId).map {
            Optional.of(it)
        }

    fun watchSectionWithCameras(sectionId: Long): Single<Optional<SectionWithCameras>> =
        client.database.sectionDao().watchSectionWithCameras(sectionId).map {
            Optional.of(it)
        }

    fun getSections(): List<Section> =
        client.database.sectionDao().getSections()

    fun watchSectionsWithCameras(): Flow<List<SectionWithCameras>> =
        client.database.sectionDao().watchSectionsWithCameras()

    fun update(section: Section): Int =
        client.database.sectionDao().update(section)

    fun update(sections: List<Section>): Int =
        client.database.sectionDao().update(sections)

    private fun insert(sections: List<Section>): List<Long> =
        client.database.sectionDao().insert(sections)

    private fun deleteAllNotInUIDs(ids: List<Long>): Completable =
        client.database.sectionDao().deleteAllNotInUids(ids)
}