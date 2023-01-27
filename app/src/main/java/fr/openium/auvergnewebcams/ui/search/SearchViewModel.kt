package fr.openium.auvergnewebcams.ui.search

import android.app.Application
import androidx.lifecycle.viewModelScope
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.repository.WebcamRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class SearchViewModel(app: Application) : AbstractViewModel(app), KoinComponent {

    private val webcamRepository by inject<WebcamRepository>()

    private val currentSearch: StateFlow<String>
        get() = _currentSearch.asStateFlow()
    private val _currentSearch = MutableStateFlow("")

    val webcams by lazy {
        currentSearch.debounce {
            if (it.isBlank()) {
                0L
            } else {
                400L
            }
        }.combine(
            webcamRepository.watchAllWebcams()
        ) { currentSearch: String, webcams: List<Webcam> ->
            webcams.filter { currentSearch.isNotBlank() && it.title?.contains(currentSearch, true) == true }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())
    }

    fun onNewSearch(search: String): Job =
        viewModelScope.launch {
            _currentSearch.emit(search)
        }
}