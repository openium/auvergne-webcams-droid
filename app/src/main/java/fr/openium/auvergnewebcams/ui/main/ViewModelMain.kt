package fr.openium.auvergnewebcams.ui.main

import android.app.Application
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit


class ViewModelMain(app: Application) : AbstractViewModel(app), KoinComponent {

    private val sectionRepository by inject<SectionRepository>()
    private val webcamRepository by inject<WebcamRepository>()

    companion object {
        const val MINIMUM_SECONDS_TO_WAIT = 2L
    }

    // Update all the data the app needs
    fun updateData(): Completable =
        Completable.timer(MINIMUM_SECONDS_TO_WAIT, TimeUnit.SECONDS).mergeWith(
            sectionRepository.fetch().doOnSuccess {
                Timber.d("Loading from network: OK")
            }.doOnError {
                Timber.e(it, "Loading from network: KO")
            }.ignoreElement()
        ).fromIOToMain()

    fun getSectionsSingle(): Single<List<Section>> =
        sectionRepository.getSectionsSingle()

    fun getWebcamsSingle(): Single<List<Webcam>> =
        webcamRepository.getWebcamsSingle()
}