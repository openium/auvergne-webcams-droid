package fr.openium.auvergnewebcams.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class ViewModelMain : ViewModel(), KoinComponent {

    private val sectionRepository by inject<SectionRepository>()
    val imageLoader by inject<ImageLoader>()
    private val prefUtils by inject<PreferencesUtils>()

    private val sectionsFlow: Flow<List<SectionWithCameras>> =
        sectionRepository.watchSectionsWithCameras()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val canBeHDFlow: Flow<Boolean> =
        flowOf(prefUtils.isWebcamsHighQuality)

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                sectionsFlow,
                isRefreshing,
                canBeHDFlow
            ) { sections, refreshing, canHd ->
                State.Loaded(
                    sections = sections,
                    isRefreshing = refreshing,
                    canBeHD = canHd
                )
            }.collect(_state::emit)
        }
    }


    fun updateData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            sectionRepository.fetch()
            _isRefreshing.value = false
        }
    }

    sealed interface State {
        object Loading : State
        data class Loaded(
            val sections: List<SectionWithCameras>,
            val isRefreshing: Boolean,
            val canBeHD: Boolean,
        ) : State
    }

}