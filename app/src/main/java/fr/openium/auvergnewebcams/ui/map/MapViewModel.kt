package fr.openium.auvergnewebcams.ui.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import fr.openium.auvergnewebcams.enums.MapStyle
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.ui.navigation.Destination
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MapViewModel(savedStateHandle: SavedStateHandle) : ViewModel(), KoinComponent {

    private val sectionId: Long? = savedStateHandle.toRoute(Destination.Map::class).sectionId
    val sectionTitle: String? = savedStateHandle.toRoute(Destination.Map::class).sectionTitle

    val prefUtils: PreferencesUtils by inject()

    private val sectionRepository by inject<SectionRepository>()

    private val _mapStyle: MutableStateFlow<MapStyle> = MutableStateFlow(prefUtils.mapStyle?.let { MapStyle.from(it) } ?: defaultMapStyle)
    val mapStyle = _mapStyle.asStateFlow()

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state by lazy {
        _state.asStateFlow()
    }

    init {
        viewModelScope.launch {
            val sections: List<SectionWithCameras> = if (sectionId == null) {
                sectionRepository.watchSectionsWithCameras().first()
            } else {
                listOf(sectionRepository.getSectionWithCameras(sectionId))
            }
            _state.emit(State.Loaded(sections))
        }
    }

    fun switchMapStyle(style: MapStyle) {
        _mapStyle.value = style
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val sections: List<SectionWithCameras>) : State
    }

    companion object {
        private val defaultMapStyle = MapStyle.ROADS
    }

}