package fr.openium.auvergnewebcams.ui.webcamDetail

import android.app.Application
import androidx.lifecycle.viewModelScope
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.enums.WebcamType
import fr.openium.auvergnewebcams.ext.jsonKey
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject


class ViewModelWebcamDetail(app: Application) : AbstractViewModel(app), KoinComponent {

    private val webcamRepository by inject<WebcamRepository>()

    val webcamState: StateFlow<WebcamLoadState>
        get() = _webcamState.asStateFlow()
    private val _webcamState = MutableStateFlow<WebcamLoadState>(WebcamLoadState.Loading)

    fun loadWebcamForId(id: Long) = viewModelScope.launch {
        _webcamState.emit(WebcamLoadState.Loading)
        val webcam = webcamRepository.getWebcamForId(id)
        _webcamState.emit(WebcamLoadState.Loaded(webcam = webcam))
    }

    fun refreshWebCam(webcam: Webcam?) = viewModelScope.launch(Dispatchers.IO) {
        webcam?.let { webcam ->
            if (webcam.type != WebcamType.IMAGE.jsonKey) {
                val isViewSurf = webcam.type == WebcamType.VIEWSURF.jsonKey
                val media = if (isViewSurf) {
                    LoadWebCamUtils.getMediaViewSurf(webcam.viewsurf)
                } else LoadWebCamUtils.getMediaViewVideo(webcam.video)
                webcam.mediaViewSurfLD = media
                webcam.mediaViewSurfHD = media
            }
            webcamRepository.update(webcam)
            loadWebcamForId(webcam.uid)
        }
    }

    sealed class WebcamLoadState {
        object Loading : WebcamLoadState()
        data class Loaded(val webcam: Webcam?) : WebcamLoadState()
    }
}