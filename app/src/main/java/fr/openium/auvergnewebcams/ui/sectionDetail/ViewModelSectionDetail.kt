package fr.openium.auvergnewebcams.ui.sectionDetail

import android.app.Application
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.auvergnewebcams.utils.Optional
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject


class ViewModelSectionDetail(app: Application) : AbstractViewModel(app), KoinComponent {

    private val sectionRepository by inject<SectionRepository>()
    private val webcamRepository by inject<WebcamRepository>()

    fun getSectionSingle(sectionId: Long): Single<Optional<Section>> =
        sectionRepository.getSectionSingle(sectionId)

    fun getWebcamsSingle(sectionId: Long): Single<List<Webcam>> =
        webcamRepository.getWebcamsSingle(sectionId)
}