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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
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
            val sectionsFlow: Flow<List<SectionWithCameras>> =
                if (sectionId == null)
                    sectionRepository.watchSectionsWithCameras()
                else
                    flowOf(listOf(sectionRepository.getSectionWithCameras(sectionId)))

            val canBeHDFlow: Flow<Boolean> =
                flowOf(prefUtils.isWebcamsHighQuality)

            combine(
                _mapStyle,
                canBeHDFlow,
                sectionsFlow
            ) { style, canHd, sections ->
                State.Loaded(
                    sections = sections,
                    mapStyle = style,
                    canBeHD = canHd
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun switchMapStyle(style: MapStyle) {
        _mapStyle.value = style
    }

    sealed interface State {
        object Loading : State
        data class Loaded(
            val sections: List<SectionWithCameras>,
            val mapStyle: MapStyle,
            val canBeHD: Boolean
        ) : State
    }

    companion object {
        private val defaultMapStyle = MapStyle.ROADS
    }

}