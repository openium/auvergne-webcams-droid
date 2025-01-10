package fr.openium.auvergnewebcams.ui.mapSection

import android.app.Application
import fr.openium.auvergnewebcams.base.AbstractMapViewModel
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapSectionViewModel(app: Application) : AbstractMapViewModel(app) {

    override val sections: StateFlow<List<SectionWithCameras>> = MutableStateFlow(
        emptyList()
    )
}