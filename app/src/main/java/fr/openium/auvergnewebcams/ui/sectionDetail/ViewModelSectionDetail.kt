package fr.openium.auvergnewebcams.ui.sectionDetail

import android.app.Application
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.auvergnewebcams.utils.Optional
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import io.reactivex.Single
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ViewModelSectionDetail(app: Application) : AbstractViewModel(app), KoinComponent {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    private val sectionRepository by inject<SectionRepository>()
    private val webcamRepository by inject<WebcamRepository>()

    fun getSectionSingle(sectionId: Long): Single<Optional<Section>> =
        sectionRepository.getSectionSingle(sectionId)

    fun getWebcamsSingle(sectionId: Long): Single<List<Webcam>> =
        webcamRepository.getWebcamsSingle(sectionId)

    val prefUtils: PreferencesUtils by inject()

    val imageLoader by inject<ImageLoader>()

    fun loadSectionAndWebcams(sectionId: Long) {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                sectionRepository.getSectionWithCameras(sectionId)
                    .collect { sectionWithCamerasOptional: Optional<SectionWithCameras> ->
                        val sectionWithCameras = sectionWithCamerasOptional.value
                            ?: throw IllegalStateException("Section not found")
                        _state.value = State.Loaded(sectionWithCameras.section, sectionWithCameras.webcams)
                    }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    sealed interface State {
        object Loading : State
        data class Loaded(val section: Section, val webcams: List<Webcam>) : State
    }


}