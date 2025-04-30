package fr.openium.auvergnewebcams.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.openium.auvergnewebcams.repository.SectionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ViewModelSplash : ViewModel(), KoinComponent {

    private val sectionRepository by inject<SectionRepository>()

    companion object {
        const val MINIMUM_SECONDS_TO_WAIT = 500L
    }

    // Update all the data the app needs
    fun updateData(onUpdateSuccess: () -> Unit) =
        viewModelScope.launch {
            sectionRepository.fetch()
            delay(MINIMUM_SECONDS_TO_WAIT)
            onUpdateSuccess()
        }


}