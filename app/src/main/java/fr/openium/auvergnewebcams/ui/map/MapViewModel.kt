package fr.openium.auvergnewebcams.ui.map

import android.app.Application
import androidx.lifecycle.viewModelScope
import fr.openium.auvergnewebcams.base.AbstractMapViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MapViewModel(app: Application) : AbstractMapViewModel(app) {

    override val sections by lazy {
        sectionRepository.watchSectionsWithCameras()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())
    }
}