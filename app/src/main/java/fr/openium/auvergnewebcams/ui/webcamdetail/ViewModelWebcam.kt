package fr.openium.auvergnewebcams.ui.webcamdetail

import android.app.Application
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.auvergnewebcams.utils.Optional
import io.reactivex.Observable
import org.kodein.di.generic.instance


class ViewModelWebcam(app: Application) : AbstractViewModel(app) {

    private val webcamRepository: WebcamRepository by instance()

    fun getWebcamObs(webcamId: Long): Observable<Optional<Webcam>> {
        return webcamRepository.getWebcamObs(webcamId)
    }

    fun updateWebcam(webcam: Webcam): Int {
        return webcamRepository.update(webcam)
    }
}