package fr.openium.auvergnewebcams.ui.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit


class ViewModelMain(app: Application) : AbstractViewModel(app), KoinComponent {

    private val sectionRepository by inject<SectionRepository>()
    private val webcamRepository by inject<WebcamRepository>()

    var isRefreshing = MutableLiveData<Boolean>()
    var sections = MutableLiveData<List<Section>>()

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

    fun getData() {
        Single.zip(
            getSectionsSingle(),
            getWebcamsSingle(),
            BiFunction { sections: List<Section>, webcams: List<Webcam> ->
                sections.sortedBy { it.order } to webcams.sortedBy { it.order }
            })
            .fromIOToMain()
            .subscribe({ sectionsAndWebcams ->
                sectionsAndWebcams.first.forEach { section ->
                    section.webcams = sectionsAndWebcams.second.filter { it.sectionUid == section.uid }
                }
                setSections(sectionsAndWebcams.first)
            }, {
                Timber.e(it, "Error when getting sections and webcams")
            }).addTo(disposables)
    }

    private fun getSectionsSingle(): Single<List<Section>> =
        sectionRepository.getSectionsSingle()

    private fun getWebcamsSingle(): Single<List<Webcam>> =
        webcamRepository.getWebcamsSingle()

    fun setRefreshing(refresh: Boolean) {
        isRefreshing.postValue(refresh)
    }

    fun setSections(sectionsList: List<Section>) {
        sections.postValue(sectionsList)
    }
}