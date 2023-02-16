package fr.openium.auvergnewebcams.ui.sectionDetail

import android.app.Application
import androidx.lifecycle.viewModelScope
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.repository.WebcamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject


class ViewModelSectionDetail(app: Application) : AbstractViewModel(app), KoinComponent {

    private val sectionRepository by inject<SectionRepository>()

    private val sectionId: StateFlow<Long?>
        get() = _sectionId.asStateFlow()
    private val _sectionId = MutableStateFlow<Long?>(null)

    fun setSectionId(id: Long) = viewModelScope.launch {
        _sectionId.emit(id)
    }

    val sectionAndWebcams by lazy {
        sectionId.filterNotNull()
            .flatMapConcat { id ->
                sectionRepository.watchSectionWithCameras(id = id)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), null)
    }
}