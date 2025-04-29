package fr.openium.auvergnewebcams.di

import fr.openium.auvergnewebcams.ui.main.ViewModelMain
import fr.openium.auvergnewebcams.ui.map.MapViewModel
import fr.openium.auvergnewebcams.ui.search.SearchViewModel
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
        viewModel { ViewModelWebcamDetail(get()) }
    }

    val vmMap = module {
        viewModel { MapViewModel(get()) }
    }

    val vmMain = module {
        viewModel { ViewModelMain() }
    }

    val vmSearch = module {
        viewModel { SearchViewModel() }
    }
}