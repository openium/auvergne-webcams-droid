package fr.openium.auvergnewebcams.repository

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
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
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.InputStreamReader
import java.util.concurrent.CancellationException

/**
 * Created by Openium on 19/02/2019.
 */
class SectionRepository(
    private val context: Context,
    private val client: AWClient,
    private val api: AWApi,
    private val weatherApi: AWWeatherApi,
    private val webcamRepository: WebcamRepository,
) {

    // WS

    suspend fun fetch(): Result<SectionList> = runCatching {
        val resp = api.getSections()

        if (resp.isSuccessful) {
            resp.body()?.also {
                insertSectionsAndWebcams(it)
            } ?: error("Response body is null")
        } else {
            LogUtils.showSingleErrorLog("Fetch sections", HttpException(resp))
            throw HttpException(resp)
        }
    }


    private suspend fun updateSectionsWeather(sections: List<Section>): List<Result<Unit>> =
        sections.map { section -> configureWeather(section) }


// Local

    private suspend fun insertSectionsAndWebcams(sectionsList: SectionList) {
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

    suspend fun getSectionWithCameras(sectionId: Long): SectionWithCameras =
        client.database.sectionDao().getSectionWithCameras(sectionId)

    private suspend fun getSections(): List<Section> =
        client.database.sectionDao().getSections()

    fun watchSectionsWithCameras(): Flow<List<SectionWithCameras>> =
        client.database.sectionDao().watchSectionsWithCameras()

    private suspend fun update(section: Section): Int =
        client.database.sectionDao().update(section)

    private suspend fun insert(sections: List<Section>): List<Long> =
        client.database.sectionDao().insert(sections)

    private suspend fun deleteAllNotInUIDs(ids: List<Long>) =
        client.database.sectionDao().deleteAllNotInUids(ids)

    private suspend fun configureWeather(section: Section): Result<Unit> = runCatching {

        if (section.latitude == 0.0 || section.longitude == 0.0) {
            error("Invalid coordinates for ${section.title}")
        }

        val response = weatherApi.queryByGeographicCoordinates(
            section.latitude,
            section.longitude,
            BuildConfig.OPEN_WEATHER_API_KEY
        )

        if (!response.isSuccessful) {
            error("HTTP ${response.code()} ${response.message()}")
        }

        val body = response.body() ?: error("Empty body")
        section.weatherUid = body.weather?.firstOrNull()?.id
        section.weatherTemp = body.main?.temp
        update(section)
        Timber.d("Success updating weather for ${section.title}")
    }.onFailure { e ->
        if (e is CancellationException) throw e
        Timber.e(e, "Failed to update weather for ${section.title}")
    }

    // If there is no access to the online content, just load the local one
    private suspend fun loadFromJson(context: Context) {
        Timber.d("Loading local.json")

        // Get sections from DB
        val sections = getSections()

        if (sections.isEmpty()) {
            getSectionsFromAssets(context)?.also {
                insertSectionsAndWebcams(it)
            }
        } else {
            updateSectionsWeather(sections)
        }
    }

    // The function that load data from .json
    private fun getSectionsFromAssets(context: Context): SectionList? {
        val inputStream = context.assets.open("aw-config.json")
        val gson = GsonBuilder().create()
        val jsonReader = JsonParser.parseReader(InputStreamReader(inputStream))
        return gson.fromJson(jsonReader, SectionList::class.java)
    }
}