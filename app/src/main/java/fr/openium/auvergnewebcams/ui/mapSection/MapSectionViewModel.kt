package fr.openium.auvergnewebcams.ui.mapSection

import android.app.Application
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.utils.Optional
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject

class MapSectionViewModel(app: Application) : AbstractViewModel(app), KoinComponent {
    private val sectionRepository by inject<SectionRepository>()

    fun getSectionWithCameras(sectionId: Long): Single<Optional<SectionWithCameras>> =
        sectionRepository.watchSectionWithCameras(sectionId)
}