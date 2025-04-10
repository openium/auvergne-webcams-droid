package fr.openium.auvergnewebcams.di

import fr.openium.auvergnewebcams.ui.sectionDetail.ViewModelSectionDetail
import fr.openium.auvergnewebcams.ui.settings.SettingsViewModel
import fr.openium.auvergnewebcams.ui.splash.ViewModelSplash
import fr.openium.auvergnewebcams.ui.webcamDetail.ViewModelWebcamDetail

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

object KoinModules {

    val vmSplash = module {
        viewModelOf(::ViewModelSplash)
    }

    val vmSettings = module {
        viewModelOf(::SettingsViewModel)
    }

    val vmSection = module {
        viewModelOf(::ViewModelSectionDetail)
    }

    val vmDetails = module {
        viewModelOf(::ViewModelWebcamDetail)
    }


}