package fr.openium.auvergnewebcams.ui.map

import android.app.Application
import androidx.lifecycle.viewModelScope
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.repository.SectionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.core.KoinComponent
import org.koin.core.inject

class MapViewModel(app: Application) : AbstractViewModel(app), KoinComponent {
    private val sectionRepository by inject<SectionRepository>()

    val sections by lazy {
        sectionRepository.watchSectionsWithCameras()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())
    }
}