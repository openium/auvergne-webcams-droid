package fr.openium.auvergnewebcams.ui.webcamDetail

import android.app.Application
import androidx.lifecycle.viewModelScope
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.enums.WebcamType
import fr.openium.auvergnewebcams.ext.jsonKey
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import kotlin.random.Random


class ViewModelWebcamDetail(app: Application) : AbstractViewModel(app), KoinComponent {

    private val webcamRepository by inject<WebcamRepository>()

    private val _webcamId = MutableStateFlow<Long?>(null)

    val webcam by lazy {
        _webcamId.filterNotNull()
            .flatMapLatest { id ->
                webcamRepository.watchWebcamForId(webcamId = id)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), null)
    }

    fun setWebcamId(id: Long) = viewModelScope.launch {
        _webcamId.emit(id)
    }

    fun refreshWebCam() = viewModelScope.launch(Dispatchers.IO) {
        webcam.value?.let { webcam ->
            if (webcam.type != WebcamType.IMAGE.jsonKey) {
                val isViewSurf = webcam.type == WebcamType.VIEWSURF.jsonKey
                val media = if (isViewSurf) {
                    LoadWebCamUtils.getMediaViewSurf(webcam.viewsurf)
                } else LoadWebCamUtils.getMediaViewVideo(webcam.video)
                webcam.mediaViewSurfLD = media
                webcam.mediaViewSurfHD = media
            }
            webcam.title = Random.nextInt().toString()
            val update = webcamRepository.update(webcam)
            Timber.d("UPDATE $update - ${webcam.title}")
        }
    }
}