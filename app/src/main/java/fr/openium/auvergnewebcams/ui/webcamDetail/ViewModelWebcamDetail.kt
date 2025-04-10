package fr.openium.auvergnewebcams.ui.webcamDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.Optional
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import io.reactivex.Single
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber


class ViewModelWebcamDetail : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    private val webcamRepository by inject<WebcamRepository>()
    val prefUtils: PreferencesUtils by inject()
    val dateUtils: DateUtils by inject()

    fun loadWebcam(webcamId: Long) {
        viewModelScope.launch {
            _state.value = State.Loading
            webcamRepository.getWebcamFlow(webcamId)
                .map { optionalWebcam -> optionalWebcam.value }
                .catch { e ->
                    Timber.e(e)
                }
                .collect { webcam ->
                    if (webcam != null) {
                        _state.value = State.Loaded(webcam)

                    }
                }
        }
    }

    fun getWebcamSingle(webcamId: Long): Single<Optional<Webcam>> =
        webcamRepository.getWebcamSingle(webcamId)

    fun updateWebcam(webcam: Webcam): Int =
        webcamRepository.update(webcam)

    sealed interface State {
        object Loading : State
        data class Loaded(val webcam: Webcam) : State
    }
}