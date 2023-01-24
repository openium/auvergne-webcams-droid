package fr.openium.auvergnewebcams.repository

import fr.openium.auvergnewebcams.model.AWClient
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.rest.model.SectionList
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import fr.openium.auvergnewebcams.utils.LogUtils
import fr.openium.auvergnewebcams.utils.Optional
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

/**
 * Created by Openium on 19/02/2019.
 */
class SectionRepository(private val client: AWClient, private val api: AWApi, private val webcamRepository: WebcamRepository) {

    // WS

    fun fetch(): Single<SectionList> =
        api.getSections()
            .doOnSuccess { sectionsList ->

                Timber.d("Sections count ${sectionsList.sections.count()}")

                for (section in sectionsList.sections) {

                    for (webcam in section.webcams) {

                        webcam.populateId(section.uid)

                        if (webcam.type == Webcam.WebcamType.VIEWSURF.nameType) {
                            webcam.mediaViewSurfLD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurf)
                            webcam.mediaViewSurfHD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurf)
                        }

                        // Try to get the webcam to know if it's already in DB
                        val webcamDB = webcamRepository.getWebcam(webcam.uid)

                        webcamDB?.also { wdb ->
                            wdb.lastUpdate?.also { webcam.lastUpdate = it }
                            webcam.isFavoris = wdb.isFavoris
                        }

                        if (webcam.hidden == null) {
                            webcam.hidden = false
                        }
                    }

                    val webcamsSectionFiltered = section.webcams.filter { it.hidden == false }
                    val rowsWebcam = webcamRepository.insert(webcamsSectionFiltered)
                    Timber.d("${rowsWebcam.count()} webcams inserted for section ${section.title} | ${section.webcams.count() - webcamsSectionFiltered.count()} are hidden")
                    webcamRepository.deleteAllNoMoreInSection(webcamsSectionFiltered.map { it.uid }, section.uid)
                }

                val rowsSection = insert(sectionsList.sections)
                Timber.d("${rowsSection.count()} section inserted")
                deleteAllNotInUIDs(sectionsList.sections.map { it.uid })
            }.doOnError {
                LogUtils.showSingleErrorLog("Fetch sections", it)
            }

    // Local

    fun getSectionSingle(sectionId: Long): Single<Optional<Section>> =
        client.database.sectionDao().getSectionSingle(sectionId).map {
            Optional.of(it)
        }

    fun getSections(): List<Section> =
        client.database.sectionDao().getSections()

    fun getSectionsSingle(): Single<List<Section>> =
        client.database.sectionDao().getSectionsSingle()

    fun update(section: Section): Int =
        client.database.sectionDao().update(section)

    fun update(sections: List<Section>): Int =
        client.database.sectionDao().update(sections)

    fun insert(section: Section): Long =
        client.database.sectionDao().insert(section)

    fun insert(sections: List<Section>): List<Long> =
        client.database.sectionDao().insert(sections)

    fun deleteAllNotInUIDs(ids: List<Long>): Completable =
        client.database.sectionDao().deleteAllNotInUids(ids)
}