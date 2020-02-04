package fr.openium.auvergnewebcams.ui.webcamdetail

import android.app.Application
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.auvergnewebcams.utils.Optional
import io.reactivex.Single
import org.kodein.di.generic.instance


class ViewModelWebcam(app: Application) : AbstractViewModel(app) {

    private val webcamRepository: WebcamRepository by instance()

    fun getWebcamSingle(webcamId: Long): Single<Optional<Webcam>> = webcamRepository.getWebcamSingle(webcamId)

    fun updateWebcam(webcam: Webcam): Int = webcamRepository.update(webcam)
}