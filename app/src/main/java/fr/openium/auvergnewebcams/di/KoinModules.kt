package fr.openium.auvergnewebcams.di

import fr.openium.auvergnewebcams.ui.map.MapViewModel
import fr.openium.auvergnewebcams.ui.mapSection.MapSectionViewModel
import fr.openium.auvergnewebcams.ui.sectionDetail.ViewModelSectionDetail
import fr.openium.auvergnewebcams.ui.settings.SettingsViewModel
import fr.openium.auvergnewebcams.ui.splash.ViewModelSplash
import fr.openium.auvergnewebcams.ui.webcamDetail.ViewModelWebcamDetail
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

object KoinModules {

    val vmSplash = module {
        viewModel { ViewModelSplash() }
    }

    val vmSettings = module {
        viewModel { SettingsViewModel() }
    }

    val vmSection = module {
        viewModel { ViewModelSectionDetail() }
    }

    val vmDetails = module {
        viewModel { ViewModelWebcamDetail() }
    }

    val vmMap = module {
        viewModel { MapViewModel() }
    }

    val vmMapSection = module {
        viewModel { MapSectionViewModel() }
    }
}