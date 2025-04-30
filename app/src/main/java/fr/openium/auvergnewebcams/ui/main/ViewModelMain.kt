package fr.openium.auvergnewebcams.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class ViewModelMain : ViewModel(), KoinComponent {

    private val sectionRepository by inject<SectionRepository>()


    val imageLoader by inject<ImageLoader>()

    val prefUtils: PreferencesUtils by inject()

    private val _sections = MutableStateFlow<List<SectionWithCameras>>(emptyList())
    val sections: StateFlow<List<SectionWithCameras>> = _sections.asStateFlow()

    init {
        viewModelScope.launch {
            sectionRepository
                .watchSectionsWithCameras()
                .collect { list ->
                    _sections.value = list
                }
        }
    }

    // Update all the data the app needs
    val isRefreshing = MutableLiveData<Boolean>()

    fun updateData() {
        viewModelScope.launch {
            isRefreshing.postValue(true)
            sectionRepository.fetch()
            isRefreshing.postValue(false)
        }
    }

}