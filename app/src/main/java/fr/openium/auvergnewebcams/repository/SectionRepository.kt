package fr.openium.auvergnewebcams.repository

import fr.openium.auvergnewebcams.BuildConfig
import fr.openium.auvergnewebcams.enums.WebcamType
import fr.openium.auvergnewebcams.ext.jsonKey
import fr.openium.auvergnewebcams.ext.populateId
import fr.openium.auvergnewebcams.model.AWClient
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.rest.AWWeatherApi
import fr.openium.auvergnewebcams.rest.model.SectionList
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import fr.openium.auvergnewebcams.utils.LogUtils
import fr.openium.auvergnewebcams.utils.Optional
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Created by Openium on 19/02/2019.
 */
class SectionRepository(
    private val client: AWClient,
    private val api: AWApi,
    private val weatherApi: AWWeatherApi,
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

    fun updateSectionsWeather(sections: List<Section>) = sections.forEach { section ->
        configureWeather(section)
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

        updateSectionsWeather(sectionsList.sections)
    }

    fun getSectionSingle(sectionId: Long): Single<Optional<Section>> =
        client.database.sectionDao().getSectionSingle(sectionId).map {
            Optional.of(it)
        }

    fun getSectionFlow(sectionId: Long): Flow<Optional<Section>> =
        client.database.sectionDao().getSectionFlow(sectionId)
            .map { section ->
                Optional.of(section)
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

    private fun configureWeather(section: Section) {
        if (section.latitude != 0.0 && section.longitude != 0.0) {
            weatherApi.queryByGeographicCoordinates(
                section.latitude,
                section.longitude,
                BuildConfig.OPEN_WEATHER_API_KEY,
            ).doOnSuccess { res ->
                if (res.isSuccessful) {
                    section.weatherUid = res?.body()?.weather?.get(0)?.id
                    section.weatherTemp = res?.body()?.main?.temp

                    update(section)

                    Timber.d("Success updating weather for " + section.title)
                } else {
                    Timber.e("HTTP " + res.code() + " when getting weather for " + section.title)
                }
            }.doOnError { error ->
                Timber.e(error, "Exception when getting weather for " + section.title)
            }.fromIOToMain().subscribe()
        }
    }
}