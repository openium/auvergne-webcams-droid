package fr.openium.auvergnewebcams.ui.map

import androidx.lifecycle.viewModelScope
import fr.openium.auvergnewebcams.base.AbstractMapViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MapViewModel : AbstractMapViewModel() {

    override val sections by lazy {
        sectionRepository.watchSectionsWithCameras()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())
    }
}