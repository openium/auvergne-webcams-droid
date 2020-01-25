package fr.openium.auvergnewebcams.rest

import android.content.Context
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.auvergnewebcams.rest.model.SectionList
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import io.reactivex.Single
import timber.log.Timber

/**
 * Created by Openium on 19/02/2019.
 */
class ApiHelper(val context: Context, val api: AWApi) {

    companion object {
        private const val TAG = "[AW]"
    }

    /**
     * Method that show a generic log when error is unknown
     */
    private fun showSingleErrorLog(functionName: String, error: Throwable?) {
        Timber.e(error, "$TAG -> $functionName")
    }

    // --- WS

    fun getSections(
        sectionRepo: SectionRepository,
        webcamRepo: WebcamRepository
    ): Single<SectionList> {
        return api.getSections()
            .doOnSuccess { sectionsList ->

                Timber.d("Sections count ${sectionsList.sections.count()}")

                for (section in sectionsList.sections) {

                    for (webcam in section.webcams) {

                        webcam.populateId(section.uid)

                        if (webcam.type == Webcam.WebcamType.VIEWSURF.nameType) {
                            webcam.mediaViewSurfLD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfLD)
                            webcam.mediaViewSurfHD = LoadWebCamUtils.getMediaViewSurf(webcam.viewsurfHD)
                        }

                        // Try to get the webcam to know if it's already in DB
                        val webcamDB = webcamRepo.getWebcam(webcam.uid)

                        webcamDB?.also { wdb ->
                            wdb.lastUpdate?.also { webcam.lastUpdate = it }
                            webcam.isFavoris = wdb.isFavoris
                        }

                        if (webcam.title?.contains("Guéry") == true) {
                            Timber.d("TEST webcam hidden ${webcam.hidden}")
                            Timber.d("TEST webcam hidden ${webcam.hidden}")
                        }

                        if (webcam.hidden == null) {
                            webcam.hidden = false
                        }
                    }

                    val webcamsSectionFiltered = section.webcams.filter { it.hidden == false }
                    val rowsWebcam = webcamRepo.insert(webcamsSectionFiltered)
                    Timber.d("${rowsWebcam.count()} webcams inserted for section ${section.title} | ${section.webcams.count() - webcamsSectionFiltered.count()} are hidden")
                    webcamRepo.deleteAllNoMoreInSection(webcamsSectionFiltered.map { it.uid }, section.uid)
                }

                val rowsSection = sectionRepo.insert(sectionsList.sections)
                Timber.d("${rowsSection.count()} section inserted")
                sectionRepo.deleteAllNotInUIDs(sectionsList.sections.map { it.uid })

                Timber.d("$TAG: Sections inserted")
            }.doOnError {
                showSingleErrorLog("getSections", it)
            }
    }
}