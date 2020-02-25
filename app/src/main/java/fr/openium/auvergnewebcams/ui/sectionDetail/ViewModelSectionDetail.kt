package fr.openium.auvergnewebcams.ui.sectionDetail

import android.app.Application
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.auvergnewebcams.utils.Optional
import io.reactivex.Single
import org.kodein.di.generic.instance


class ViewModelSectionDetail(app: Application) : AbstractViewModel(app) {

    private val sectionRepository: SectionRepository by instance()
    private val webcamRepository: WebcamRepository by instance()

    fun getSectionSingle(sectionId: Long): Single<Optional<Section>> = sectionRepository.getSectionSingle(sectionId)
    fun getWebcamsSingle(sectionId: Long): Single<List<Webcam>> = webcamRepository.getWebcamsSingle(sectionId)
}