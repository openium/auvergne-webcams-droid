package fr.openium.auvergnewebcams.base

import androidx.lifecycle.ViewModel
import fr.openium.auvergnewebcams.enums.MapStyle
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.utils.Optional
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import io.reactivex.Single
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class AbstractMapViewModel : ViewModel(), KoinComponent {
    private val defaultMapStyle = MapStyle.ROADS

    private val _mapStyle: MutableStateFlow<MapStyle> = MutableStateFlow(defaultMapStyle)
    val mapStyle = _mapStyle.asStateFlow()

    val prefUtils: PreferencesUtils by inject()

    protected val sectionRepository by inject<SectionRepository>()

    abstract val sections: StateFlow<List<SectionWithCameras>>

    fun switchMapStyle(style: String) {
        _mapStyle.value = MapStyle.from(style) ?: defaultMapStyle
    }

    fun switchMapStyle(mapStyle: MapStyle) {
        _mapStyle.value = mapStyle
    }

    fun getSectionWithCameras(sectionId: Long): Single<Optional<SectionWithCameras>> =
        sectionRepository.watchSectionWithCameras(sectionId)
}