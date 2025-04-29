package fr.openium.auvergnewebcams.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class ViewModelMain : ViewModel(), KoinComponent {

    private val sectionRepository by inject<SectionRepository>()

    val isRefreshing = MutableLiveData<Boolean>()

    val imageLoader by inject<ImageLoader>()

    val prefUtils: PreferencesUtils by inject()

    val sections by lazy {
        sectionRepository.watchSectionsWithCameras()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())
    }

    // Update all the data the app needs
    fun updateData() {
        viewModelScope.launch {
            isRefreshing.postValue(true)
            sectionRepository.fetch()
            isRefreshing.postValue(false)
        }
    }

}