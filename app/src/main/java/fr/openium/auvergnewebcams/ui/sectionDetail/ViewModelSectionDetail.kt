package fr.openium.auvergnewebcams.ui.sectionDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ViewModelSectionDetail : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    private val sectionRepository by inject<SectionRepository>()

    val prefUtils: PreferencesUtils by inject()
    val imageLoader by inject<ImageLoader>()

    fun loadSectionAndWebcams(sectionId: Long) {
        viewModelScope.launch {
            _state.value = State.Loading
            val sectionWithCameras = sectionRepository.getSectionWithCameras(sectionId)
            _state.value = State.Loaded(sectionWithCameras.section, sectionWithCameras.webcams)
        }
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val section: Section, val webcams: List<Webcam>) : State
    }


}